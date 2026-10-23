package com.example.model

/**
 * Fundamental data unit following the required 3-part layout + Level 0 English Beginner scaffolding:
 * 1. Manglish (Roman Script with spoken cadence)
 * 2. Malayalam Script
 * 3. English Translation & Regional / Dialect Nuance Context
 * + Phonetic pronunciation guide & word-by-word literal breakdown for Level 0 learners.
 */
data class PhraseItem(
    val id: String,
    val manglish: String,
    val malayalam: String,
    val english: String,
    val regionalNuance: String = "",
    val textbookAlternative: String = "",
    val category: String = "Novice Level 1: Core Words",
    val levelNumber: Int = 0, // 0..10 across the progression plan
    val levelBadge: String = "Novice Level 1",
    val phoneticGuide: String = "",
    val wordBreakdown: String = "",
    val prerequisiteIds: List<String> = emptyList(),
    val acceptedVariants: List<String> = emptyList(),
    val audioUtterance: String = malayalam,
    val tags: List<String> = emptyList()
)

/**
 * Tracks per-word/phrase mastery & struggle history in the user's Known Words Database.
 */
data class PhraseMasteryRecord(
    val phraseId: String,
    val consecutiveCorrect: Int = 0, // 0 = Unfamiliar, 1 = Still Learning, 2+ = Mastered/Known
    val totalCorrect: Int = 0,
    val mistakeCount: Int = 0,
    val lastPracticedMs: Long = System.currentTimeMillis()
) {
    val isKnown: Boolean get() = consecutiveCorrect >= 2
    val isStillLearning: Boolean get() = consecutiveCorrect == 1
    val isStruggling: Boolean get() = mistakeCount > 0 && consecutiveCorrect < 2
}

/**
 * Official ACTFL Language Proficiency Scale adapted for Spoken Malayalam (Central Travancore):
 * - Novice (Levels 1..3): Limited vocabulary; short, simple responses & home family combos
 * - Intermediate (Levels 1..3): Familiar topics; kitchen/thattukada, travel & Catholic church/prayers
 * - Advanced (Levels 1..2): Fluent in most contexts; storytelling, past memories & polite debate
 * - Superior (Levels 1..2): Near-native; rapid Central Travancore banter, idioms & Malayalam proverbs
 * - Distinguished: Exceptional; cultural master, parish community discourse & eloquent speeches
 */
enum class ActflProficiencyTier(
    val tierId: String,
    val title: String,
    val actflDescriptor: String,
    val iconEmoji: String,
    val prerequisiteTierId: String?,
    val graduationTitle: String
) {
    NOVICE(
        tierId = "novice",
        title = "Novice Tier",
        actflDescriptor = "Limited vocabulary; short, simple responses, home contractions & family dialogues",
        iconEmoji = "🌿",
        prerequisiteTierId = null,
        graduationTitle = "Novice-to-Intermediate Spoken Graduation Exam"
    ),
    INTERMEDIATE(
        tierId = "intermediate",
        title = "Intermediate Tier",
        actflDescriptor = "Familiar topics: food orders, bus navigation & Catholic church/prayers",
        iconEmoji = "⛪",
        prerequisiteTierId = "novice",
        graduationTitle = "Intermediate-to-Advanced Spoken Graduation Exam"
    ),
    ADVANCED(
        tierId = "advanced",
        title = "Advanced Tier",
        actflDescriptor = "Fluent in most contexts: storytelling, feelings, memories & nuanced opinions",
        iconEmoji = "📜",
        prerequisiteTierId = "intermediate",
        graduationTitle = "Advanced-to-Superior Spoken Graduation Exam"
    ),
    SUPERIOR(
        tierId = "superior",
        title = "Superior Tier",
        actflDescriptor = "Near-native: rapid family banter, exclamations & famous Malayalam proverbs",
        iconEmoji = "🏆",
        prerequisiteTierId = "advanced",
        graduationTitle = "Superior-to-Distinguished Spoken Graduation Exam"
    ),
    DISTINGUISHED(
        tierId = "distinguished",
        title = "Distinguished Tier",
        actflDescriptor = "Exceptional mastery: cultural orator, community discourse & seamless adaptability",
        iconEmoji = "👑",
        prerequisiteTierId = "superior",
        graduationTitle = "Master Speaker Certification"
    );

    companion object {
        fun fromTierId(id: String): ActflProficiencyTier {
            return entries.find { it.tierId == id } ?: NOVICE
        }
    }
}

/**
 * Represents a stage in the ACTFL Proficiency Study Plan
 */
data class StudyLevelStage(
    val levelIndex: Int,
    val tierId: String = "novice",
    val tierName: String = "Novice Tier",
    val rankTitle: String,       // e.g. "Novice Level 1", "Intermediate Level 3 (Catholic & Church)"
    val topicTitle: String,      // e.g. "Catholic Church, Mass & Daily Prayers"
    val description: String,
    val iconEmoji: String,
    val deckId: String,
    val requiredWordsToUnlock: Int,
    val isTierFinalStage: Boolean = false // If true, reaching this stage triggers the Oral Graduation Exam to unlock the next tier!
)

