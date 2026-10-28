package com.example.data

import com.example.model.DrillItem
import com.example.model.PhraseItem
import com.example.model.SpokenCurriculum
import com.example.model.UserStats

data class DailyDrillDeck(
    val reviewItems: List<DrillItem>,
    val newItems: List<DrillItem>
)

class DrillItemRepository(private val dao: DrillItemDao) {
    suspend fun seedCurriculumIfNeeded() {
        val items = DialectRepository.allPhrases.map { phrase ->
            val level = SpokenCurriculum.levels[phrase.levelNumber]
            DrillItem(
                id = phrase.id,
                tier = level.tier,
                levelId = level.levelId,
                levelNumber = level.levelNumber,
                malayalam = phrase.malayalam,
                manglish = phrase.manglish,
                english = phrase.english,
                spokenExampleMalayalam = phrase.spokenExampleMalayalam,
                spokenExampleManglish = phrase.spokenExampleManglish,
                spokenExampleEnglish = phrase.spokenExampleEnglish
            )
        }
        dao.syncSeedItems(items)
    }

    suspend fun getDailyDeck(
        currentLevelIndex: Int,
        stats: UserStats,
        currentTime: Long = System.currentTimeMillis()
    ): DailyDrillDeck {
        val currentLevel = SpokenCurriculum.levels.getOrNull(currentLevelIndex)
            ?: SpokenCurriculum.levels.first()
        val currentItems = dao.getItemsForLevel(currentLevel.levelId)
        val completedPreviousLevelIds = SpokenCurriculum.levels
            .take(currentLevelIndex.coerceAtLeast(0))
            .filter { level ->
                val levelIndex = SpokenCurriculum.levelIndex(level.levelId)
                (1..3).all { round -> "$levelIndex:$round" in stats.completedDrillRounds }
            }
            .map { it.levelId }
            .toSet()
        val dueItems = if (completedPreviousLevelIds.isEmpty()) {
            emptyList()
        } else {
            dao.getDueReviewItemsForLevels(currentTime, completedPreviousLevelIds.toList())
        }

        return DailyDrillDeck(reviewItems = dueItems, newItems = currentItems)
    }

    suspend fun recordReview(itemId: String, wasCorrect: Boolean, currentTime: Long = System.currentTimeMillis()) {
        dao.updateSpacedRepetition(itemId, wasCorrect, currentTime)
    }

    fun toReviewPhrase(item: DrillItem): PhraseItem {
        val sentenceManglish = item.spokenExampleManglish.ifBlank { item.manglish }
        val sentenceMalayalam = item.spokenExampleMalayalam.ifBlank { item.malayalam }
        val sentenceEnglish = item.spokenExampleEnglish.ifBlank { item.english }
        return PhraseItem(
            id = item.id,
            manglish = sentenceManglish,
            malayalam = sentenceMalayalam,
            english = sentenceEnglish,
            category = "${item.tier.shortTitle} Level ${item.levelNumber}",
            levelNumber = SpokenCurriculum.levelIndex(item.levelId),
            levelBadge = "${item.tier.shortTitle} Level ${item.levelNumber}",
            audioUtterance = sentenceMalayalam
        )
    }
}
