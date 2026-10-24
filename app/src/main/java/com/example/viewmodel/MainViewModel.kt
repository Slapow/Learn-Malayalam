package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioHelper
import com.example.data.CloudProgressRepository
import com.example.data.DialectRepository
import com.example.data.SmartGrader
import com.example.data.TutorAiService
import com.example.data.UserStatsRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class Screen {
    HOME,
    QUIZ,
    FLASHCARDS,
    AI_TUTOR,
    DIALECT_GUIDE,
    SETTINGS
}

data class QuizState(
    val currentIndex: Int = 0,
    val questions: List<QuizQuestion> = emptyList(),
    val typedInput: String = "",
    val selectedTileIndices: List<Int> = emptyList(),
    val selectedOptionIndex: Int? = null,
    val isEvaluated: Boolean = false,
    val isCorrect: Boolean = false,
    val isSkipped: Boolean = false,
    val feedbackMessage: String = "",
    val speechTranscription: String = "",
    val mustCopyAnswer: Boolean = false,
    val copyAnswerInput: String = "",
    val isCopyAnswerUnlocked: Boolean = false,
    val reshuffledCount: Int = 0,
    val isFinished: Boolean = false
)

/**
 * Complete Quizlet + Duolingo Deck Study State supporting all 7 modes:
 * FLASHCARDS, MIXED_MASTERY (Written + Spoken Shuffle), LEARN, WRITE, SPEAK, MATCH, TEST
 */
data class FlashcardSwipeHistory(
    val phraseId: String,
    val cardIndex: Int,
    val wasKnown: Boolean,
    val wasFirstStillLearning: Boolean = false
)

data class FlashcardState(
    val currentDeck: FlashcardDeck = DialectRepository.decks[0],
    val activeMode: QuizletStudyMode = QuizletStudyMode.FLASHCARDS,
    // 1. Classic Flashcards sub-state
    val currentCardIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isEnglishOnFront: Boolean = true,
    val isShuffled: Boolean = false,
    val learnedInSession: Set<String> = emptySet(),
    val stillLearningInSession: Set<String> = emptySet(),
    val cardsSentToStillLearning: Set<String> = emptySet(),
    val cardPresentationCount: Map<String, Int> = emptyMap(),
    val swipeHistory: List<FlashcardSwipeHistory> = emptyList(),
    // 2. Adaptive Queue sub-state (MIXED_MASTERY, LEARN, WRITE, SPEAK, TEST)
    val studyQueue: List<DeckStudyQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val cardStreaks: Map<String, Int> = emptyMap(), // 0 = Unfamiliar, 1 = Familiar (1/2), 2 = Mastered (2/2)
    val typedResponse: String = "",
    val selectedMcOption: Int? = null,
    val selectedTrueFalse: Boolean? = null,
    val spokenTranscript: String = "",
    val isQuestionEvaluated: Boolean = false,
    val isQuestionCorrect: Boolean = false,
    val isAlmostExactTypo: Boolean = false,
    val gradeHeadline: String = "",
    val gradeDetail: String = "",
    // Quizlet "Copy Answer When Wrong" Lock
    val mustCopyToContinue: Boolean = false,
    val copyInputText: String = "",
    val isCopyVerified: Boolean = false,
    // Round / Test Summary
    val totalAnsweredInRound: Int = 0,
    val totalCorrectInRound: Int = 0,
    val reshuffleEventsCount: Int = 0,
    val isRoundComplete: Boolean = false,
    // 3. Quizlet Match Mode sub-state
    val matchTiles: List<MatchTile> = emptyList(),
    val selectedMatchTileId: String? = null,
    val mismatchedTileIds: Set<String> = emptySet(),
    val matchElapsedTenths: Int = 0,
    val matchBestTenths: Int? = null,
    val isMatchFinished: Boolean = false
)

