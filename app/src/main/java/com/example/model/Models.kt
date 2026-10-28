package com.example.model

/** A spoken Malayalam vocabulary item and its optional conversational example. */
data class PhraseItem(
    val id: String,
    val manglish: String,
    val malayalam: String,
    val english: String,
    val regionalNuance: String = "",
    val textbookAlternative: String = "",
    val category: String = "Novice Level 1: Core Words",
    val levelNumber: Int = 0, // Global index in SpokenCurriculum.levels
    val levelBadge: String = "Novice Level 1",
    val phoneticGuide: String = "",
    val wordBreakdown: String = "",
    val prerequisiteIds: List<String> = emptyList(),
    val acceptedVariants: List<String> = emptyList(),
    val audioUtterance: String = malayalam,
    val tags: List<String> = emptyList(),
    val spokenExampleMalayalam: String = "",
    val spokenExampleManglish: String = "",
    val spokenExampleEnglish: String = ""
)

/**
 * Tracks per-word/phrase mastery & struggle history in the user's Known Words Database.
 */
data class PhraseMasteryRecord(
    val phraseId: String,
    val touchLevel: Int = 0, // 0 = Untouched, 1 = Touch 1 (Recognition), 2 = Touch 2 (Cued Production), 3 = Touch 3 (Free Recall / Mastered)
    val consecutiveCorrect: Int = 0,
    val totalCorrect: Int = 0,
    val mistakeCount: Int = 0,
    val lastPracticedMs: Long = System.currentTimeMillis()
) {
    val isKnown: Boolean get() = touchLevel >= 3 || consecutiveCorrect >= 2
    val isStillLearning: Boolean get() = touchLevel in 1..2
    val isStruggling: Boolean get() = mistakeCount > 0 && touchLevel < 3
}

