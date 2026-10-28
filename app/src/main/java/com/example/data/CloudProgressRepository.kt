package com.example.data

import android.content.Context
import com.example.R
import com.example.model.CloudLearnedPhrase
import com.example.model.CloudUserProgress
import com.example.model.PhraseItem
import com.example.model.UserStats
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CloudProgressRepository(private val db: FirebaseFirestore) {

    // CRITICAL: Always initialize with R.string.firestore_database_id
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProgress(userId: String): Flow<CloudUserProgress?> {
        val docRef = db.collection("users").document(userId)
        return docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(CloudUserProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.GET, docRef.path)
                }
                throw error
            }
    }

    fun observeLearnedPhrases(userId: String): Flow<List<CloudLearnedPhrase>> {
        val path = "users/$userId/learned_phrases"
        return db.collection("users").document(userId)
            .collection("learned_phrases")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(CloudLearnedPhrase::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun getUserProgress(userId: String): Result<CloudUserProgress?> {
        val docRef = db.collection("users").document(userId)
        return try {
            val snapshot = docRef.get(com.google.firebase.firestore.Source.SERVER).await()
            val progress = if (snapshot.exists()) {
                snapshot.toObject(CloudUserProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else {
                null
            }
            Result.success(progress)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun saveOrUpdateProgress(
        displayName: String,
        stats: UserStats,
        activeScreen: String,
        activeDeckId: String,
        activeCardIndex: Int,
        activeQuizIndex: Int,
        activeScenarioId: String,
        lastDevice: String = "Android"
    ): Result<String> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val safeName = displayName.trim().ifBlank { "Adoor Learner" }.take(100)

        return try {
            val existingSnap = docRef.get().await()
            if (!existingSnap.exists()) {
                val createPayload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "streakDays" to stats.streakDays.coerceIn(0, 10000),
                    "completedQuizCount" to stats.completedQuizCount.coerceIn(0, 100000),
                    "spokenPhrasesCount" to stats.spokenPhrasesCount.coerceIn(0, 100000),
                    "activeScreen" to activeScreen.take(40),
                    "activeDeckId" to activeDeckId.take(64),
                    "activeCardIndex" to activeCardIndex.coerceIn(0, 500),
                    "activeQuizIndex" to activeQuizIndex.coerceIn(0, 500),
                    "activeScenarioId" to activeScenarioId.take(64),
                    "lastDevice" to lastDevice.take(64),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            } else {
                val updatePayload = mapOf(
                    "displayName" to safeName,
                    "streakDays" to stats.streakDays.coerceIn(0, 10000),
                    "completedQuizCount" to stats.completedQuizCount.coerceIn(0, 100000),
                    "spokenPhrasesCount" to stats.spokenPhrasesCount.coerceIn(0, 100000),
                    "activeScreen" to activeScreen.take(40),
                    "activeDeckId" to activeDeckId.take(64),
                    "activeCardIndex" to activeCardIndex.coerceIn(0, 500),
                    "activeQuizIndex" to activeQuizIndex.coerceIn(0, 500),
                    "activeScenarioId" to activeScenarioId.take(64),
                    "lastDevice" to lastDevice.take(64),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            }

            // Also sync full progress snapshot to users/{uid}/progress/current
            try {
                val progressDoc = docRef.collection("progress").document("current")
                val fullProgressPayload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "streakDays" to stats.streakDays.coerceIn(0, 10000),
                    "completedQuizCount" to stats.completedQuizCount.coerceIn(0, 100000),
                    "spokenPhrasesCount" to stats.spokenPhrasesCount.coerceIn(0, 100000),
                    "learnedPhraseIds" to stats.learnedPhraseIds.toList(),
                    "graduatedTiers" to stats.graduatedTiers.toList(),
                    "activeScreen" to activeScreen.take(40),
                    "activeDeckId" to activeDeckId.take(64),
                    "activeCardIndex" to activeCardIndex.coerceIn(0, 500),
                    "activeQuizIndex" to activeQuizIndex.coerceIn(0, 500),
                    "lastDevice" to lastDevice.take(64),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                progressDoc.set(fullProgressPayload, com.google.firebase.firestore.SetOptions.merge()).await()
            } catch (_: Exception) {}

            Result.success(uid)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun saveLearnedPhrase(phrase: PhraseItem, masteryLevel: Int = 3): Result<String> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("learned_phrases").document(phrase.id)

        return try {
            val existingSnap = docRef.get().await()
            if (!existingSnap.exists()) {
                val payload = mapOf(
                    "phraseId" to phrase.id.take(64),
                    "userId" to uid,
                    "manglish" to phrase.manglish.take(300),
                    "category" to phrase.category.take(64),
                    "masteryLevel" to masteryLevel.coerceIn(1, 5),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                val updatePayload = mapOf(
                    "manglish" to phrase.manglish.take(300),
                    "category" to phrase.category.take(64),
                    "masteryLevel" to masteryLevel.coerceIn(1, 5),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            }
            Result.success(phrase.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
