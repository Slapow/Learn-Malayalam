package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CloudLearnedPhrase
import com.example.model.PhraseMasteryRecord
import com.example.model.UserStats
import com.example.model.SpokenCurriculum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class UserStatsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("parayu_stats", Context.MODE_PRIVATE)

    init {
        migrateCurriculumProgress()
    }

    private fun migrateCurriculumProgress() {
        if (prefs.getInt("curriculum_version", 1) >= SpokenCurriculum.VERSION) return
        val drills = prefs.getStringSet("mastered_drills", emptySet()).orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .map { SpokenCurriculum.migrateLegacyIndex(it).toString() }.toSet()
        val rounds = prefs.getStringSet("completed_drill_rounds", emptySet()).orEmpty()
            .mapNotNull { value ->
                val parts = value.split(':')
                val index = parts.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
                val round = parts.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
                if (index !in 0..23 || round !in 1..3) return@mapNotNull null
                "${SpokenCurriculum.migrateLegacyIndex(index)}:$round"
            }.toSet()
        prefs.edit()
            .putStringSet("mastered_drills", drills)
            .putStringSet("completed_drill_rounds", rounds)
            .putInt("unlocked_level", SpokenCurriculum.migrateLegacyIndex(prefs.getInt("unlocked_level", 0)))
            .putInt("curriculum_version", SpokenCurriculum.VERSION)
            .apply()
        // Phrase/deck IDs, mastery records, and earned graduations stay stable.
        // New units remain available for study without being marked complete.
    }

    private val _stats = MutableStateFlow(loadStats())
    val stats: StateFlow<UserStats> = _stats.asStateFlow()

    private fun loadStats(): UserStats {
        val learnedIds = prefs.getStringSet("learned_phrases", emptySet()) ?: emptySet()
        val masteredDecks = prefs.getStringSet("mastered_decks", emptySet()) ?: emptySet()
        val quizCount = prefs.getInt("quiz_count", 0)
        val spokenCount = prefs.getInt("spoken_count", 0)
        val graduatedTiers = prefs.getStringSet("graduated_tiers", emptySet()) ?: emptySet()
        val masteredDrills = prefs.getStringSet("mastered_drills", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        val completedDrillRounds = prefs.getStringSet("completed_drill_rounds", emptySet())
            ?.toMutableSet()
            ?: mutableSetOf()
        masteredDrills.forEach { levelIndex ->
            (1..3).forEach { round -> completedDrillRounds.add("$levelIndex:$round") }
        }
        val highestDrillLevel = if (masteredDrills.isNotEmpty()) (masteredDrills.maxOrNull() ?: 0) + 1 else 0
        val highestDeckLevel = DialectRepository.studyPlanStages
            .filter { it.deckId in masteredDecks }
            .maxOfOrNull { it.levelIndex + 1 } ?: 0
        val savedUnlockedLevel = prefs.getInt("unlocked_level", 0)
        val unlockedLevel = maxOf(savedUnlockedLevel, highestDrillLevel, highestDeckLevel)
            .coerceIn(0, DialectRepository.studyPlanStages.size - 1)
        val masteryRecords = loadMasteryRecords()

        val lastDateStr = prefs.getString("last_practice_date", null)
        var streak = prefs.getInt("user_streak", 0)
        if (lastDateStr != null && streak > 0) {
            val today = LocalDate.now()
            val lastDate = try { LocalDate.parse(lastDateStr) } catch (_: Exception) { null }
            if (lastDate != null) {
                val daysBetween = ChronoUnit.DAYS.between(lastDate, today)
                if (daysBetween > 1L) {
                    streak = 0
                    prefs.edit().putInt("user_streak", 0).apply()
                }
            }
        } else if (lastDateStr == null && streak <= 0) {
            val maxPracticedMs = masteryRecords.values.maxOfOrNull { it.lastPracticedMs } ?: 0L
            if (maxPracticedMs > 0L) {
                val lastDate = Instant.ofEpochMilli(maxPracticedMs).atZone(ZoneId.systemDefault()).toLocalDate()
                val today = LocalDate.now()
                val daysBetween = ChronoUnit.DAYS.between(lastDate, today)
                if (daysBetween <= 1L) {
                    streak = 1
                    prefs.edit().putInt("user_streak", 1).putString("last_practice_date", lastDate.toString()).apply()
                }
            }
        }

        return UserStats(
            streakDays = streak,
            learnedPhraseIds = learnedIds,
            masteredDeckIds = masteredDecks,
            completedQuizCount = quizCount,
            spokenPhrasesCount = spokenCount,
            masteryRecords = masteryRecords,
            graduatedTiers = graduatedTiers,
            masteredDrillLevelIndices = masteredDrills,
            completedDrillRounds = completedDrillRounds,
            unlockedLevelIndex = unlockedLevel
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
                    touchLevel = obj.optInt("touchLevel", 0),
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
                put("touchLevel", rec.touchLevel)
                put("consecutiveCorrect", rec.consecutiveCorrect)
                put("totalCorrect", rec.totalCorrect)
                put("mistakeCount", rec.mistakeCount)
                put("lastPracticedMs", rec.lastPracticedMs)
            }
            root.put(id, obj)
        }
        prefs.edit().putString("mastery_records_json", root.toString()).apply()
    }

    fun recordActivityForStreak(): Int {
        val today = LocalDate.now()
        val lastDateStr = prefs.getString("last_practice_date", null)
        val currentStreak = prefs.getInt("user_streak", 0)

        val newStreak = if (lastDateStr == null) {
            1
        } else {
            val lastDate = try { LocalDate.parse(lastDateStr) } catch (_: Exception) { null }
            if (lastDate == null) {
                1
            } else {
                val daysBetween = ChronoUnit.DAYS.between(lastDate, today)
                when {
                    daysBetween == 0L -> if (currentStreak <= 0) 1 else currentStreak
                    daysBetween == 1L -> currentStreak + 1
                    daysBetween > 1L -> 1
                    else -> currentStreak
                }
            }
        }
        prefs.edit()
            .putString("last_practice_date", today.toString())
            .putInt("user_streak", newStreak)
            .apply()
        _stats.value = _stats.value.copy(streakDays = newStreak)
        return newStreak
    }

    /**
     * Records a right or wrong answer for a specific phrase in Quizlet / Duolingo modes.
     * - Correct: increments consecutiveCorrect; if >= 2, marks as Known/Mastered!
     * - Wrong: resets consecutiveCorrect to 0 and increments mistakeCount for Dynamic Adaptive Review!
     */
    /**
     * Records a phrase attempt with specific Touch level (1, 2, or 3).
     * - Touch 1 (Recognition): MCQ / Audio matching
     * - Touch 2 (Cued Production): Word Tiles / Sentence Arranging
     * - Touch 3 (Free Recall): Direct Typing / Speaking
     *
     * Only correctly completed touches count toward drill progress. A failed touch
     * can lower the progress to the preceding completed touch, but never advances it.
     */
    fun recordPhraseTouch(phraseId: String, touch: Int, isCorrect: Boolean): PhraseMasteryRecord {
        val currentMap = _stats.value.masteryRecords.toMutableMap()
        val existing = currentMap[phraseId] ?: PhraseMasteryRecord(phraseId = phraseId)

        val newTouchLevel = if (isCorrect) {
            existing.touchLevel.coerceAtLeast(touch)
        } else {
            // Only completed rounds count as progress; a failed round cannot add a touch.
            existing.touchLevel.coerceAtMost(touch - 1)
        }

        val updated = existing.copy(
            touchLevel = newTouchLevel,
            consecutiveCorrect = if (isCorrect) existing.consecutiveCorrect + 1 else 0,
            totalCorrect = if (isCorrect) existing.totalCorrect + 1 else existing.totalCorrect,
            mistakeCount = if (!isCorrect) existing.mistakeCount + 1 else existing.mistakeCount,
            lastPracticedMs = System.currentTimeMillis()
        )

        currentMap[phraseId] = updated
        saveMasteryRecords(currentMap)
        recordActivityForStreak()

        val updatedLearnedSet = if (updated.isKnown) {
            _stats.value.learnedPhraseIds + phraseId
        } else {
            _stats.value.learnedPhraseIds - phraseId
        }
        prefs.edit().putStringSet("learned_phrases", updatedLearnedSet).apply()
        _stats.value = _stats.value.copy(
            learnedPhraseIds = updatedLearnedSet,
            masteryRecords = currentMap
        )
        return updated
    }

    fun recordPhraseAttempt(phraseId: String, isCorrect: Boolean): PhraseMasteryRecord {
        return recordPhraseTouch(phraseId, touch = 1, isCorrect = isCorrect)
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
    }

    fun recordDrillCompletedForLevel(levelIndex: Int) {
        val updated = _stats.value.masteredDrillLevelIndices + levelIndex
        val completedRounds = _stats.value.completedDrillRounds.toMutableSet().apply {
            (1..3).forEach { round -> add("$levelIndex:$round") }
        }
        val currentUnlocked = _stats.value.unlockedLevelIndex
        val newUnlocked = if (levelIndex >= currentUnlocked) {
            (levelIndex + 1).coerceAtMost(DialectRepository.studyPlanStages.size - 1)
        } else {
            currentUnlocked
        }
        prefs.edit()
            .putStringSet("mastered_drills", updated.map { it.toString() }.toSet())
            .putStringSet("completed_drill_rounds", completedRounds)
            .putInt("quiz_count", _stats.value.completedQuizCount + 1)
            .putInt("unlocked_level", newUnlocked)
            .apply()
        _stats.value = _stats.value.copy(
            masteredDrillLevelIndices = updated,
            completedDrillRounds = completedRounds,
            completedQuizCount = _stats.value.completedQuizCount + 1,
            unlockedLevelIndex = newUnlocked
        )
    }

    fun recordDrillRoundCompleted(levelIndex: Int, round: Int) {
        if (round !in 1..3) return
        val updated = _stats.value.completedDrillRounds + "$levelIndex:$round"
        prefs.edit().putStringSet("completed_drill_rounds", updated).apply()
        _stats.value = _stats.value.copy(completedDrillRounds = updated)
        recordActivityForStreak()
    }

    fun recordDeckMastered(deckId: String) {
        val updated = _stats.value.masteredDeckIds + deckId
        val deckStage = DialectRepository.studyPlanStages.find { it.deckId == deckId }
        val currentUnlocked = _stats.value.unlockedLevelIndex
        val newUnlocked = if (deckStage != null && deckStage.levelIndex >= currentUnlocked) {
            (deckStage.levelIndex + 1).coerceAtMost(DialectRepository.studyPlanStages.size - 1)
        } else {
            currentUnlocked
        }
        prefs.edit()
            .putStringSet("mastered_decks", updated)
            .putInt("unlocked_level", newUnlocked)
            .apply()
        _stats.value = _stats.value.copy(
            masteredDeckIds = updated,
            unlockedLevelIndex = newUnlocked
        )
    }

    fun setUnlockedLevelIndex(newLevel: Int) {
        prefs.edit().putInt("unlocked_level", newLevel).apply()
        _stats.value = _stats.value.copy(unlockedLevelIndex = newLevel)
    }

    fun incrementSpokenCount() {
        val newCount = _stats.value.spokenPhrasesCount + 1
        prefs.edit().putInt("spoken_count", newCount).apply()
        _stats.value = _stats.value.copy(spokenPhrasesCount = newCount)
    }

    fun setCustomStreak(days: Int) {
        val safeDays = days.coerceAtLeast(0)
        prefs.edit().putInt("user_streak", safeDays).apply()
        _stats.value = _stats.value.copy(streakDays = safeDays)
    }

    /**
     * Records successful passage of an ACTFL Tier Oral Spoken Graduation Exam.
     * Unlocks progression into the next tier.
     */
    fun recordTierGraduation(tierId: String) {
        val updatedTiers = _stats.value.graduatedTiers + tierId
        prefs.edit().putStringSet("graduated_tiers", updatedTiers).apply()
        _stats.value = _stats.value.copy(graduatedTiers = updatedTiers)
    }

    fun resetProgressToZero() {
        prefs.edit()
            .putInt("user_streak", 0)
            .putInt("quiz_count", 0)
            .putInt("spoken_count", 0)
            .putStringSet("learned_phrases", emptySet())
            .putStringSet("mastered_decks", emptySet())
            .putStringSet("mastered_drills", emptySet())
            .putStringSet("completed_drill_rounds", emptySet())
            .putInt("unlocked_level", 0)
            .putStringSet("graduated_tiers", emptySet())
            .remove("mastery_records_json")
            .apply()
        _stats.value = UserStats(
            streakDays = 0,
            learnedPhraseIds = emptySet(),
            masteredDeckIds = emptySet(),
            completedQuizCount = 0,
            spokenPhrasesCount = 0,
            masteryRecords = emptyMap(),
            graduatedTiers = emptySet(),
            masteredDrillLevelIndices = emptySet(),
            completedDrillRounds = emptySet(),
            unlockedLevelIndex = 0
        )
    }

    fun syncFromCloud(streakDays: Int, quizCount: Int, spokenCount: Int) {
        prefs.edit()
            .putInt("user_streak", streakDays)
            .putInt("quiz_count", quizCount)
            .putInt("spoken_count", spokenCount)
            .apply()
        _stats.value = _stats.value.copy(
            streakDays = streakDays,
            completedQuizCount = quizCount,
            spokenPhrasesCount = spokenCount
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

    fun resetDeckProgress(deckId: String, phraseIds: Set<String>) {
        val currentMap = _stats.value.masteryRecords.toMutableMap()
        for (id in phraseIds) {
            currentMap.remove(id)
        }
        val updatedLearned = _stats.value.learnedPhraseIds - phraseIds
        val updatedMasteredDecks = _stats.value.masteredDeckIds - deckId
        saveMasteryRecords(currentMap)
        prefs.edit()
            .putStringSet("learned_phrases", updatedLearned)
            .putStringSet("mastered_decks", updatedMasteredDecks)
            .apply()
        _stats.value = _stats.value.copy(
            learnedPhraseIds = updatedLearned,
            masteredDeckIds = updatedMasteredDecks,
            masteryRecords = currentMap
        )
    }
}
