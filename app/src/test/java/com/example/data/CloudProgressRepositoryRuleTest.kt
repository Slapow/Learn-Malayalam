package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.model.UserStats
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudProgressRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndGetProgress_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = CloudProgressRepository(firestore)

        val stats = UserStats(streakDays = 5, completedQuizCount = 8, spokenPhrasesCount = 14)
        val saveRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.saveOrUpdateProgress(
                displayName = "Alice Adoor",
                stats = stats,
                activeScreen = "FLASHCARDS",
                activeDeckId = "deck_family",
                activeCardIndex = 2,
                activeQuizIndex = 3,
                activeScenarioId = "scenario_ammachi",
                lastDevice = "Android"
            )
        }
        assertTrue(saveRes.isSuccess)

        val fetchedRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.getUserProgress(uid)
        }
        assertTrue(fetchedRes.isSuccess)
        val progress = fetchedRes.getOrThrow()
        assertNotNull(progress)
        assertEquals("FLASHCARDS", progress?.activeScreen)
        assertEquals("deck_family", progress?.activeDeckId)
    }

    @Test
    fun saveLearnedPhrase_andObserve_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = CloudProgressRepository(firestore)
        val phrase = DialectRepository.allPhrases[0]

        val saveRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.saveLearnedPhrase(phrase, masteryLevel = 4)
        }
        assertTrue(saveRes.isSuccess)

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeLearnedPhrases(uid).first { list -> list.any { it.phraseId == phrase.id } }
        }
        assertTrue(emitted.any { it.phraseId == phrase.id && it.masteryLevel == 4L })
    }

    @Test
    fun getUserProgress_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = CloudProgressRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveOrUpdateProgress(
                displayName = "Alice",
                stats = UserStats(),
                activeScreen = "HOME",
                activeDeckId = "deck_contractions",
                activeCardIndex = 0,
                activeQuizIndex = 0,
                activeScenarioId = "scenario_ammachi"
            )
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = CloudProgressRepository(firestore)
        val crossReadResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getUserProgress(aliceUid)
        }
        assertTrue(crossReadResult.isFailure)
        val ex = crossReadResult.exceptionOrNull() as? FirebaseFirestoreException
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
    }

    @Test
    fun observeLearnedPhrases_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = CloudProgressRepository(firestore)

        var caughtPermissionDenied = false
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repo.observeLearnedPhrases("unauth_user_123").first()
            }
        } catch (t: Throwable) {
            var current: Throwable? = t
            while (current != null) {
                if (current is FirebaseFirestoreException &&
                    current.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
                ) {
                    caughtPermissionDenied = true
                    break
                }
                current = current.cause
            }
        }
        assertTrue(caughtPermissionDenied)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@test.com"
        const val BOB_EMAIL = "bob@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
