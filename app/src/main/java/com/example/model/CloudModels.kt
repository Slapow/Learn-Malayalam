package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CloudUserProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Adoor Learner",
    val xp: Long = 0L,
    val streakDays: Long = 0L,
    val completedQuizCount: Long = 0L,
    val spokenPhrasesCount: Long = 0L,
    val activeScreen: String = "HOME",
    val activeDeckId: String = "deck_level0_words",
    val activeCardIndex: Long = 0L,
    val activeQuizIndex: Long = 0L,
    val activeScenarioId: String = "scenario_ammachi",
    val lastDevice: String = "Android",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloudLearnedPhrase(
    @DocumentId val id: String = "",
    val phraseId: String = "",
    val userId: String = "",
    val manglish: String = "",
    val category: String = "",
    val masteryLevel: Long = 1L, // 1 = Struggling/Missed, 3 = Familiar (1/2), 5 = Known/Mastered (2/2)
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