data class AiTutorState(
    val currentScenario: RoleplayScenario = DialectRepository.roleplayScenarios[0],
    val messages: List<ChatMessage> = listOf(DialectRepository.roleplayScenarios[0].initialMessage),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class MainViewModel(
    private val cloudRepo: CloudProgressRepository,
    private val statsRepo: UserStatsRepository,
    val audioHelper: AudioHelper,
    private val currentUserId: String,
    private val userDisplayName: String
) : ViewModel() {

    private val aiService = TutorAiService()

    val userStats: StateFlow<UserStats> = statsRepo.stats

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _flashcardState = MutableStateFlow(FlashcardState())
    val flashcardState: StateFlow<FlashcardState> = _flashcardState.asStateFlow()

    private val _aiTutorState = MutableStateFlow(AiTutorState())
    val aiTutorState: StateFlow<AiTutorState> = _aiTutorState.asStateFlow()

    private val _speechStatus = MutableStateFlow("Ready to listen")
    val speechStatus: StateFlow<String> = _speechStatus.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _cloudSyncStatus = MutableStateFlow("☁️ Synced with Cloud Firestore")
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    private val _lastSyncedDevice = MutableStateFlow("Android")
    val lastSyncedDevice: StateFlow<String> = _lastSyncedDevice.asStateFlow()

    private val _lastActiveCloudScreen = MutableStateFlow("HOME")
    val lastActiveCloudScreen: StateFlow<String> = _lastActiveCloudScreen.asStateFlow()

    private val _tierGraduationState = MutableStateFlow<TierGraduationExamState?>(null)
    val tierGraduationState: StateFlow<TierGraduationExamState?> = _tierGraduationState.asStateFlow()

    private var autoAdvanceQuizJob: Job? = null
    private var autoAdvanceDeckJob: Job? = null

    init {
        startNewQuizSession()
        initializeDeckStudySession(DialectRepository.decks[0], QuizletStudyMode.FLASHCARDS)
        observeCloudState()
    }

    private fun observeCloudState() {
        viewModelScope.launch {
            cloudRepo.observeUserProgress(currentUserId)
                .catch { e ->
                    Log.w("MainVM", "Error observing user progress", e)
                    _cloudSyncStatus.value = "Offline cache active"
                }
                .collect { cloudProgress ->
                    if (cloudProgress != null) {
                        val isLegacyDefault = cloudProgress.xp == 140L && cloudProgress.streakDays == 4L &&
                            statsRepo.stats.value.learnedPhraseIds.isEmpty()
                        if (isLegacyDefault) {
                            // Clean up old placeholder 140 XP / 4-day streak in Cloud Firestore
                            statsRepo.resetProgressToZero()
                            syncToCloud()
                        } else {
                            statsRepo.syncFromCloud(
                                xp = cloudProgress.xp.toInt(),
                                streakDays = cloudProgress.streakDays.toInt(),
                                quizCount = cloudProgress.completedQuizCount.toInt(),
                                spokenCount = cloudProgress.spokenPhrasesCount.toInt()
                            )
                        }
                        _lastSyncedDevice.value = cloudProgress.lastDevice
                        _lastActiveCloudScreen.value = cloudProgress.activeScreen

                        val deck = DialectRepository.decks.find { it.id == cloudProgress.activeDeckId }
                        if (deck != null && deck.id != _flashcardState.value.currentDeck.id) {
                            val safeCardIdx = cloudProgress.activeCardIndex.toInt()
                                .coerceIn(0, (deck.phrases.size - 1).coerceAtLeast(0))
                            _flashcardState.value = _flashcardState.value.copy(
                                currentDeck = deck,
                                currentCardIndex = safeCardIdx
                            )
                        }
                        _cloudSyncStatus.value = "☁️ Auto-saved to Cloud Firestore"
                    } else {
                        syncToCloud()
                    }
                }
        }

        viewModelScope.launch {
            cloudRepo.observeLearnedPhrases(currentUserId)
                .catch { e ->
                    Log.w("MainVM", "Error observing learned phrases", e)
                }
                .collect { cloudPhrases ->
                    if (cloudPhrases.isNotEmpty()) {
                        statsRepo.syncCloudLearnedPhrases(cloudPhrases)
                    }
                }
        }
    }

    fun syncToCloud() {
        viewModelScope.launch {
            _cloudSyncStatus.value = "☁️ Saving to Cloud..."
            val result = cloudRepo.saveOrUpdateProgress(
                displayName = userDisplayName,
                stats = statsRepo.stats.value,
                activeScreen = _currentScreen.value.name,
                activeDeckId = _flashcardState.value.currentDeck.id,
                activeCardIndex = _flashcardState.value.currentCardIndex,
                activeQuizIndex = _quizState.value.currentIndex,
                activeScenarioId = _aiTutorState.value.currentScenario.id,
                lastDevice = "Android"
            )
            _cloudSyncStatus.value = if (result.isSuccess) {
                "☁️ Auto-saved to Cloud Firestore"
            } else {
                "☁️ Saved locally (Cloud retry pending)"
            }
        }
    }

    fun resetAllProgressToZero() {
        statsRepo.resetProgressToZero()
        initializeDeckStudySession(DialectRepository.decks[0], QuizletStudyMode.FLASHCARDS)
        startNewQuizSession()
        syncToCloud()
    }

    fun resumeLastCloudSession() {
        val target = try {
            Screen.valueOf(_lastActiveCloudScreen.value)
        } catch (_: Exception) {
            Screen.FLASHCARDS
        }
        navigateTo(if (target == Screen.HOME) Screen.FLASHCARDS else target)
    }

    fun buildSessionSnapshotJson(): JSONObject {
        val s = statsRepo.stats.value
        return JSONObject().apply {
            put("xp", s.xp)
            put("streakDays", s.streakDays)
            put("completedQuizCount", s.completedQuizCount)
            put("spokenPhrasesCount", s.spokenPhrasesCount)
            put("activeScreen", _currentScreen.value.name)
            put("activeDeckId", _flashcardState.value.currentDeck.id)
            put("activeCardIndex", _flashcardState.value.currentCardIndex)
            put("activeQuizIndex", _quizState.value.currentIndex)
            put("activeScenarioId", _aiTutorState.value.currentScenario.id)
            put("lastDevice", "Android")
        }
    }

    fun applyRemoteLanSession(json: JSONObject) {
        val newXp = json.optInt("xp", statsRepo.stats.value.xp)
        val newStreak = json.optInt("streakDays", statsRepo.stats.value.streakDays)
        val newQuizCount = json.optInt("completedQuizCount", statsRepo.stats.value.completedQuizCount)
        val newSpoken = json.optInt("spokenPhrasesCount", statsRepo.stats.value.spokenPhrasesCount)
        statsRepo.syncFromCloud(newXp, newStreak, newQuizCount, newSpoken)
        _lastSyncedDevice.value = "Laptop Web"
        _lastActiveCloudScreen.value = json.optString("activeScreen", _lastActiveCloudScreen.value)
        syncToCloud()
    }

    fun navigateTo(screen: Screen) {
        autoAdvanceQuizJob?.cancel()
        autoAdvanceDeckJob?.cancel()
        _currentScreen.value = screen
        syncToCloud()
    }

    fun resetUserProgressToZero() {
        statsRepo.resetProgressToZero()
        syncToCloud()
    }

    /**
     * Launches any specific deck & Quizlet study mode directly from Home or the Study Plan Ladder
     */
    fun openDeckWithMode(deck: FlashcardDeck, mode: QuizletStudyMode = QuizletStudyMode.FLASHCARDS) {
        initializeDeckStudySession(deck, QuizletStudyMode.FLASHCARDS)
        _currentScreen.value = Screen.FLASHCARDS
        syncToCloud()
    }

    /**
     * Dynamically builds an Adaptive Struggle Deck of missed words + Level 0 prerequisites
     * and launches it in Flashcards mode!
     */
    fun launchDynamicAdaptiveReview(mode: QuizletStudyMode = QuizletStudyMode.FLASHCARDS) {
        val adaptiveDeck = DialectRepository.buildAdaptiveStruggleDeck(statsRepo.stats.value.masteryRecords)
        openDeckWithMode(adaptiveDeck, QuizletStudyMode.FLASHCARDS)
    }

    // === Audio Functions ===
    fun speakPhrase(phrase: PhraseItem) {
        audioHelper.speak(phrase.malayalam, phrase.manglish)
    }

    fun speakText(malayalamText: String, manglishText: String = "") {
        audioHelper.speak(malayalamText, manglishText)
    }

    fun stopSpeaking() {
        audioHelper.stopSpeaking()
    }

    fun toggleLearned(phraseId: String) {
        statsRepo.markPhraseLearned(phraseId)
        val phrase = DialectRepository.allPhrases.find { it.id == phraseId }
        val rec = statsRepo.stats.value.masteryRecords[phraseId]
        if (phrase != null) {
            val level = if (rec?.isKnown == true) 5 else 2
            viewModelScope.launch {
                cloudRepo.saveLearnedPhrase(phrase, masteryLevel = level)
            }
        }
        syncToCloud()
    }

    // === Speech Recognition ===
    fun startSpeechPractice(onTranscription: (String) -> Unit) {
        _isListening.value = true
        _speechStatus.value = "Listening... Speak out loud now"

        audioHelper.startListening(
            onResult = { spokenText ->
                _isListening.value = false
                _speechStatus.value = "Heard: \"$spokenText\""
                // Update both QuizState and FlashcardState transcripts so whichever screen is active receives it
                _quizState.value = _quizState.value.copy(speechTranscription = spokenText)
                _flashcardState.value = _flashcardState.value.copy(spokenTranscript = spokenText)
                onTranscription(spokenText)
                statsRepo.incrementSpokenCount()
                syncToCloud()
            },
            onError = { errorMsg ->
                _isListening.value = false
                _speechStatus.value = errorMsg
            },
            onReadyForSpeech = {
                _speechStatus.value = "Mic open, speak Manglish or Malayalam..."
            }
        )
    }

    fun stopSpeechPractice() {
        audioHelper.stopListening()
        _isListening.value = false
        _speechStatus.value = "Ready to listen"
    }

    // =========================================================================
    // ACTFL TIER SPOKEN GRADUATION EXAM ENGINE
    // =========================================================================

    fun startTierGraduationExam(tier: ActflProficiencyTier) {
        val prompts = DialectRepository.getGraduationPromptsForTier(tier)
        _tierGraduationState.value = TierGraduationExamState(
            tier = tier,
            prompts = prompts,
            currentPromptIndex = 0,
            scoreCount = 0,
            isListening = false,
            spokenTranscript = "",
            isEvaluated = false,
            isPromptPassed = false,
            feedbackMessage = "",
            isExamFinished = false,
            didPassGraduation = false,
            xpAwarded = 0
        )
    }

    fun dismissGraduationExam() {
        audioHelper.stopListening()
        _tierGraduationState.value = null
    }

    fun toggleGraduationListening() {
        val state = _tierGraduationState.value ?: return
        if (state.isListening) {
            audioHelper.stopListening()
            _tierGraduationState.value = state.copy(isListening = false)
        } else {
            _tierGraduationState.value = state.copy(isListening = true, spokenTranscript = "")
            audioHelper.startListening(
                onResult = { transcript ->
                    val s = _tierGraduationState.value ?: return@startListening
                    _tierGraduationState.value = s.copy(
                        isListening = false,
                        spokenTranscript = transcript
                    )
                    statsRepo.incrementSpokenCount()
                    evaluateGraduationPrompt(overridePassed = null)
                },
                onError = { errMsg ->
                    val s = _tierGraduationState.value ?: return@startListening
                    _tierGraduationState.value = s.copy(
                        isListening = false,
                        feedbackMessage = "Mic error: $errMsg. You can tap 'I Spoke It Clearly' to self-verify!"
                    )
                },
                onReadyForSpeech = {
                    // Ready
                }
            )
        }
    }

    fun evaluateGraduationPrompt(overridePassed: Boolean? = null) {
        val state = _tierGraduationState.value ?: return
        val currentPrompt = state.prompts.getOrNull(state.currentPromptIndex) ?: return

        val isPassed = if (overridePassed != null) {
            overridePassed
        } else {
            val grade = SmartGrader.gradeSpokenResponse(state.spokenTranscript, currentPrompt.phrase)
            grade.isCorrect
        }

        val newScore = if (isPassed) state.scoreCount + 1 else state.scoreCount
        val msg = if (isPassed) {
            "Awesome pronunciation! Target: \"${currentPrompt.targetExpectedManglish}\""
        } else {
            "Heard: \"${state.spokenTranscript.ifBlank { "Nothing yet" }}\". Target: \"${currentPrompt.targetExpectedManglish}\""
        }

        speakPhrase(currentPrompt.phrase)

        _tierGraduationState.value = state.copy(
            isListening = false,
            isEvaluated = true,
            isPromptPassed = isPassed,
            scoreCount = newScore,
            feedbackMessage = msg
        )
    }

    fun nextGraduationPrompt() {
        val state = _tierGraduationState.value ?: return
        val nextIdx = state.currentPromptIndex + 1
        if (nextIdx >= state.prompts.size) {
            val passingThreshold = (state.prompts.size * 0.65).toInt().coerceAtLeast(1)
            val passed = state.scoreCount >= passingThreshold
            if (passed) {
                statsRepo.recordTierGraduation(state.tier.tierId)
                syncToCloud()
            }
            _tierGraduationState.value = state.copy(
                isExamFinished = true,
                didPassGraduation = passed,
                xpAwarded = if (passed) 50 else 10
            )
        } else {
            _tierGraduationState.value = state.copy(
                currentPromptIndex = nextIdx,
                spokenTranscript = "",
                isEvaluated = false,
                isPromptPassed = false,
                feedbackMessage = ""
            )
        }
    }

    // =========================================================================
    // QUIZLET + DUOLINGO MULTI-MODE DECK ENGINE (7 STUDY MODES)
    // =========================================================================

    fun selectDeck(deck: FlashcardDeck) {
        initializeDeckStudySession(deck, QuizletStudyMode.FLASHCARDS)
        syncToCloud()
    }

    fun selectQuizletMode(mode: QuizletStudyMode) {
        initializeDeckStudySession(_flashcardState.value.currentDeck, QuizletStudyMode.FLASHCARDS)
        syncToCloud()
    }

    fun initializeDeckStudySession(deck: FlashcardDeck, mode: QuizletStudyMode) {
        autoAdvanceDeckJob?.cancel()
        val initialStreaks = deck.phrases.associate { it.id to 0 }
        when (mode) {
            QuizletStudyMode.FLASHCARDS -> {
                val initialCardId = deck.phrases.getOrNull(0)?.id
                val initPresCount = if (initialCardId != null) mapOf(initialCardId to 1) else emptyMap()
                _flashcardState.value = FlashcardState(
                    currentDeck = deck,
                    activeMode = mode,
                    currentCardIndex = 0,
                    isFlipped = false,
                    isEnglishOnFront = _flashcardState.value.isEnglishOnFront,
                    isShuffled = false,
                    learnedInSession = emptySet(),
                    stillLearningInSession = emptySet(),
                    cardsSentToStillLearning = emptySet(),
                    cardPresentationCount = initPresCount,
                    swipeHistory = emptyList(),
                    cardStreaks = initialStreaks
                )
            }

            QuizletStudyMode.MATCH -> {
                val samplePhrases = deck.phrases.shuffled().take(6)
                val tiles = mutableListOf<MatchTile>()
                for (p in samplePhrases) {
                    tiles.add(
                        MatchTile(
                            id = "en_${p.id}",
                            phraseId = p.id,
                            text = p.english,
                            subText = "English",
                            isEnglish = true
                        )
                    )
                    tiles.add(
                        MatchTile(
                            id = "ml_${p.id}",
                            phraseId = p.id,
                            text = p.manglish,
                            subText = p.malayalam,
                            isEnglish = false
                        )
                    )
                }
                _flashcardState.value = FlashcardState(
                    currentDeck = deck,
                    activeMode = mode,
                    cardStreaks = initialStreaks,
                    matchTiles = tiles.shuffled(),
                    matchBestTenths = _flashcardState.value.matchBestTenths
                )
            }

            else -> {
                val queue = buildInitialStudyQueue(deck, mode, initialStreaks)
                _flashcardState.value = FlashcardState(
                    currentDeck = deck,
                    activeMode = mode,
                    studyQueue = queue,
                    currentQuestionIndex = 0,
                    cardStreaks = initialStreaks
                )
            }
        }
    }

    private fun buildInitialStudyQueue(
        deck: FlashcardDeck,
        mode: QuizletStudyMode,
        streaks: Map<String, Int>
    ): List<DeckStudyQuestion> {
        val phrases = deck.phrases.shuffled()
        return phrases.mapIndexed { idx, phrase ->
            val format = when (mode) {
                QuizletStudyMode.WRITE -> StudyPromptFormat.WRITTEN_MANGLISH
                QuizletStudyMode.SPEAK -> StudyPromptFormat.SPOKEN_OUT_LOUD
                QuizletStudyMode.MIXED_MASTERY -> {
                    // Alternating 50/50 Written and Spoken prompts
                    if (idx % 2 == 0) StudyPromptFormat.WRITTEN_MANGLISH else StudyPromptFormat.SPOKEN_OUT_LOUD
                }
                QuizletStudyMode.LEARN -> {
                    // Start with Multiple Choice for streak 0, then upgrade to Written/Spoken for streak 1!
                    StudyPromptFormat.MULTIPLE_CHOICE
                }
                QuizletStudyMode.TEST -> {
                    when (idx % 4) {
                        0 -> StudyPromptFormat.TRUE_FALSE
                        1 -> StudyPromptFormat.MULTIPLE_CHOICE
                        2 -> StudyPromptFormat.WRITTEN_MANGLISH
                        else -> StudyPromptFormat.SPOKEN_OUT_LOUD
                    }
                }
                else -> StudyPromptFormat.WRITTEN_MANGLISH
            }
            createDeckQuestionForPhrase(
                phrase = phrase,
                deck = deck,
                format = format,
                currentStreak = streaks[phrase.id] ?: 0,
                requiredStreak = if (mode == QuizletStudyMode.TEST) 1 else 2
            )
        }
    }

    private fun createDeckQuestionForPhrase(
        phrase: PhraseItem,
        deck: FlashcardDeck,
        format: StudyPromptFormat,
        currentStreak: Int,
        requiredStreak: Int = 2,
        isRemediation: Boolean = false
    ): DeckStudyQuestion {
        val allPool = (deck.phrases + DialectRepository.allPhrases).distinctBy { it.id }
        val distractors = allPool.filter { it.id != phrase.id }.shuffled().take(3).map { it.manglish }
        val mcOptions = (listOf(phrase.manglish) + distractors).shuffled()
        val correctIdx = mcOptions.indexOf(phrase.manglish).coerceAtLeast(0)

        val isTrue = (phrase.id.hashCode() + currentStreak) % 2 == 0
        val tfShown = if (isTrue) {
            phrase.manglish
        } else {
            distractors.firstOrNull() ?: "Varoolla"
        }

        val promptText = when (format) {
            StudyPromptFormat.MULTIPLE_CHOICE -> phrase.english
            StudyPromptFormat.TRUE_FALSE -> "Does \"${phrase.english}\" mean \"$tfShown\"?"
            StudyPromptFormat.WRITTEN_MANGLISH -> phrase.english
            StudyPromptFormat.SPOKEN_OUT_LOUD -> phrase.english
        }

        val subPrompt = buildString {
            if (phrase.phoneticGuide.isNotBlank() && format == StudyPromptFormat.SPOKEN_OUT_LOUD) {
                append("🔊 Phonetic hint: ${phrase.phoneticGuide}")
            }
            if (phrase.wordBreakdown.isNotBlank()) {
                if (isNotEmpty()) append("  •  ")
                append("🧩 ${phrase.wordBreakdown}")
            }
        }

        return DeckStudyQuestion(
            id = "dq_${phrase.id}_${System.currentTimeMillis()}_${(100..999).random()}",
            phrase = phrase,
            format = format,
            promptText = promptText,
            subPromptText = subPrompt,
            options = mcOptions,
            correctOptionIndex = correctIdx,
            trueFalseShownManglish = tfShown,
            isTruePair = isTrue,
            currentMasteryStreak = currentStreak,
            requiredMasteryStreak = requiredStreak,
            isRemediationStep = isRemediation
        )
    }

    // Classic Flashcards actions
    fun flipCard() {
        _flashcardState.value = _flashcardState.value.copy(
            isFlipped = !_flashcardState.value.isFlipped
        )
    }

    fun markCardKnown() {
        val s = _flashcardState.value
        val currentPhrase = s.currentDeck.phrases.getOrNull(s.currentCardIndex) ?: return
        val rec = statsRepo.recordPhraseAttempt(currentPhrase.id, isCorrect = true)
        val cloudLevel = if (rec.isKnown) 5 else 3
        viewModelScope.launch {
            cloudRepo.saveLearnedPhrase(currentPhrase, masteryLevel = cloudLevel)
        }
        val newStreaks = s.cardStreaks.toMutableMap().apply {
            put(currentPhrase.id, ((this[currentPhrase.id] ?: 0) + 1).coerceAtMost(2))
        }
        val newLearned = s.learnedInSession + currentPhrase.id
        val newStillLearning = s.stillLearningInSession - currentPhrase.id
        val nextIdx = (s.currentCardIndex + 1) % s.currentDeck.phrases.size
        val nextCardId = s.currentDeck.phrases.getOrNull(nextIdx)?.id
        val newPresCount = s.cardPresentationCount.toMutableMap()
        if (nextCardId != null) {
            newPresCount[nextCardId] = (newPresCount[nextCardId] ?: 0) + 1
        }
        val historyItem = FlashcardSwipeHistory(
            phraseId = currentPhrase.id,
            cardIndex = s.currentCardIndex,
            wasKnown = true
        )
        _flashcardState.value = s.copy(
            currentCardIndex = nextIdx,
            isFlipped = false,
            learnedInSession = newLearned,
            stillLearningInSession = newStillLearning,
            cardPresentationCount = newPresCount,
            swipeHistory = s.swipeHistory + historyItem,
            cardStreaks = newStreaks
        )
        syncToCloud()
    }

    fun markCardLearning() {
        val s = _flashcardState.value
        val currentPhrase = s.currentDeck.phrases.getOrNull(s.currentCardIndex) ?: return
        statsRepo.recordPhraseAttempt(currentPhrase.id, isCorrect = false)
        viewModelScope.launch {
            cloudRepo.saveLearnedPhrase(currentPhrase, masteryLevel = 1)
        }
        val newStreaks = s.cardStreaks.toMutableMap().apply {
            put(currentPhrase.id, 0)
        }
        val isFirstTimeSent = !s.cardsSentToStillLearning.contains(currentPhrase.id)
        val newCardsSent = s.cardsSentToStillLearning + currentPhrase.id
        val nextIdx = (s.currentCardIndex + 1) % s.currentDeck.phrases.size
        val nextCardId = s.currentDeck.phrases.getOrNull(nextIdx)?.id
        val newPresCount = s.cardPresentationCount.toMutableMap()
        if (nextCardId != null) {
            newPresCount[nextCardId] = (newPresCount[nextCardId] ?: 0) + 1
        }
        val historyItem = FlashcardSwipeHistory(
            phraseId = currentPhrase.id,
            cardIndex = s.currentCardIndex,
            wasKnown = false,
            wasFirstStillLearning = isFirstTimeSent
        )
        _flashcardState.value = s.copy(
            currentCardIndex = nextIdx,
            isFlipped = false,
            learnedInSession = s.learnedInSession - currentPhrase.id,
            stillLearningInSession = s.stillLearningInSession + currentPhrase.id,
            cardsSentToStillLearning = newCardsSent,
            cardPresentationCount = newPresCount,
            swipeHistory = s.swipeHistory + historyItem,
            cardStreaks = newStreaks
        )
        syncToCloud()
    }

    fun undoLastFlashcardSwipe() {
        val s = _flashcardState.value
        if (s.swipeHistory.isNotEmpty()) {
            val lastAction = s.swipeHistory.last()
            val updatedHistory = s.swipeHistory.dropLast(1)
            val newLearned = if (lastAction.wasKnown) {
                s.learnedInSession - lastAction.phraseId
            } else {
                s.learnedInSession
            }
            val newStillLearning = if (!lastAction.wasKnown) {
                s.stillLearningInSession - lastAction.phraseId
            } else {
                s.stillLearningInSession
            }
            val newCardsSent = if (!lastAction.wasKnown && lastAction.wasFirstStillLearning) {
                s.cardsSentToStillLearning - lastAction.phraseId
            } else {
                s.cardsSentToStillLearning
            }
            val currentCardId = s.currentDeck.phrases.getOrNull(s.currentCardIndex)?.id
            val newPresCount = s.cardPresentationCount.toMutableMap()
            if (currentCardId != null) {
                val count = newPresCount[currentCardId] ?: 1
                if (count > 1) {
                    newPresCount[currentCardId] = count - 1
                }
            }
            _flashcardState.value = s.copy(
                currentCardIndex = lastAction.cardIndex,
                isFlipped = false,
                learnedInSession = newLearned,
                stillLearningInSession = newStillLearning,
                cardsSentToStillLearning = newCardsSent,
                cardPresentationCount = newPresCount,
                swipeHistory = updatedHistory
            )
            syncToCloud()
        } else {
            val prevIdx = if (s.currentDeck.phrases.isNotEmpty()) {
                (s.currentCardIndex - 1 + s.currentDeck.phrases.size) % s.currentDeck.phrases.size
            } else 0
            _flashcardState.value = s.copy(
                currentCardIndex = prevIdx,
                isFlipped = false
            )
        }
    }

    fun advanceCardWithoutTally() {
        val s = _flashcardState.value
        if (s.currentDeck.phrases.isEmpty()) return
        val nextIdx = (s.currentCardIndex + 1) % s.currentDeck.phrases.size
        _flashcardState.value = s.copy(
            currentCardIndex = nextIdx,
            isFlipped = false
        )
    }

    fun toggleFlashcardFrontLanguage() {
        val s = _flashcardState.value
        _flashcardState.value = s.copy(
            isEnglishOnFront = !s.isEnglishOnFront,
            isFlipped = false
        )
    }

    fun shuffleCurrentFlashcardDeck() {
        val s = _flashcardState.value
        val shuffledDeck = s.currentDeck.copy(
            phrases = s.currentDeck.phrases.shuffled()
        )
        val initialCardId = shuffledDeck.phrases.getOrNull(0)?.id
        val initPresCount = if (initialCardId != null) mapOf(initialCardId to 1) else emptyMap()
        _flashcardState.value = s.copy(
            currentDeck = shuffledDeck,
            currentCardIndex = 0,
            isFlipped = false,
            isShuffled = true,
            cardPresentationCount = initPresCount,
            swipeHistory = emptyList()
        )
    }

    fun restartCurrentFlashcardDeck() {
        val s = _flashcardState.value
        val phraseIds = s.currentDeck.phrases.map { it.id }.toSet()
        statsRepo.resetDeckProgress(s.currentDeck.id, phraseIds)
        val canonicalDeck = if (s.currentDeck.id == "deck_overall") {
            DialectRepository.buildOverallDeck(statsRepo.stats.value.learnedPhraseIds.size)
        } else {
            DialectRepository.decks.find { it.id == s.currentDeck.id } ?: s.currentDeck
        }
        val resetStreaks = canonicalDeck.phrases.associate { it.id to 0 }
        val initialCardId = canonicalDeck.phrases.getOrNull(0)?.id
        val initPresCount = if (initialCardId != null) mapOf(initialCardId to 1) else emptyMap()
        _flashcardState.value = s.copy(
            currentDeck = canonicalDeck,
            currentCardIndex = 0,
            isFlipped = false,
            isShuffled = false,
            learnedInSession = emptySet(),
            stillLearningInSession = emptySet(),
            cardsSentToStillLearning = emptySet(),
            cardPresentationCount = initPresCount,
            swipeHistory = emptyList(),
            cardStreaks = resetStreaks
        )
        syncToCloud()
    }

    // Active Deck Study Question Inputs
    fun updateStudyTypedResponse(text: String) {
        _flashcardState.value = _flashcardState.value.copy(typedResponse = text)
    }

    fun selectStudyMcOption(index: Int) {
        _flashcardState.value = _flashcardState.value.copy(selectedMcOption = index)
        if (!_flashcardState.value.isQuestionEvaluated) {
            evaluateDeckStudyQuestion()
        }
    }

    fun selectStudyTrueFalse(choice: Boolean) {
        _flashcardState.value = _flashcardState.value.copy(selectedTrueFalse = choice)
        if (!_flashcardState.value.isQuestionEvaluated) {
            evaluateDeckStudyQuestion()
        }
    }

    fun updateStudyCopyInput(text: String) {
        val s = _flashcardState.value
        val currentQ = s.studyQueue.getOrNull(s.currentQuestionIndex) ?: return
        val verified = SmartGrader.matchesCopyAnswer(text, currentQ.phrase)
        _flashcardState.value = s.copy(
            copyInputText = text,
            isCopyVerified = verified
        )
    }

    /**
     * Evaluates the current question in MIXED_MASTERY, LEARN, WRITE, SPEAK, or TEST mode.
     * - If correct: increments card streak (0 -> 1 -> 2). If streak < 2 (and not in TEST mode),
     *   appends a round-2 question (switching between Written & Spoken!) to the shuffle queue so the
     *   user gets it right 2 times to reach Mastered!
     * - If wrong:
     *   1. Resets card streak to 0.
     *   2. Activates Quizlet's "Copy Answer When Wrong" (`mustCopyToContinue = true`).
     *   3. Re-inserts the phrase back into the shuffle queue so the user will be quizzed on it again
     *      until they get it right 2 times in a row!
     */
    fun evaluateDeckStudyQuestion() {
        val s = _flashcardState.value
        val currentQ = s.studyQueue.getOrNull(s.currentQuestionIndex) ?: return
        val phrase = currentQ.phrase

        val gradeResult = when (currentQ.format) {
            StudyPromptFormat.MULTIPLE_CHOICE -> {
                val isRight = s.selectedMcOption == currentQ.correctOptionIndex
                if (isRight) {
                    com.example.data.GradeResult(
                        isCorrect = true,
                        scorePercent = 100,
                        feedbackHeadline = "Correct! (+5 XP)",
                        detailedExplanation = "\"${phrase.manglish}\" = \"${phrase.english}\""
                    )
                } else {
                    com.example.data.GradeResult(
                        isCorrect = false,
                        scorePercent = 0,
                        feedbackHeadline = "Missed it! Type the answer below to lock it in:",
                        detailedExplanation = "Correct Manglish: \"${phrase.manglish}\" (${phrase.phoneticGuide})"
                    )
                }
            }

            StudyPromptFormat.TRUE_FALSE -> {
                val isRight = s.selectedTrueFalse == currentQ.isTruePair
                if (isRight) {
                    com.example.data.GradeResult(
                        isCorrect = true,
                        scorePercent = 100,
                        feedbackHeadline = "Spot on! (+5 XP)",
                        detailedExplanation = "\"${phrase.manglish}\" means \"${phrase.english}\"."
                    )
                } else {
                    com.example.data.GradeResult(
                        isCorrect = false,
                        scorePercent = 0,
                        feedbackHeadline = "Not quite! Copy the right answer to continue:",
                        detailedExplanation = "\"${phrase.english}\" is \"${phrase.manglish}\"."
                    )
                }
            }

            StudyPromptFormat.WRITTEN_MANGLISH -> {
                SmartGrader.gradeWrittenResponse(s.typedResponse, phrase)
            }

            StudyPromptFormat.SPOKEN_OUT_LOUD -> {
                val candidateSpeech = s.spokenTranscript.ifBlank { s.typedResponse }
                SmartGrader.gradeSpokenResponse(candidateSpeech, phrase)
            }
        }

        applyDeckGradeResult(currentQ, gradeResult)
    }

    /**
     * Quizlet-style "Override: I was right" button in case of a typo or microphone accent mismatch.
     */
    fun overrideStudyQuestionAsCorrect() {
        val s = _flashcardState.value
        val currentQ = s.studyQueue.getOrNull(s.currentQuestionIndex) ?: return
        val overrideGrade = com.example.data.GradeResult(
            isCorrect = true,
            isAlmostExactTypo = false,
            scorePercent = 100,
            feedbackHeadline = "Marked Correct (Override Applied!)",
            detailedExplanation = "\"${currentQ.phrase.manglish}\" counted as correct!"
        )
        applyDeckGradeResult(currentQ, overrideGrade, isOverride = true)
    }

    private fun applyDeckGradeResult(
        currentQ: DeckStudyQuestion,
        gradeResult: com.example.data.GradeResult,
        isOverride: Boolean = false
    ) {
        val s = _flashcardState.value
        val phrase = currentQ.phrase
        val mode = s.activeMode
        val isTestMode = mode == QuizletStudyMode.TEST

        val previousStreak = s.cardStreaks[phrase.id] ?: 0
        val newStreak = if (gradeResult.isCorrect) previousStreak + 1 else 0
        val updatedStreaks = s.cardStreaks.toMutableMap().apply {
            put(phrase.id, newStreak.coerceAtMost(2))
        }

        // Persist in UserStatsRepository & Cloud Firestore for Dynamic Study Plan + Known Words DB
        val masteryRecord = statsRepo.recordPhraseAttempt(phrase.id, gradeResult.isCorrect)
        val cloudLevel = when {
            masteryRecord.isKnown -> 5
            gradeResult.isCorrect -> 3
            else -> 1
        }
        viewModelScope.launch {
            cloudRepo.saveLearnedPhrase(phrase, masteryLevel = cloudLevel)
        }

        // Build updated shuffle queue
        val updatedQueue = s.studyQueue.toMutableList()
        var reshuffleIncrement = 0

        if (!isTestMode) {
            if (!gradeResult.isCorrect) {
                // USER GOT IT WRONG -> Re-insert into shuffle queue so they must get it right multiple times (2x)!
                reshuffleIncrement = 1
                // If phrase has Level 0 prerequisite building blocks that aren't mastered, also inject a quick refresher!
                for (prereqId in phrase.prerequisiteIds) {
                    val prereqPhrase = DialectRepository.allPhrases.find { it.id == prereqId }
                    val prereqRec = statsRepo.stats.value.masteryRecords[prereqId]
                    val alreadyQueued = updatedQueue.drop(s.currentQuestionIndex + 1).any { it.phrase.id == prereqId }
                    if (prereqPhrase != null && (prereqRec == null || !prereqRec.isKnown) && !alreadyQueued) {
                        val remediationQ = createDeckQuestionForPhrase(
                            phrase = prereqPhrase,
                            deck = s.currentDeck,
                            format = StudyPromptFormat.MULTIPLE_CHOICE,
                            currentStreak = 0,
                            requiredStreak = 1,
                            isRemediation = true
                        )
                        val insertPos = (s.currentQuestionIndex + 2).coerceAtMost(updatedQueue.size)
                        updatedQueue.add(insertPos, remediationQ)
                    }
                }

                // Re-insert the missed card 2 positions ahead (or at end of queue) for round 1 of 2
                val retryFormat = when (mode) {
                    QuizletStudyMode.WRITE -> StudyPromptFormat.WRITTEN_MANGLISH
                    QuizletStudyMode.SPEAK -> StudyPromptFormat.SPOKEN_OUT_LOUD
                    QuizletStudyMode.MIXED_MASTERY -> currentQ.format // Retry the format they missed first!
                    QuizletStudyMode.LEARN -> StudyPromptFormat.MULTIPLE_CHOICE // Step down difficulty when struggling!
                    else -> StudyPromptFormat.WRITTEN_MANGLISH
                }
                val retryQ = createDeckQuestionForPhrase(
                    phrase = phrase,
                    deck = s.currentDeck,
                    format = retryFormat,
                    currentStreak = 0,
                    requiredStreak = 2
                )
                val insertIdx = (s.currentQuestionIndex + 3).coerceAtMost(updatedQueue.size)
                updatedQueue.add(insertIdx, retryQ)
            } else if (newStreak < currentQ.requiredMasteryStreak) {
                // Got it right once (1/2) -> Queue its second appearance to reach 2/2 Mastery!
                val secondFormat = when (mode) {
                    QuizletStudyMode.WRITE -> StudyPromptFormat.WRITTEN_MANGLISH
                    QuizletStudyMode.SPEAK -> StudyPromptFormat.SPOKEN_OUT_LOUD
                    QuizletStudyMode.MIXED_MASTERY -> {
                        // Flip modality so they do BOTH Written AND Spoken to master the card!
                        if (currentQ.format == StudyPromptFormat.WRITTEN_MANGLISH) {
                            StudyPromptFormat.SPOKEN_OUT_LOUD
                        } else {
                            StudyPromptFormat.WRITTEN_MANGLISH
                        }
                    }
                    QuizletStudyMode.LEARN -> {
                        // Upgrade from Multiple Choice (1/2) to Active Written Recall (2/2)!
                        StudyPromptFormat.WRITTEN_MANGLISH
                    }
                    else -> StudyPromptFormat.WRITTEN_MANGLISH
                }
                val alreadyPending = updatedQueue.drop(s.currentQuestionIndex + 1).any { it.phrase.id == phrase.id }
                if (!alreadyPending) {
                    val secondPassQ = createDeckQuestionForPhrase(
                        phrase = phrase,
                        deck = s.currentDeck,
                        format = secondFormat,
                        currentStreak = newStreak,
                        requiredStreak = 2
                    )
                    updatedQueue.add(secondPassQ)
                }
            }
        }

        // In non-Test modes, if wrong, require "Copy Answer to Continue"
        val requireCopy = !gradeResult.isCorrect && !isTestMode && !isOverride

        _flashcardState.value = s.copy(
            studyQueue = updatedQueue,
            cardStreaks = updatedStreaks,
            isQuestionEvaluated = true,
            isQuestionCorrect = gradeResult.isCorrect,
            isAlmostExactTypo = gradeResult.isAlmostExactTypo,
            gradeHeadline = gradeResult.feedbackHeadline,
            gradeDetail = gradeResult.detailedExplanation,
            mustCopyToContinue = requireCopy,
            copyInputText = "",
            isCopyVerified = false,
            totalAnsweredInRound = if (isOverride) s.totalAnsweredInRound else s.totalAnsweredInRound + 1,
            totalCorrectInRound = if (gradeResult.isCorrect) s.totalCorrectInRound + 1 else s.totalCorrectInRound,
            reshuffleEventsCount = s.reshuffleEventsCount + reshuffleIncrement
        )

        if (gradeResult.isCorrect) {
            speakPhrase(phrase)
            autoAdvanceDeckJob?.cancel()
            val evaluatedIndex = s.currentQuestionIndex
            autoAdvanceDeckJob = viewModelScope.launch {
                delay(850)
                if (_flashcardState.value.currentQuestionIndex == evaluatedIndex &&
                    _flashcardState.value.isQuestionEvaluated &&
                    _flashcardState.value.isQuestionCorrect
                ) {
                    nextDeckStudyQuestion()
                }
            }
        }
        syncToCloud()
    }

    fun nextDeckStudyQuestion() {
        autoAdvanceDeckJob?.cancel()
        val s = _flashcardState.value
        if (s.mustCopyToContinue && !s.isCopyVerified) return

        val nextIdx = s.currentQuestionIndex + 1
        if (nextIdx >= s.studyQueue.size) {
            statsRepo.incrementQuizCompleted()
            _flashcardState.value = s.copy(
                isRoundComplete = true,
                mustCopyToContinue = false
            )
        } else {
            _flashcardState.value = s.copy(
                currentQuestionIndex = nextIdx,
                typedResponse = "",
                selectedMcOption = null,
                selectedTrueFalse = null,
                spokenTranscript = "",
                isQuestionEvaluated = false,
                isQuestionCorrect = false,
                isAlmostExactTypo = false,
                gradeHeadline = "",
                gradeDetail = "",
                mustCopyToContinue = false,
                copyInputText = "",
                isCopyVerified = false
            )
        }
        syncToCloud()
    }

    // === Quizlet Match Mode Logic ===
    fun onMatchTileTapped(tileId: String) {
        val s = _flashcardState.value
        val tappedTile = s.matchTiles.find { it.id == tileId } ?: return
        if (tappedTile.isMatched) return

        val prevSelectedId = s.selectedMatchTileId
        if (prevSelectedId == null) {
            _flashcardState.value = s.copy(
                selectedMatchTileId = tileId,
                mismatchedTileIds = emptySet()
            )
            return
        }

        if (prevSelectedId == tileId) {
            _flashcardState.value = s.copy(selectedMatchTileId = null)
            return
        }

        val prevTile = s.matchTiles.find { it.id == prevSelectedId } ?: return
        if (prevTile.phraseId == tappedTile.phraseId && prevTile.isEnglish != tappedTile.isEnglish) {
            // Match found!
            val updatedTiles = s.matchTiles.map {
                if (it.id == prevTile.id || it.id == tappedTile.id) it.copy(isMatched = true) else it
            }
            val allCleared = updatedTiles.all { it.isMatched }
            val phrase = DialectRepository.allPhrases.find { it.id == tappedTile.phraseId }
            if (phrase != null) {
                statsRepo.recordPhraseAttempt(phrase.id, isCorrect = true)
                speakPhrase(phrase)
            }
            val best = if (allCleared) {
                val cur = s.matchElapsedTenths
                if (s.matchBestTenths == null || cur < s.matchBestTenths) cur else s.matchBestTenths
            } else s.matchBestTenths

            _flashcardState.value = s.copy(
                matchTiles = updatedTiles,
                selectedMatchTileId = null,
                mismatchedTileIds = emptySet(),
                isMatchFinished = allCleared,
                matchBestTenths = best
            )
            if (allCleared) {
                statsRepo.incrementQuizCompleted()
                syncToCloud()
            }
        } else {
            // Mismatch! Add +1.0s (10 tenths) time penalty and flash red
            _flashcardState.value = s.copy(
                selectedMatchTileId = null,
                mismatchedTileIds = setOf(prevTile.id, tappedTile.id),
                matchElapsedTenths = s.matchElapsedTenths + 10
            )
        }
    }

    fun tickMatchTimer() {
        val s = _flashcardState.value
        if (s.activeMode == QuizletStudyMode.MATCH && !s.isMatchFinished) {
            _flashcardState.value = s.copy(matchElapsedTenths = s.matchElapsedTenths + 1)
        }
    }

    // =========================================================================
    // LEGACY / QUICK DAILY DRILLS TAB (With Copy Answer When Wrong & Shuffle Back)
    // =========================================================================

    fun startNewQuizSession() {
        autoAdvanceQuizJob?.cancel()
        _quizState.value = QuizState(
            currentIndex = 0,
            questions = DialectRepository.quizQuestions,
            typedInput = "",
            selectedTileIndices = emptyList(),
            selectedOptionIndex = null,
            isEvaluated = false,
            isCorrect = false,
            feedbackMessage = "",
            speechTranscription = "",
            mustCopyAnswer = false,
            copyAnswerInput = "",
            isCopyAnswerUnlocked = false,
            reshuffledCount = 0,
            isFinished = false
        )
    }

    fun updateQuizTypedInput(text: String) {
        _quizState.value = _quizState.value.copy(typedInput = text)
    }

    fun updateQuizCopyInput(text: String) {
        val state = _quizState.value
        val q = state.questions.getOrNull(state.currentIndex) ?: return
        val unlocked = SmartGrader.matchesCopyAnswer(text, q.phrase)
        _quizState.value = state.copy(
            copyAnswerInput = text,
            isCopyAnswerUnlocked = unlocked
        )
    }

    fun toggleWordTile(index: Int) {
        val current = _quizState.value.selectedTileIndices.toMutableList()
        if (current.contains(index)) {
            current.remove(index)
        } else {
            current.add(index)
        }
        _quizState.value = _quizState.value.copy(selectedTileIndices = current)
    }

    fun selectQuizOption(index: Int) {
        _quizState.value = _quizState.value.copy(selectedOptionIndex = index)
        if (!_quizState.value.isEvaluated) {
            evaluateCurrentQuestion()
        }
    }

    fun evaluateCurrentQuestion() {
        val state = _quizState.value
        val q = state.questions.getOrNull(state.currentIndex) ?: return

        var isCorrect = false
        var feedback = ""

        when (q.type) {
            QuizType.MANGLISH_TYPING -> {
                val res = SmartGrader.gradeWrittenResponse(state.typedInput, q.phrase)
                isCorrect = res.isCorrect
                feedback = "${res.feedbackHeadline} — ${res.detailedExplanation}"
            }

            QuizType.SPEAKING_PRACTICE -> {
                val res = SmartGrader.gradeSpokenResponse(
                    state.speechTranscription.ifBlank { state.typedInput },
                    q.phrase
                )
                isCorrect = res.isCorrect
                feedback = "${res.feedbackHeadline} — ${res.detailedExplanation}"
            }

            QuizType.WORD_TILES -> {
                val assembled = state.selectedTileIndices.map { q.correctTileOrder[it] }
                isCorrect = assembled == q.correctTileOrder
                feedback = if (isCorrect) {
                    "Correct order! '${q.phrase.manglish}'"
                } else {
                    "Correct order: ${q.correctTileOrder.joinToString(" ")}"
                }
            }

            QuizType.DIALECT_CHOICE -> {
                val chosenText = q.options.getOrNull(state.selectedOptionIndex ?: -1)
                isCorrect = (chosenText != null && (chosenText.equals(q.phrase.manglish, ignoreCase = true) || state.selectedOptionIndex == 0))
                feedback = if (isCorrect) {
                    "Spot on! '${q.options.getOrNull(0) ?: q.phrase.manglish}'"
                } else {
                    "The correct word is '${q.phrase.manglish}'!"
                }
            }
        }

        val rec = statsRepo.recordPhraseAttempt(q.phrase.id, isCorrect)
        viewModelScope.launch {
            cloudRepo.saveLearnedPhrase(q.phrase, masteryLevel = if (rec.isKnown) 5 else if (isCorrect) 3 else 1)
        }

        val updatedQuestions = state.questions.toMutableList()
        var newReshuffled = state.reshuffledCount
        val mustCopy = false

        if (!isCorrect) {
            // Add back to the shuffle queue so the user practices it again in this session!
            updatedQuestions.add(q.copy(id = "${q.id}_retry_${System.currentTimeMillis()}"))
            newReshuffled += 1
        } else {
            speakPhrase(q.phrase)
            autoAdvanceQuizJob?.cancel()
            val evaluatedIndex = state.currentIndex
            autoAdvanceQuizJob = viewModelScope.launch {
                delay(850)
                if (_quizState.value.currentIndex == evaluatedIndex &&
                    _quizState.value.isEvaluated &&
                    _quizState.value.isCorrect
                ) {
                    nextQuizQuestion()
                }
            }
        }

        _quizState.value = state.copy(
            questions = updatedQuestions,
            isEvaluated = true,
            isCorrect = isCorrect,
            isSkipped = false,
            feedbackMessage = feedback,
            mustCopyAnswer = mustCopy,
            copyAnswerInput = "",
            isCopyAnswerUnlocked = !mustCopy,
            reshuffledCount = newReshuffled
        )
        syncToCloud()
    }

    fun skipCurrentQuizQuestion() {
        autoAdvanceQuizJob?.cancel()
        val state = _quizState.value
        val q = state.questions.getOrNull(state.currentIndex) ?: return

        val rec = statsRepo.recordPhraseAttempt(q.phrase.id, false)
        viewModelScope.launch {
            cloudRepo.saveLearnedPhrase(q.phrase, masteryLevel = if (rec.isKnown) 5 else 1)
        }

        // Add word back to shuffle queue for the next round
        val updatedQuestions = state.questions.toMutableList()
        updatedQuestions.add(q.copy(id = "${q.id}_skipped_${System.currentTimeMillis()}"))

        _quizState.value = state.copy(
            questions = updatedQuestions,
            isEvaluated = true,
            isCorrect = false,
            isSkipped = true,
            feedbackMessage = "Skipped — '${q.phrase.manglish}'",
            mustCopyAnswer = false,
            copyAnswerInput = "",
            isCopyAnswerUnlocked = true,
            reshuffledCount = state.reshuffledCount + 1
        )
        syncToCloud()
    }

    fun nextQuizQuestion() {
        autoAdvanceQuizJob?.cancel()
        val state = _quizState.value
        if (state.mustCopyAnswer && !state.isCopyAnswerUnlocked) return

        val nextIdx = state.currentIndex + 1
        if (nextIdx >= state.questions.size) {
            statsRepo.incrementQuizCompleted()
            _quizState.value = state.copy(isFinished = true)
        } else {
            _quizState.value = state.copy(
                currentIndex = nextIdx,
                typedInput = "",
                selectedTileIndices = emptyList(),
                selectedOptionIndex = null,
                isEvaluated = false,
                isCorrect = false,
                isSkipped = false,
                feedbackMessage = "",
                speechTranscription = "",
                mustCopyAnswer = false,
                copyAnswerInput = "",
                isCopyAnswerUnlocked = false
            )
        }
        syncToCloud()
    }

    // === AI Tutor & Roleplay ===
    fun selectRoleplayScenario(scenario: RoleplayScenario) {
        _aiTutorState.value = AiTutorState(
            currentScenario = scenario,
            messages = listOf(scenario.initialMessage),
            inputText = "",
            isLoading = false,
            error = null
        )
        syncToCloud()
    }

    fun updateAiTutorInput(text: String) {
        _aiTutorState.value = _aiTutorState.value.copy(inputText = text)
    }

    fun sendTutorMessage(textOverride: String? = null) {
        val textToSend = (textOverride ?: _aiTutorState.value.inputText).trim()
        if (textToSend.isBlank() || _aiTutorState.value.isLoading) return

        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            isUser = true,
            manglishText = textToSend,
            malayalamText = "",
            englishText = ""
        )

        val updatedMessages = _aiTutorState.value.messages + userMsg
        _aiTutorState.value = _aiTutorState.value.copy(
            messages = updatedMessages,
            inputText = "",
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val reply = aiService.sendRoleplayMessage(
                    scenario = _aiTutorState.value.currentScenario,
                    conversationHistory = updatedMessages,
                    userMessage = textToSend
                )
                _aiTutorState.value = _aiTutorState.value.copy(
                    messages = _aiTutorState.value.messages + reply,
                    isLoading = false
                )
                statsRepo.addXp(10)
                syncToCloud()
                audioHelper.speak(reply.malayalamText, reply.manglishText)
            } catch (e: Exception) {
                _aiTutorState.value = _aiTutorState.value.copy(
                    isLoading = false,
                    error = "Could not reach tutor: ${e.localizedMessage}"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoAdvanceQuizJob?.cancel()
        autoAdvanceDeckJob?.cancel()
        audioHelper.shutdown()
    }
}
