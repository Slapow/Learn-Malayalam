package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CloudLearnedPhrase
import com.example.model.PhraseMasteryRecord
import com.example.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class UserStatsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("parayu_stats", Context.MODE_PRIVATE)

    init {
        // Clean up any legacy template defaults (140 XP / 4-day streak) so new learners start at 0 XP, 0-day streak
        val zeroMigrated = prefs.getBoolean("zero_baseline_migrated_v2", false)
        if (!zeroMigrated) {
            val oldXp = prefs.getInt("user_xp", 0)
            val oldStreak = prefs.getInt("user_streak", 0)
            val learnedSet = prefs.getStringSet("learned_phrases", emptySet()) ?: emptySet()
            if (oldXp == 140 && oldStreak == 4 && learnedSet.isEmpty()) {
                prefs.edit()
                    .putInt("user_xp", 0)
                    .putInt("user_streak", 0)
                    .putInt("quiz_count", 0)
                    .putInt("spoken_count", 0)
                    .putBoolean("zero_baseline_migrated_v2", true)
                    .apply()
            } else {
                prefs.edit().putBoolean("zero_baseline_migrated_v2", true).apply()
            }
        }
    }

    private val _stats = MutableStateFlow(loadStats())
    val stats: StateFlow<UserStats> = _stats.asStateFlow()

    private fun loadStats(): UserStats {
        val xp = prefs.getInt("user_xp", 0)
        val streak = prefs.getInt("user_streak", 0)
        val learnedIds = prefs.getStringSet("learned_phrases", emptySet()) ?: emptySet()
        val masteredDecks = prefs.getStringSet("mastered_decks", emptySet()) ?: emptySet()
        val quizCount = prefs.getInt("quiz_count", 0)
        val spokenCount = prefs.getInt("spoken_count", 0)
        val graduatedTiers = prefs.getStringSet("graduated_tiers", emptySet()) ?: emptySet()
        val masteryRecords = loadMasteryRecords()

        return UserStats(
            xp = xp,
            streakDays = streak,
            learnedPhraseIds = learnedIds,
            masteredDeckIds = masteredDecks,
            completedQuizCount = quizCount,
            spokenPhrasesCount = spokenCount,
            masteryRecords = masteryRecords,
            graduatedTiers = graduatedTiers
        )
    }

    private fun loadMasteryRecords(): Map<String, PhraseMasteryRecord> {
        val raw = prefs.getString("mastery_records_json", null) ?: return emptyMap()
        val result = mutableMapOf<String, PhraseMasteryRecord>()
        try {
            val root = JSONObject(raw)
            val keys = root.keys()
            while (keys.hasNext()) {
                val id = keys.next()
                val obj = root.optJSONObject(id) ?: continue
                result[id] = PhraseMasteryRecord(
                    phraseId = id,
                    consecutiveCorrect = obj.optInt("consecutiveCorrect", 0),
                    totalCorrect = obj.optInt("totalCorrect", 0),
                    mistakeCount = obj.optInt("mistakeCount", 0),
                    lastPracticedMs = obj.optLong("lastPracticedMs", 0L)
                )
            }
        } catch (_: Exception) {}
        return result
    }

    private fun saveMasteryRecords(records: Map<String, PhraseMasteryRecord>) {
        val root = JSONObject()
        for ((id, rec) in records) {
            val obj = JSONObject().apply {
                put("consecutiveCorrect", rec.consecutiveCorrect)
                put("totalCorrect", rec.totalCorrect)
                put("mistakeCount", rec.mistakeCount)
                put("lastPracticedMs", rec.lastPracticedMs)
            }
            root.put(id, obj)
        }
        prefs.edit().putString("mastery_records_json", root.toString()).apply()
    }

    private fun ensureActiveStreakOnFirstActivity(currentStreak: Int): Int {
        return if (currentStreak <= 0) 1 else currentStreak
    }

    fun addXp(points: Int) {
        val newXp = _stats.value.xp + points
        val newStreak = ensureActiveStreakOnFirstActivity(_stats.value.streakDays)
        prefs.edit()
            .putInt("user_xp", newXp)
            .putInt("user_streak", newStreak)
            .apply()
        _stats.value = _stats.value.copy(xp = newXp, streakDays = newStreak)
    }

    /**
     * Records a right or wrong answer for a specific phrase in Quizlet / Duolingo modes.
     * - Correct: increments consecutiveCorrect; if >= 2, marks as Known/Mastered!
     * - Wrong: resets consecutiveCorrect to 0 and increments mistakeCount for Dynamic Adaptive Review!
     */
    fun recordPhraseAttempt(phraseId: String, isCorrect: Boolean): PhraseMasteryRecord {
        val currentMap = _stats.value.masteryRecords.toMutableMap()
        val existing = currentMap[phraseId] ?: PhraseMasteryRecord(phraseId = phraseId)
        val updated = if (isCorrect) {
            existing.copy(
                consecutiveCorrect = existing.consecutiveCorrect + 1,
                totalCorrect = existing.totalCorrect + 1,
                lastPracticedMs = System.currentTimeMillis()
            )
        } else {
            existing.copy(
                consecutiveCorrect = 0,
                mistakeCount = existing.mistakeCount + 1,
                lastPracticedMs = System.currentTimeMillis()
            )
        }
        currentMap[phraseId] = updated
        saveMasteryRecords(currentMap)

        val updatedLearnedSet = if (updated.isKnown) {
            _stats.value.learnedPhraseIds + phraseId
        } else {
            _stats.value.learnedPhraseIds
        }
        prefs.edit().putStringSet("learned_phrases", updatedLearnedSet).apply()
        _stats.value = _stats.value.copy(
            learnedPhraseIds = updatedLearnedSet,
            masteryRecords = currentMap
        )
        if (isCorrect) {
            addXp(if (updated.consecutiveCorrect == 2) 10 else 5)
        }
        return updated
    }

    fun markPhraseLearned(phraseId: String) {
        val currentMap = _stats.value.masteryRecords.toMutableMap()
        val existing = currentMap[phraseId] ?: PhraseMasteryRecord(phraseId = phraseId)
        val isCurrentlyKnown = _stats.value.learnedPhraseIds.contains(phraseId)
        val newSet = if (isCurrentlyKnown) {
            currentMap[phraseId] = existing.copy(consecutiveCorrect = 0)
            _stats.value.learnedPhraseIds - phraseId
        } else {
            currentMap[phraseId] = existing.copy(
                consecutiveCorrect = 2,
                totalCorrect = existing.totalCorrect.coerceAtLeast(2),
                lastPracticedMs = System.currentTimeMillis()
            )
            addXp(5)
            _stats.value.learnedPhraseIds + phraseId
        }
        saveMasteryRecords(currentMap)
        prefs.edit().putStringSet("learned_phrases", newSet).apply()
        _stats.value = _stats.value.copy(
            learnedPhraseIds = newSet,
            masteryRecords = currentMap
        )
    }

    fun incrementQuizCompleted() {
        val newCount = _stats.value.completedQuizCount + 1
        prefs.edit().putInt("quiz_count", newCount).apply()
        _stats.value = _stats.value.copy(completedQuizCount = newCount)
        addXp(20)
    }

    fun incrementSpokenCount() {
        val newCount = _stats.value.spokenPhrasesCount + 1
        prefs.edit().putInt("spoken_count", newCount).apply()
        _stats.value = _stats.value.copy(spokenPhrasesCount = newCount)
        addXp(10)
    }

    /**
     * Records successful passage of an ACTFL Tier Oral Spoken Graduation Exam.
     * Awards +50 XP and unlocks progression into the next tier.
     */
    fun recordTierGraduation(tierId: String) {
        val updatedTiers = _stats.value.graduatedTiers + tierId
        prefs.edit().putStringSet("graduated_tiers", updatedTiers).apply()
        _stats.value = _stats.value.copy(graduatedTiers = updatedTiers)
        addXp(50)
    }

    fun resetProgressToZero() {
        prefs.edit()
            .putInt("user_xp", 0)
            .putInt("user_streak", 0)
            .putInt("quiz_count", 0)
            .putInt("spoken_count", 0)
            .putStringSet("learned_phrases", emptySet())
            .putStringSet("mastered_decks", emptySet())
            .putStringSet("graduated_tiers", emptySet())
            .remove("mastery_records_json")
            .apply()
        _stats.value = UserStats(
            xp = 0,
            streakDays = 0,
            learnedPhraseIds = emptySet(),
            masteredDeckIds = emptySet(),
            completedQuizCount = 0,
            spokenPhrasesCount = 0,
            masteryRecords = emptyMap(),
            graduatedTiers = emptySet()
        )
    }

    fun syncFromCloud(xp: Int, streakDays: Int, quizCount: Int, spokenCount: Int) {
        // Ignore the old template placeholder (140 XP / 4-day streak with 0 learned phrases)
        val isLegacyPlaceholder = xp == 140 && streakDays == 4 && _stats.value.learnedPhraseIds.isEmpty()
        val effectiveXp = if (isLegacyPlaceholder) 0 else xp
        val effectiveStreak = if (isLegacyPlaceholder) 0 else streakDays
        val effectiveQuiz = if (isLegacyPlaceholder) 0 else quizCount
        val effectiveSpoken = if (isLegacyPlaceholder) 0 else spokenCount

        prefs.edit()
            .putInt("user_xp", effectiveXp)
            .putInt("user_streak", effectiveStreak)
            .putInt("quiz_count", effectiveQuiz)
            .putInt("spoken_count", effectiveSpoken)
            .apply()
        _stats.value = _stats.value.copy(
            xp = effectiveXp,
            streakDays = effectiveStreak,
            completedQuizCount = effectiveQuiz,
            spokenPhrasesCount = effectiveSpoken
        )
    }

    fun syncCloudLearnedPhrases(cloudPhrases: List<CloudLearnedPhrase>) {
        val currentMap = _stats.value.masteryRecords.toMutableMap()
        val learnedIds = _stats.value.learnedPhraseIds.toMutableSet()
        for (cp in cloudPhrases) {
            val existing = currentMap[cp.phraseId] ?: PhraseMasteryRecord(phraseId = cp.phraseId)
            val streakFromLevel = when {
                cp.masteryLevel >= 4L -> 2
                cp.masteryLevel >= 2L -> 1
                else -> 0
            }
            val mistakesFromLevel = if (cp.masteryLevel == 1L) existing.mistakeCount.coerceAtLeast(1) else existing.mistakeCount
            val merged = existing.copy(
                consecutiveCorrect = maxOf(existing.consecutiveCorrect, streakFromLevel),
                mistakeCount = mistakesFromLevel
            )
            currentMap[cp.phraseId] = merged
            if (merged.isKnown) {
                learnedIds.add(cp.phraseId)
            }
        }
        saveMasteryRecords(currentMap)
        prefs.edit().putStringSet("learned_phrases", learnedIds).apply()
        _stats.value = _stats.value.copy(
            learnedPhraseIds = learnedIds,
            masteryRecords = currentMap
        )
    }

    fun syncLearnedPhraseIds(ids: Set<String>) {
        val merged = _stats.value.learnedPhraseIds + ids
        prefs.edit().putStringSet("learned_phrases", merged).apply()
        _stats.value = _stats.value.copy(learnedPhraseIds = merged)
    }
}
