package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drill_items")
data class DrillItem(
    @PrimaryKey val id: String,
    val tier: ActflProficiencyTier,
    val levelId: String,
    val levelNumber: Int,
    val malayalam: String,
    val manglish: String,
    val english: String,
    val spokenExampleMalayalam: String = "",
    val spokenExampleManglish: String = "",
    val spokenExampleEnglish: String = "",
    val intervalDays: Int = 1,
    val repetitionCount: Int = 0,
    val easeFactor: Float = 2.5f,
    val nextReviewTimestamp: Long = System.currentTimeMillis(),
    val lastReviewedTimestamp: Long = 0L
)