/** Course tiers for spoken Malayalam; these are not ACTFL or CEFR certifications. */
enum class ActflProficiencyTier(
    val levelNumber: Int,
    val tierId: String,
    val title: String,
    val shortTitle: String,
    val actflDescriptor: String,
    val iconEmoji: String,
    val focusDescription: String,
    val grammarStructures: List<String>,
    val canDoStatements: List<String>,
    val dialectHighlights: String,
    val prerequisiteTierId: String?,
    val graduationTitle: String
) {
    NOVICE(
        levelNumber = 1,
        tierId = "novice",
        title = "Level 1: Novice (Formulaic & Survival)",
        shortTitle = "Novice",
        actflDescriptor = "High-frequency root nouns, personal pronouns, basic numbers, polite greetings, immediate food/family terms, and core imperative verbs.",
        iconEmoji = "🌱",
        focusDescription = "High-frequency root nouns, personal pronouns, basic numbers, polite greetings, immediate food/family terms, and core imperative verbs.",
        grammarStructures = listOf(
            "Subject-Object-Verb (SOV) basic word order (e.g., 'Njan chaya kudikkum')",
            "Pronouns (Njan, Nee/Ningal, Avan/Aval, Njangal, Avar) and polite address markers",
            "Simple present tense & existence forms ('undo?', 'illa', 'alla', 'veno?', 'venda')",
            "Question words (Aara - Who, Entha - What, Evide - Where, Eppol - When, Ethra - How much)"
        ),
        canDoStatements = listOf(
            "Introduce self and family members using memorized phrases & loving Central Travancore terms (Ammachi, Appachan, Appa, Achayan).",
            "State basic needs: ask for food or tea ('Chaya veno?'), water ('Vellam venam'), or directions.",
            "Count, exchange prices, and use basic courtesies (greetings, gratitude) with warm domestic tone."
        ),
        dialectHighlights = "Central Travancore kinship addresses: 'Ammachi' (Grandmother), 'Appachan' (Grandfather), and conversational contraction 'Entha?' instead of textbook 'Enthaanu?'.",
        prerequisiteTierId = null,
        graduationTitle = "Level 1 Novice Spoken Graduation Exam"
    ),

    ELEMENTARY(
        levelNumber = 2,
        tierId = "elementary",
        title = "Level 2: Elementary (Transactional & Daily Routine)",
        shortTitle = "Elementary",
        actflDescriptor = "Daily schedule routines, shopping in town, travel navigation, weather, health/symptoms, and common emotion adjectives.",
        iconEmoji = "🛺",
        focusDescription = "Daily schedule routines, shopping, travel navigation, weather, health/symptoms, and common emotion adjectives.",
        grammarStructures = listOf(
            "Past tense ('poyi', 'vannu', 'kazhichu'), simple future ('varum', 'pokum'), and continuous ('-unnu')",
            "Postpositions and location markers ('-il', '-kku', 'aduthu', 'ninnu')",
            "Negation patterns: Iconic Central Travancore verbal contractions ('Varoolla'/'Varathilla' - won't come, 'Cheyyoolla' - won't do, 'Pokoolla' - won't go, 'Pattilla' - cannot)",
            "Basic conjunctions ('pinne' - and then, 'pakshe' - but, 'kaaranam' - because)"
        ),
        canDoStatements = listOf(
            "Form original 4-to-6 word sentences rather than solely reciting fixed phrases.",
            "Describe yesterday's routine ('Innale town-il poyi') and outline tomorrow's plans ('Naale palli-yil pokum').",
            "Ask and answer routine personal questions without external prompting.",
            "Handle predictable transactional interactions (Adoor KSRTC bus, auto meters, grocery and thattukada ordering)."
        ),
        dialectHighlights = "Verbal negative future contractions ('-oolla' / '-athilla') and authentic Central Travancore transit & thattukada ordering.",
        prerequisiteTierId = "novice",
        graduationTitle = "Level 2 Elementary Spoken Graduation Exam"
    ),

    INTERMEDIATE(
        levelNumber = 3,
        tierId = "intermediate",
        title = "Level 3: Intermediate (Spontaneous & Conversational)",
        shortTitle = "Intermediate",
        actflDescriptor = "Abstract feelings, storytelling/narration, workplace/school topics, social opinions, and conversational filler words.",
        iconEmoji = "🏡",
        focusDescription = "Abstract feelings, storytelling/narration, workplace/school topics, social opinions, and conversational filler words.",
        grammarStructures = listOf(
            "Conditional clauses ('-enkil' - if/when, 'ennaalum' - even if)",
            "Modal auxiliaries ('pattum' - can, 'venam' - must/should, 'aakaam' - might)",
            "Relative clauses and compound verb constructions ('poyi vannu', 'nokki kandu')",
            "Spoken vs. formal register shifts and common Central Travancore colloquial contractions"
        ),
        canDoStatements = listOf(
            "Sustain a 2-to-3 minute unscripted conversation on familiar personal, family, church, or social topics.",
            "Narrate a past trip or family story chronologically with Ammachi's kitchen recipes.",
            "Express and justify personal opinions, preferences, and friendly advice ('Enikku thonunnu...').",
            "Use circumlocution (explaining a concept using known words when the exact term is forgotten)."
        ),
        dialectHighlights = "Central Travancore conversational tags ('Aadey', 'Ketto?', 'Alle?', 'Enthuvaadey') and Catholic parish/rosary traditions.",
        prerequisiteTierId = "elementary",
        graduationTitle = "Level 3 Intermediate Spoken Graduation Exam"
    ),

    ADVANCED(
        levelNumber = 4,
        tierId = "advanced",
        title = "Level 4: Advanced (Professional & Nuanced)",
        shortTitle = "Advanced",
        actflDescriptor = "News, cultural issues, professional/technical discussions, debate vocabulary, and idiomatic expressions.",
        iconEmoji = "📜",
        focusDescription = "News, cultural issues, professional/technical discussions, debate vocabulary, and idiomatic expressions.",
        grammarStructures = listOf(
            "Complex nested clauses, passive/causative constructions ('cheyyippichu', 'parayappettu')",
            "Subtle tense-aspect combinations (habitual past, perfect continuous)",
            "Nuanced tone particles (politeness shades, mild irony, emphasis markers: '-aanallo', '-thanne')",
            "Fluency in regional spoken contractions alongside formal standard syntax"
        ),
        canDoStatements = listOf(
            "Defend an opinion in an open debate or discussion, weighing pros and cons diplomatically.",
            "Explain complex technical, cultural, or abstract topics with minimal pausing.",
            "Understand and participate comfortably in group conversations among native speakers."
        ),
        dialectHighlights = "Nuanced Central Travancore tone particles and fluid code-switching between formal Kerala news syntax and home Adoor cadence.",
        prerequisiteTierId = "intermediate",
        graduationTitle = "Level 4 Advanced Spoken Graduation Exam"
    ),

    MASTERY(
        levelNumber = 5,
        tierId = "mastery",
        title = "Level 5: Mastery (Idioms & Cultural Nuance)",
        shortTitle = "Mastery",
        actflDescriptor = "Cultural idioms, proverbs, humor/satire, regional dialectal variations, and literary vocabulary.",
        iconEmoji = "👑",
        focusDescription = "Cultural idioms, proverbs, humor/satire, regional dialectal variations, and literary vocabulary.",
        grammarStructures = listOf(
            "Recognising differences between informal, formal, and literary expressions",
            "Practising rhythm, contractions, and intonation through listening and speaking"
        ),
        canDoStatements = listOf(
            "Practise interpreting idioms, implied meaning, and culturally specific humour.",
            "Compare informal everyday expressions with more formal alternatives.",
            "Continue developing spontaneous conversation with speakers beyond the course."
        ),
        dialectHighlights = "Mastery of iconic Pathanamthitta/Adoor exclamations ('Enthuvaadey!', 'Pulli'), famous Pazhamchollukal (Malayalam proverbs), and cultural eloquence.",
        prerequisiteTierId = "advanced",
        graduationTitle = "Level 5 Cultural Nuance Course Review"
    );

    val targetVocabulary: String
        get() = "${SpokenCurriculum.entriesInTier(this)} teaching entries in this tier; ${SpokenCurriculum.cumulativeEntries(this)} cumulative"

    val minWordsToGraduate: Int
        get() = SpokenCurriculum.cumulativeEntries(this)

    val graduationGate: String
        get() = "Complete this tier's units and spoken course review. Continue spaced review, listening, and unscripted conversation; course completion is not a fluency certification."

    companion object {
        fun fromTierId(id: String): ActflProficiencyTier {
            return when (id.lowercase()) {
                "novice" -> NOVICE
                "elementary" -> ELEMENTARY
                "intermediate" -> INTERMEDIATE
                "advanced" -> ADVANCED
                "superior", "distinguished", "mastery" -> MASTERY
                else -> entries.find { it.tierId.equals(id, ignoreCase = true) } ?: NOVICE
            }
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
    val correctOptionIndex: Int = 0,
    val correctTileOrder: List<String> = emptyList(),
    val acceptedManglishVariants: List<String> = emptyList(),
    val roundNumber: Int = 1,
    val roundLabel: String = "Touch 1: Recognition",
    val touchLevel: Int = 1,
    val isSpacedReview: Boolean = false,
    val recycleCount: Int = 0
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
    val streakDays: Int = 1,
    val learnedPhraseIds: Set<String> = emptySet(),
    val masteredDeckIds: Set<String> = emptySet(),
    val completedQuizCount: Int = 0,
    val spokenPhrasesCount: Int = 0,
    val masteryRecords: Map<String, PhraseMasteryRecord> = emptyMap(),
    val graduatedTiers: Set<String> = emptySet(), // e.g. "ground_zero", "novice", "intermediate", "advanced", "superior"
    val masteredDrillLevelIndices: Set<Int> = emptySet(),
    val completedDrillRounds: Set<String> = emptySet(),
    val unlockedLevelIndex: Int = 0
)

data class LevelUpEvent(
    val newLevelIndex: Int,
    val rankTitle: String,
    val completedLevelTitle: String,
    val unlockedDeckTitle: String,
)
