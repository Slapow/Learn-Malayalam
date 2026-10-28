package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.model.ActflProficiencyTier
import com.example.model.DrillItem

@Dao
interface DrillItemDao {
    @Query(
        "SELECT * FROM drill_items " +
            "WHERE nextReviewTimestamp <= :currentTime " +
            "ORDER BY nextReviewTimestamp ASC LIMIT 15"
    )
    suspend fun getDueReviewItems(currentTime: Long): List<DrillItem>

    @Query(
        "SELECT * FROM drill_items " +
            "WHERE nextReviewTimestamp <= :currentTime AND levelId IN (:completedLevelIds) " +
            "ORDER BY nextReviewTimestamp ASC LIMIT 15"
    )
    suspend fun getDueReviewItemsForLevels(currentTime: Long, completedLevelIds: List<String>): List<DrillItem>

    @Query("SELECT * FROM drill_items WHERE levelId = :levelId ORDER BY id ASC")
    suspend fun getItemsForLevel(levelId: String): List<DrillItem>

    @Query("SELECT COUNT(*) FROM drill_items")
    suspend fun countItems(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<DrillItem>)

    @Query(
        """
        UPDATE drill_items SET
            tier = :tier,
            levelId = :levelId,
            levelNumber = :levelNumber,
            malayalam = :malayalam,
            manglish = :manglish,
            english = :english,
            spokenExampleMalayalam = :spokenExampleMalayalam,
            spokenExampleManglish = :spokenExampleManglish,
            spokenExampleEnglish = :spokenExampleEnglish
        WHERE id = :itemId
        """
    )
    suspend fun updateSeedContent(
        itemId: String,
        tier: ActflProficiencyTier,
        levelId: String,
        levelNumber: Int,
        malayalam: String,
        manglish: String,
        english: String,
        spokenExampleMalayalam: String,
        spokenExampleManglish: String,
        spokenExampleEnglish: String
    )

    // Insert missing cards and refresh content without replacing review history.
    @Transaction
    suspend fun syncSeedItems(items: List<DrillItem>) {
        insertAll(items)
        items.forEach { item ->
            updateSeedContent(
                item.id, item.tier, item.levelId, item.levelNumber,
                item.malayalam, item.manglish, item.english,
                item.spokenExampleMalayalam, item.spokenExampleManglish, item.spokenExampleEnglish
            )
        }
    }

    @Query(
        """
        UPDATE drill_items SET
            intervalDays = CASE
                WHEN :wasCorrect = 0 THEN 1
                WHEN repetitionCount = 0 THEN 1
                WHEN repetitionCount = 1 THEN 6
                ELSE MAX(1, CAST(ROUND(intervalDays * easeFactor) AS INTEGER))
            END,
            repetitionCount = CASE
                WHEN :wasCorrect = 0 THEN 0
                ELSE repetitionCount + 1
            END,
            easeFactor = MAX(
                1.3,
                easeFactor + (
                    0.1 - (5 - CASE WHEN :wasCorrect = 1 THEN 5 ELSE 0 END) *
                    (0.08 + (5 - CASE WHEN :wasCorrect = 1 THEN 5 ELSE 0 END) * 0.02)
                )
            ),
            nextReviewTimestamp = :currentTime + (
                CASE
                    WHEN :wasCorrect = 0 THEN 1
                    WHEN repetitionCount = 0 THEN 1
                    WHEN repetitionCount = 1 THEN 6
                    ELSE MAX(1, CAST(ROUND(intervalDays * easeFactor) AS INTEGER))
                END
            ) * 86400000,
            lastReviewedTimestamp = :currentTime
        WHERE id = :itemId
        """
    )
    suspend fun updateSpacedRepetition(itemId: String, wasCorrect: Boolean, currentTime: Long)
}