data class GraduationPrompt(
    val id: String,
    val phrase: PhraseItem,
    val scenarioPrompt: String,  // Real-life context prompt in English
    val targetExpectedManglish: String,
    val hint: String = ""
)

data class TierGraduationExamState(
    val tier: ActflProficiencyTier = ActflProficiencyTier.NOVICE,
    val prompts: List<GraduationPrompt> = emptyList(),
    val currentPromptIndex: Int = 0,
    val scoreCount: Int = 0,
    val isListening: Boolean = false,
    val spokenTranscript: String = "",
    val isEvaluated: Boolean = false,
    val isPromptPassed: Boolean = false,
    val feedbackMessage: String = "",
    val isExamFinished: Boolean = false,
    val didPassGraduation: Boolean = false,
    val xpAwarded: Int = 0
)

enum class QuizType {
    MANGLISH_TYPING,    // Type Manglish with smart tolerance + Copy Answer when wrong
    SPEAKING_PRACTICE,  // Speak into mic out loud, analyze speech transcript
    WORD_TILES,         // Arrange Manglish tiles in order
    DIALECT_CHOICE      // Pick authentic Adoor spoken phrase vs options
}

/**
 * All Study Modes available for any selected Deck
 */
enum class QuizletStudyMode(
    val title: String,
    val shortLabel: String,
    val iconEmoji: String,
    val subtitle: String
) {
    FLASHCARDS(
        title = "Flashcards",
        shortLabel = "Cards",
        iconEmoji = "🃏",
        subtitle = "Interactive cards with pronunciation & breakdown"
    ),
    MIXED_MASTERY(
        title = "Mixed Recall",
        shortLabel = "Mixed",
        iconEmoji = "🔥",
        subtitle = "Adaptive 50/50 mix of typing and speaking drills"
    ),
    LEARN(
        title = "Adaptive Learn",
        shortLabel = "Learn",
        iconEmoji = "🧠",
        subtitle = "Multiple choice to typing and speaking recall"
    ),
    WRITE(
        title = "Spelling & Recall",
        shortLabel = "Write",
        iconEmoji = "⌨️",
        subtitle = "Type the spoken Manglish; retype missed words to lock in memory"
    ),
    SPEAK(
        title = "Spoken Audio Drill",
        shortLabel = "Speak",
        iconEmoji = "🎙️",
        subtitle = "Practice speaking phrases out loud with instant pronunciation feedback"
    ),
    MATCH(
        title = "Speed Match",
        shortLabel = "Match",
        iconEmoji = "⏱️",
        subtitle = "Pair English meanings with Manglish words against the clock"
    ),
    TEST(
        title = "Level Assessment",
        shortLabel = "Test",
        iconEmoji = "📝",
        subtitle = "Full graded exam mixing True/False, Multiple Choice, Written & Spoken questions"
    )
}

enum class StudyPromptFormat {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    WRITTEN_MANGLISH,
    SPOKEN_OUT_LOUD
}

data class DeckStudyQuestion(
    val id: String,
    val phrase: PhraseItem,
    val format: StudyPromptFormat,
    val promptText: String,
    val subPromptText: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val trueFalseShownManglish: String = "",
    val isTruePair: Boolean = true,
    val currentMasteryStreak: Int = 0,
    val requiredMasteryStreak: Int = 2,
    val isRemediationStep: Boolean = false
)

data class MatchTile(
    val id: String,
    val phraseId: String,
    val text: String,
    val subText: String = "",
    val isEnglish: Boolean,
    val isMatched: Boolean = false
)

data class QuizQuestion(
    val id: String,
    val type: QuizType,
    val phrase: PhraseItem,
    val prompt: String,
    val hint: String = "",
    val options: List<String> = emptyList(),
    val correctTileOrder: List<String> = emptyList(),
    val acceptedManglishVariants: List<String> = emptyList()
)

data class FlashcardDeck(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val levelIndex: Int = 0,
    val levelLabel: String = "Novice Level 1",
    val phrases: List<PhraseItem>
)

data class RoleplayScenario(
    val id: String,
    val title: String,
    val partnerName: String,
    val partnerRole: String,
    val avatarEmoji: String,
    val location: String,
    val initialMessage: ChatMessage,
    val systemPromptContext: String
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val manglishText: String,
    val malayalamText: String = "",
    val englishText: String = "",
    val correctionFeedback: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserStats(
    val xp: Int = 0,
    val streakDays: Int = 1,
    val learnedPhraseIds: Set<String> = emptySet(),
    val masteredDeckIds: Set<String> = emptySet(),
    val completedQuizCount: Int = 0,
    val spokenPhrasesCount: Int = 0,
    val masteryRecords: Map<String, PhraseMasteryRecord> = emptyMap(),
    val graduatedTiers: Set<String> = emptySet() // e.g. "ground_zero", "novice", "intermediate", "advanced", "superior"
)
