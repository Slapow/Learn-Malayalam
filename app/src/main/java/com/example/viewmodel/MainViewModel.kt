package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioHelper
import com.example.data.CloudProgressRepository
import com.example.data.DialectRepository
import com.example.data.DrillItemRepository
import com.example.data.SmartGrader
import com.example.data.TutorAiService
import com.example.data.TranslatorAiService
import com.example.data.TranslationResult
import com.example.data.mergeTranslationResults
import com.example.data.translationDelayMillis
import com.example.data.translationQueryKey
import com.example.data.UserStatsRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
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
    val levelIndex: Int = 0,
    val levelTitle: String = "Novice Level 1",
    val currentRoundNumber: Int = 1, // 1, 2, or 3
    val practiceQuestionType: QuizType? = null,
    val currentIndex: Int = 0,
    val questions: List<QuizQuestion> = emptyList(),
    val fixedRoundTargetCount: Int = 12,
    val roundCorrectCount: Int = 0,
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
    val recycleCounts: Map<String, Int> = emptyMap(),
    val showRoundCheckpoint: Boolean = false,
    val completedRoundNumber: Int = 0,
    val isLoading: Boolean = false,
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
    val hasStartedSession: Boolean = false,
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
    val isMatchFinished: Boolean = false,
    val continueToDrillsLevelIndex: Int? = null
)

data class AiTutorState(
    val currentScenario: RoleplayScenario = DialectRepository.roleplayScenarios[0],
    val messages: List<ChatMessage> = listOf(DialectRepository.roleplayScenarios[0].initialMessage),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class TranslatorState(
    val inputText: String = "",
    val results: List<TranslationResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAiConfigured: Boolean = true
)

class MainViewModel(
    private val cloudRepo: CloudProgressRepository,
    private val statsRepo: UserStatsRepository,
    private val drillRepository: DrillItemRepository,
    val audioHelper: AudioHelper,
    private val currentUserId: String,
    private val userDisplayName: String
) : ViewModel() {

    private val aiService = TutorAiService()
    private val sessionOrder = SessionOrder()
    private val translatorService = TranslatorAiService()
    private var translationJob: Job? = null
    private var translationGeneration = 0L
    private var lastTranslationQuery: String? = null
    private val _translatorState = MutableStateFlow(TranslatorState(isAiConfigured = translatorService.isConfigured))
    val translatorState: StateFlow<TranslatorState> = _translatorState.asStateFlow()

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
    val speechLevel: StateFlow<Float> = audioHelper.speechLevel

    private val _cloudSyncStatus = MutableStateFlow("☁️ Synced with Cloud Firestore")
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    private val _lastSyncedDevice = MutableStateFlow("Android")
    val lastSyncedDevice: StateFlow<String> = _lastSyncedDevice.asStateFlow()

    private val _lastActiveCloudScreen = MutableStateFlow("HOME")
    val lastActiveCloudScreen: StateFlow<String> = _lastActiveCloudScreen.asStateFlow()

    private val _tierGraduationState = MutableStateFlow<TierGraduationExamState?>(null)
    val tierGraduationState: StateFlow<TierGraduationExamState?> = _tierGraduationState.asStateFlow()

    private val _isDevModeEnabled = MutableStateFlow(false)
    val isDevModeEnabled: StateFlow<Boolean> = _isDevModeEnabled.asStateFlow()

    fun setDevModeEnabled(enabled: Boolean) {
        _isDevModeEnabled.value = enabled
    }

    fun devEndMode() {
        _isDevModeEnabled.value = false
    }

    private val _levelUpEvent = MutableStateFlow<LevelUpEvent?>(null)
    val levelUpEvent: StateFlow<LevelUpEvent?> = _levelUpEvent.asStateFlow()

    fun dismissLevelUpEvent() {
        _levelUpEvent.value = null
    }

    fun checkForLevelUp() {
        val currentStats = statsRepo.stats.value
        val currentLevel = currentStats.unlockedLevelIndex
        val status = DialectRepository.getLevelMasteryStatus(currentLevel, currentStats)
        if (status.isLevelMastered && currentLevel + 1 < DialectRepository.studyPlanStages.size) {
            val nextLevel = currentLevel + 1
            val completedStage = DialectRepository.studyPlanStages[currentLevel]
            val newStage = DialectRepository.studyPlanStages[nextLevel]
            val newDeck = DialectRepository.decks.find { it.id == newStage.deckId }

            statsRepo.setUnlockedLevelIndex(nextLevel)

            _levelUpEvent.value = LevelUpEvent(
                newLevelIndex = nextLevel,
                rankTitle = newStage.rankTitle,
                completedLevelTitle = completedStage.rankTitle,
                unlockedDeckTitle = newDeck?.title ?: newStage.topicTitle
            )
            syncToCloud()
        }
    }

    fun setDevLevel(levelIndex: Int) {
        val safeIndex = levelIndex.coerceIn(0, DialectRepository.studyPlanStages.size - 1)
        statsRepo.setUnlockedLevelIndex(safeIndex)
        val stage = DialectRepository.studyPlanStages[safeIndex]
        val stageDeck = DialectRepository.decks.find { it.id == stage.deckId } ?: DialectRepository.decks.first()
        _flashcardState.value = _flashcardState.value.copy(
            currentDeck = stageDeck,
            currentCardIndex = 0,
            isFlipped = false
        )
        syncToCloud()
    }

    fun setDevStreak(days: Int) {
        statsRepo.setCustomStreak(days)
        syncToCloud()
    }

    fun queueDevLevelUp(isDrill: Boolean) {
        val currentLevel = statsRepo.stats.value.unlockedLevelIndex
        val nextLevel = (currentLevel + 1).coerceAtMost(DialectRepository.studyPlanStages.size - 1)
        val completedStage = DialectRepository.studyPlanStages[currentLevel]
        val newStage = DialectRepository.studyPlanStages[nextLevel]
        val newDeck = DialectRepository.decks.find { it.id == newStage.deckId }
        val modeLabel = if (isDrill) "3-Round Drill" else "Flashcard Deck"

        statsRepo.setUnlockedLevelIndex(nextLevel)

        _levelUpEvent.value = LevelUpEvent(
            newLevelIndex = nextLevel,
            rankTitle = newStage.rankTitle,
            completedLevelTitle = "${completedStage.rankTitle} ($modeLabel)",
            unlockedDeckTitle = newDeck?.title ?: newStage.topicTitle,
        )
        syncToCloud()
    }

    // --- Dev Mode Quick Actions for Drills ---
    fun devGetQuizQuestionCorrect() {
        autoAdvanceQuizJob?.cancel()
        val state = _quizState.value
        val currentQ = state.questions.getOrNull(state.currentIndex) ?: return

        val rec = statsRepo.recordPhraseTouch(
            phraseId = currentQ.phrase.id,
            touch = currentQ.touchLevel.coerceAtLeast(state.currentRoundNumber),
            isCorrect = true
        )
        viewModelScope.launch {
            cloudRepo.saveLearnedPhrase(
                currentQ.phrase,
                masteryLevel = if (rec.touchLevel >= 3) 5 else 3
            )
        }

        val newCorrectCount = if (state.isCorrect) state.roundCorrectCount else state.roundCorrectCount + 1
        val correctedState = if (state.isEvaluated && !state.isCorrect) state.undoCurrentRecycle() else state

        _quizState.value = correctedState.copy(
            isEvaluated = true,
            isCorrect = true,
            isSkipped = false,
            roundCorrectCount = newCorrectCount,
            feedbackMessage = "Marked Correct (Dev Mode Applied!)",
            mustCopyAnswer = false,
            copyAnswerInput = "",
            isCopyAnswerUnlocked = true
        )
        syncToCloud()

        nextQuizQuestion()
    }

    fun devSkipToLastQuizQuestion() {
        autoAdvanceQuizJob?.cancel()
        val state = _quizState.value
        if (state.questions.isEmpty()) return

        if (state.currentIndex >= state.questions.size - 1) {
            devGetQuizQuestionCorrect()
            return
        }

        val targetRound = state.currentRoundNumber.coerceIn(1, 3)
        val lastIndex = state.questions.size - 1

        for (i in 0 until lastIndex) {
            val q = state.questions[i]
            val rec = statsRepo.recordPhraseTouch(
                phraseId = q.phrase.id,
                touch = q.touchLevel.coerceAtLeast(targetRound),
                isCorrect = true
            )
            viewModelScope.launch {
                cloudRepo.saveLearnedPhrase(
                    q.phrase,
                    masteryLevel = if (rec.touchLevel >= 3) 5 else 3
                )
            }
        }

        val cleanedQuestions = state.questions.take(lastIndex + 1)

        _quizState.value = state.copy(
            questions = cleanedQuestions,
            currentIndex = lastIndex,
            roundCorrectCount = state.fixedRoundTargetCount.coerceAtLeast(lastIndex),
            reshuffledCount = 0,
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
        syncToCloud()
    }

    // --- Dev Mode Quick Actions for Deck Study Modes ---
    fun devGetDeckStudyQuestionCorrect() {
        autoAdvanceDeckJob?.cancel()
        val s = _flashcardState.value
        val currentQ = s.studyQueue.getOrNull(s.currentQuestionIndex) ?: return
        val phrase = currentQ.phrase

        val updatedStreaks = s.cardStreaks.toMutableMap().apply {
            put(phrase.id, 2)
        }

        statsRepo.recordPhraseAttempt(phrase.id, isCorrect = true)
        viewModelScope.launch {
            cloudRepo.saveLearnedPhrase(phrase, masteryLevel = 5)
        }

        val isDeckMastered = s.currentDeck.phrases.isNotEmpty() && s.currentDeck.phrases.all {
            statsRepo.stats.value.learnedPhraseIds.contains(it.id) || (updatedStreaks[it.id] ?: 0) >= 2
        }
        if (isDeckMastered) {
            statsRepo.recordDeckMastered(s.currentDeck.id)
        }
        checkForLevelUp()

        val nextIdx = s.currentQuestionIndex + 1
        if (nextIdx >= s.studyQueue.size) {
            statsRepo.incrementQuizCompleted()
            _flashcardState.value = s.copy(
                cardStreaks = updatedStreaks,
                totalCorrectInRound = s.totalCorrectInRound + 1,
                totalAnsweredInRound = s.totalAnsweredInRound + 1,
                isRoundComplete = true,
                mustCopyToContinue = false,
                isQuestionEvaluated = false,
                isQuestionCorrect = false
            )
        } else {
            _flashcardState.value = s.copy(
                currentQuestionIndex = nextIdx,
                cardStreaks = updatedStreaks,
                totalCorrectInRound = s.totalCorrectInRound + 1,
                totalAnsweredInRound = s.totalAnsweredInRound + 1,
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

    fun devSkipToLastDeckStudyQuestion() {
        autoAdvanceDeckJob?.cancel()
        val s = _flashcardState.value
        if (s.studyQueue.isEmpty()) return

        if (s.currentQuestionIndex >= s.studyQueue.size - 1) {
            devGetDeckStudyQuestionCorrect()
            return
        }

        val lastIndex = s.studyQueue.size - 1
        val updatedStreaks = s.cardStreaks.toMutableMap()

        for (i in 0 until lastIndex) {
            val q = s.studyQueue[i]
            updatedStreaks[q.phrase.id] = 2
            statsRepo.recordPhraseAttempt(q.phrase.id, isCorrect = true)
            viewModelScope.launch {
                cloudRepo.saveLearnedPhrase(q.phrase, masteryLevel = 5)
            }
        }

        _flashcardState.value = s.copy(
            currentQuestionIndex = lastIndex,
            cardStreaks = updatedStreaks,
            totalCorrectInRound = lastIndex,
            totalAnsweredInRound = lastIndex,
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
        syncToCloud()
    }

    fun devSkipToLastFlashcard() {
        val s = _flashcardState.value
        val phrases = s.currentDeck.phrases
        if (phrases.isEmpty()) return

        if (s.currentCardIndex >= phrases.size - 1) {
            markCardKnown()
            return
        }

        val lastIndex = phrases.size - 1
        val newLearned = s.learnedInSession.toMutableSet()
        val newStreaks = s.cardStreaks.toMutableMap()

        for (i in 0 until lastIndex) {
            val p = phrases[i]
            newLearned.add(p.id)
            newStreaks[p.id] = 2
            statsRepo.recordPhraseAttempt(p.id, isCorrect = true)
            viewModelScope.launch {
                cloudRepo.saveLearnedPhrase(p, masteryLevel = 5)
            }
        }

        _flashcardState.value = s.copy(
            currentCardIndex = lastIndex,
            isFlipped = false,
            learnedInSession = newLearned,
            stillLearningInSession = s.stillLearningInSession - newLearned,
            cardStreaks = newStreaks
        )
        syncToCloud()
    }

    fun devGetMatchCorrect() {
        val s = _flashcardState.value
        if (s.matchTiles.isEmpty() || s.isMatchFinished) return

        val unmatchedEn = s.matchTiles.firstOrNull { !it.isMatched && it.isEnglish } ?: return
        val unmatchedMl = s.matchTiles.firstOrNull { !it.isMatched && !it.isEnglish && it.phraseId == unmatchedEn.phraseId } ?: return

        val updatedTiles = s.matchTiles.map {
            if (it.id == unmatchedEn.id || it.id == unmatchedMl.id) it.copy(isMatched = true) else it
        }
        val allCleared = updatedTiles.all { it.isMatched }
        val phrase = DialectRepository.allPhrases.find { it.id == unmatchedEn.phraseId }
        if (phrase != null) {
            statsRepo.recordPhraseAttempt(phrase.id, isCorrect = true)
        }

        _flashcardState.value = s.copy(
            matchTiles = updatedTiles,
            selectedMatchTileId = null,
            mismatchedTileIds = emptySet(),
            isMatchFinished = allCleared
        )
        if (allCleared) {
            statsRepo.incrementQuizCompleted()
            syncToCloud()
        }
    }

    fun devSkipToLastMatch() {
        val s = _flashcardState.value
        if (s.matchTiles.isEmpty()) return

        val updatedTiles = s.matchTiles.map { it.copy(isMatched = true) }
        for (t in s.matchTiles.filter { it.isEnglish }) {
            statsRepo.recordPhraseAttempt(t.phraseId, isCorrect = true)
        }

        _flashcardState.value = s.copy(
            matchTiles = updatedTiles,
            selectedMatchTileId = null,
            mismatchedTileIds = emptySet(),
            isMatchFinished = true
        )
        statsRepo.incrementQuizCompleted()
        syncToCloud()
    }

    fun devGetFlashcardsScreenCorrect() {
        when (_flashcardState.value.activeMode) {
            QuizletStudyMode.FLASHCARDS -> markCardKnown()
            QuizletStudyMode.MATCH -> devGetMatchCorrect()
            else -> devGetDeckStudyQuestionCorrect()
        }
    }

    fun devSkipToLastFlashcardsScreenQuestion() {
        when (_flashcardState.value.activeMode) {
            QuizletStudyMode.FLASHCARDS -> devSkipToLastFlashcard()
            QuizletStudyMode.MATCH -> devSkipToLastMatch()
            else -> devSkipToLastDeckStudyQuestion()
        }
    }

    fun devSkipToLastGraduationPrompt() {
        val state = _tierGraduationState.value ?: return
        if (state.prompts.isEmpty()) return

        if (state.currentPromptIndex >= state.prompts.size - 1) {
            evaluateGraduationPrompt(overridePassed = true)
            nextGraduationPrompt()
            return
        }

        val lastIndex = state.prompts.size - 1
        _tierGraduationState.value = state.copy(
            currentPromptIndex = lastIndex,
            scoreCount = lastIndex,
            spokenTranscript = "",
            isEvaluated = false,
            isPromptPassed = false,
            feedbackMessage = "Dev Mode: Skipped to final prompt"
        )
    }

    private var autoAdvanceQuizJob: Job? = null
    private var autoAdvanceDeckJob: Job? = null
    private var drillSessionGeneration = 0

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
                        statsRepo.syncFromCloud(
                            streakDays = cloudProgress.streakDays.toInt(),
                            quizCount = cloudProgress.completedQuizCount.toInt(),
                            spokenCount = cloudProgress.spokenPhrasesCount.toInt()
                        )
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
        val newStreak = json.optInt("streakDays", statsRepo.stats.value.streakDays)
        val newQuizCount = json.optInt("completedQuizCount", statsRepo.stats.value.completedQuizCount)
        val newSpoken = json.optInt("spokenPhrasesCount", statsRepo.stats.value.spokenPhrasesCount)
        statsRepo.syncFromCloud(newStreak, newQuizCount, newSpoken)
        _lastSyncedDevice.value = "Laptop Web"
        _lastActiveCloudScreen.value = json.optString("activeScreen", _lastActiveCloudScreen.value)
        syncToCloud()
    }

    fun navigateTo(screen: Screen) {
        autoAdvanceQuizJob?.cancel()
        autoAdvanceDeckJob?.cancel()
        if (screen == Screen.FLASHCARDS) {
            val curDeck = _flashcardState.value.currentDeck
            val currentStage = DialectRepository.getCurrentStage(statsRepo.stats.value)
            val currentLevelDeck = DialectRepository.decks.find { it.id == currentStage.deckId } ?: DialectRepository.decks.first()
            if (curDeck.id == "deck_level0_words" && currentStage.deckId != "deck_level0_words") {
                _flashcardState.value = _flashcardState.value.copy(
                    currentDeck = currentLevelDeck,
                    currentCardIndex = 0,
                    isFlipped = false
                )
            }
        }
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
        initializeDeckStudySession(deck, mode)
        _currentScreen.value = Screen.FLASHCARDS
        syncToCloud()
    }

    fun openLevelDeckBeforeDrills(deck: FlashcardDeck, levelIndex: Int) {
        initializeDeckStudySession(deck, QuizletStudyMode.FLASHCARDS)
        _flashcardState.value = _flashcardState.value.copy(continueToDrillsLevelIndex = levelIndex)
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

    fun speakEnglish(text: String) {
        audioHelper.speakEnglish(text)
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
        _speechStatus.value = "Opening microphone..."

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

                // Auto-evaluate when speech is captured!
                if (!_quizState.value.isEvaluated && _quizState.value.questions.getOrNull(_quizState.value.currentIndex)?.type == QuizType.SPEAKING_PRACTICE) {
                    evaluateCurrentQuestion()
                } else if (!_flashcardState.value.isQuestionEvaluated && _flashcardState.value.activeMode == QuizletStudyMode.SPEAK) {
                    evaluateDeckStudyQuestion()
                }
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
        val prompts = sessionOrder.shuffled(
            "graduation:${tier.tierId}", DialectRepository.getGraduationPromptsForTier(tier)
        ) { it.phrase.id }
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
        initializeDeckStudySession(_flashcardState.value.currentDeck, mode)
        syncToCloud()
    }

    fun initializeDeckStudySession(deck: FlashcardDeck, mode: QuizletStudyMode) {
        autoAdvanceDeckJob?.cancel()
        val sessionKey = "deck:${deck.id}:${mode.name}"
        // Match records the first visible tile, rather than the first sampled phrase.
        val sessionDeck = if (mode == QuizletStudyMode.MATCH) deck else deck.copy(
            phrases = sessionOrder.shuffled(sessionKey, deck.phrases) { it.id }
        )
        val initialStreaks = deck.phrases.associate { it.id to 0 }
        when (mode) {
            QuizletStudyMode.FLASHCARDS -> {
                val initialCardId = sessionDeck.phrases.firstOrNull()?.id
                val initPresCount = if (initialCardId != null) mapOf(initialCardId to 1) else emptyMap()
                _flashcardState.value = FlashcardState(
                    currentDeck = sessionDeck,
                    activeMode = mode,
                    currentCardIndex = 0,
                    isFlipped = false,
                    isEnglishOnFront = _flashcardState.value.isEnglishOnFront,
                    isShuffled = true,
                    learnedInSession = emptySet(),
                    stillLearningInSession = emptySet(),
                    cardsSentToStillLearning = emptySet(),
                    cardPresentationCount = initPresCount,
                    swipeHistory = emptyList(),
                    cardStreaks = initialStreaks
                )
            }

            QuizletStudyMode.MATCH -> {
                val samplePhrases = deck.phrases.distinctBy { it.id }.shuffled().take(6)
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
                    matchTiles = sessionOrder.shuffled(sessionKey, tiles) { it.phraseId },
                    matchBestTenths = _flashcardState.value.matchBestTenths
                )
            }

            else -> {
                val queue = buildInitialStudyQueue(sessionDeck, mode, initialStreaks)
                _flashcardState.value = FlashcardState(
                    currentDeck = sessionDeck,
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
        val phrases = deck.phrases
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
        val distractors = deck.phrases.map { it.manglish }
            .filter { it.isNotBlank() && it != phrase.manglish }.distinct().shuffled().take(3)
        val mcOptions = (listOf(phrase.manglish) + distractors).shuffled()
        val correctIdx = mcOptions.indexOf(phrase.manglish).coerceAtLeast(0)

        val isTrue = distractors.isEmpty() || (phrase.id.hashCode() + currentStreak) % 2 == 0
        val tfShown = if (isTrue) {
            phrase.manglish
        } else {
            distractors.firstOrNull() ?: phrase.manglish
        }

        val promptText = when (format) {
            StudyPromptFormat.MULTIPLE_CHOICE -> phrase.english
            StudyPromptFormat.TRUE_FALSE -> "${phrase.english}\n$tfShown"
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
    fun markStudySessionStarted() {
        val state = _flashcardState.value
        if (!state.hasStartedSession) {
            _flashcardState.value = state.copy(hasStartedSession = true)
        }
    }

    fun flipCard() {
        _flashcardState.value = _flashcardState.value.copy(
            hasStartedSession = true,
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
        
        // Find next card that hasn't been learned yet
        var nextIdx = s.currentCardIndex
        var found = false
        for (i in 1..s.currentDeck.phrases.size) {
            val candidateIdx = (s.currentCardIndex + i) % s.currentDeck.phrases.size
            val candidateId = s.currentDeck.phrases[candidateIdx].id
            if (!newLearned.contains(candidateId)) {
                nextIdx = candidateIdx
                found = true
                break
            }
        }
        
        val nextCardId = if (found) s.currentDeck.phrases[nextIdx].id else null
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
            learnedInSession = s.learnedInSession + currentPhrase.id,
            hasStartedSession = true,
            stillLearningInSession = newStillLearning,
            cardPresentationCount = newPresCount,
            swipeHistory = s.swipeHistory + historyItem,
            cardStreaks = newStreaks
        )
        val isDeckFullyKnown = s.currentDeck.phrases.isNotEmpty() && s.currentDeck.phrases.all {
            newLearned.contains(it.id) || statsRepo.stats.value.learnedPhraseIds.contains(it.id)
        }
        if (isDeckFullyKnown) {
            statsRepo.recordDeckMastered(s.currentDeck.id)
        }
        checkForLevelUp()
        syncToCloud()
    }

    fun markCardLearning() {
        val s = _flashcardState.value.copy(hasStartedSession = true)
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
        
        // Find next card that hasn't been learned yet (skipping learned ones)
        var nextIdx = s.currentCardIndex
        for (i in 1..s.currentDeck.phrases.size) {
            val candidateIdx = (s.currentCardIndex + i) % s.currentDeck.phrases.size
            val candidateId = s.currentDeck.phrases[candidateIdx].id
            if (!s.learnedInSession.contains(candidateId)) {
                nextIdx = candidateIdx
                break
            }
        }
        
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
        android.util.Log.d("FlashcardSwipe", "advanceCardWithoutTally CALLED!")
        val s = _flashcardState.value
        if (s.currentDeck.phrases.isEmpty()) return
        val nextIdx = (s.currentCardIndex + 1) % s.currentDeck.phrases.size
        val nextId = s.currentDeck.phrases[nextIdx].id
        _flashcardState.value = s.copy(
            currentCardIndex = nextIdx,
            hasStartedSession = true,
            cardPresentationCount = s.cardPresentationCount + (nextId to ((s.cardPresentationCount[nextId] ?: 0) + 1)),
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
        android.util.Log.d("FlashcardSwipe", "shuffleCurrentFlashcardDeck CALLED!")
        autoAdvanceDeckJob?.cancel()
        val s = _flashcardState.value
        val shuffledDeck = s.currentDeck.copy(
            phrases = sessionOrder.shuffled(
                "deck:${s.currentDeck.id}:${QuizletStudyMode.FLASHCARDS.name}", s.currentDeck.phrases
            ) { it.id }
        )
        val initialCardId = shuffledDeck.phrases.getOrNull(0)?.id
        val initPresCount = if (initialCardId != null) mapOf(initialCardId to 1) else emptyMap()
        _flashcardState.value = s.copy(
            currentDeck = shuffledDeck,
            hasStartedSession = false,
            currentCardIndex = 0,
            isFlipped = false,
            isShuffled = true,
            cardPresentationCount = initPresCount,
            swipeHistory = emptyList()
        )
    }

    fun restartCurrentFlashcardDeck() {
        autoAdvanceDeckJob?.cancel()
        val s = _flashcardState.value
        val phraseIds = s.currentDeck.phrases.map { it.id }.toSet()
        statsRepo.resetDeckProgress(s.currentDeck.id, phraseIds)
        val canonicalDeck = if (s.currentDeck.id == "deck_overall") {
            DialectRepository.buildOverallDeck(statsRepo.stats.value.learnedPhraseIds.size)
        } else {
            DialectRepository.decks.find { it.id == s.currentDeck.id } ?: s.currentDeck
        }
        val sessionDeck = canonicalDeck.copy(
            phrases = sessionOrder.shuffled(
                "deck:${canonicalDeck.id}:${QuizletStudyMode.FLASHCARDS.name}", canonicalDeck.phrases
            ) { it.id }
        )
        val resetStreaks = sessionDeck.phrases.associate { it.id to 0 }
        val initialCardId = sessionDeck.phrases.firstOrNull()?.id
        val initPresCount = if (initialCardId != null) mapOf(initialCardId to 1) else emptyMap()
        _flashcardState.value = s.copy(
            currentDeck = sessionDeck,
            currentCardIndex = 0,
            hasStartedSession = false,
            isFlipped = false,
            isShuffled = true,
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
                        feedbackHeadline = "Correct!",
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
                        feedbackHeadline = "Spot on!",
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

        val isDeckMastered = s.currentDeck.phrases.isNotEmpty() && s.currentDeck.phrases.all {
            statsRepo.stats.value.learnedPhraseIds.contains(it.id) || (updatedStreaks[it.id] ?: 0) >= 2
        }
        if (isDeckMastered) {
            statsRepo.recordDeckMastered(s.currentDeck.id)
        }
        checkForLevelUp()

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
        val s = _flashcardState.value.copy(hasStartedSession = true)
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

    fun startNewQuizSession(
        levelIndex: Int? = null,
        round: Int? = null,
        questionType: QuizType? = null
    ) {
        autoAdvanceQuizJob?.cancel()
        val sessionGeneration = ++drillSessionGeneration
        val currentUnlocked = statsRepo.stats.value.unlockedLevelIndex
        val targetLevel = levelIndex ?: _quizState.value.levelIndex.takeIf { _quizState.value.questions.isNotEmpty() } ?: currentUnlocked
        val activeQuestionType = questionType
        val activeRound = round ?: DialectRepository.getNextDrillRound(
            targetLevel,
            statsRepo.stats.value
        )
        val stage = if (targetLevel >= 0) DialectRepository.studyPlanStages.getOrNull(targetLevel) else null
        val title = if (targetLevel == -1) "Overall Drill" else (stage?.rankTitle ?: "Novice Level ${targetLevel + 1}")
        fun buildQuestions() = DialectRepository.generateDrillQuestionsForLevelAndRound(
            targetLevel, activeRound, currentUnlocked, activeQuestionType,
            orderPhrases = { phrases ->
                sessionOrder.shuffled("drill:$targetLevel:${activeQuestionType?.name ?: "mixed"}", phrases) { it.id }
            }
        )
        _quizState.value = QuizState(
            levelIndex = targetLevel,
            currentRoundNumber = activeRound,
            practiceQuestionType = activeQuestionType,
            roundCorrectCount = 0,
            levelTitle = title,
            isLoading = true
        )
        viewModelScope.launch {
            try {
                drillRepository.seedCurriculumIfNeeded()
                if (sessionGeneration == drillSessionGeneration) {
                    val questions = buildQuestions()
                    _quizState.value = _quizState.value.copy(
                        questions = questions,
                        fixedRoundTargetCount = questions.size,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("MainVM", "Could not refresh stored review content; using the selected deck", e)
                if (sessionGeneration == drillSessionGeneration) {
                    val fallbackQuestions = buildQuestions()
                    _quizState.value = _quizState.value.copy(
                        questions = fallbackQuestions,
                        fixedRoundTargetCount = fallbackQuestions.size,
                        isLoading = false
                    )
                }
            }
        }
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
        if (state.isEvaluated) return
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
                val chosenIndex = state.selectedOptionIndex ?: -1
                isCorrect = chosenIndex == q.correctOptionIndex
                feedback = if (isCorrect) {
                    "Spot on! '${q.phrase.manglish}'"
                } else {
                    "The correct word is '${q.phrase.manglish}'!"
                }
            }
        }

        val rec = if (q.isSpacedReview) {
            statsRepo.stats.value.masteryRecords[q.phrase.id]
        } else {
            statsRepo.recordPhraseTouch(
                phraseId = q.phrase.id,
                touch = q.touchLevel,
                isCorrect = isCorrect
            )
        }
        if (q.isSpacedReview || q.touchLevel == 3) {
            viewModelScope.launch {
                try {
                    drillRepository.recordReview(q.phrase.id, isCorrect)
                } catch (e: Exception) {
                    Log.e("MainVM", "Could not update spaced repetition for ${q.phrase.id}", e)
                }
            }
        }
        if (!q.isSpacedReview) {
            viewModelScope.launch {
                cloudRepo.saveLearnedPhrase(
                    q.phrase,
                    masteryLevel = if ((rec?.touchLevel ?: 0) >= 3) 5 else if (rec?.touchLevel == 2) 3 else 1
                )
            }
        }

        var updatedState = state
        val mustCopy = false

        if (!isCorrect) {
            // Recycling & Demotion Rules:
            // - Touch 3 fail -> Demote to Touch 2 (Mastery 2). Recycle Touch 2 question 3-4 items later.
            // - Touch 1 or 2 fail -> Keep/demote to Touch 1 (Mastery 1). Recycle Touch 1 question 3-4 items later.
            val recycledQ = if (q.isSpacedReview) {
                q.copy(id = "srs_retry_${q.id}_${System.currentTimeMillis()}")
            } else {
                val recycleTouch = if (q.touchLevel >= 3) 2 else 1
                DialectRepository.generateQuestionForPhraseAndTouch(
                    phrase = q.phrase,
                    touch = recycleTouch,
                    levelIndex = state.levelIndex,
                    indexInRound = state.currentIndex,
                    questionType = state.practiceQuestionType,
                    sourceDeck = DialectRepository.getPracticeDeck(state.levelIndex, statsRepo.stats.value.unlockedLevelIndex)
                )
            }

            // Interleaving queue rule: space repeat touch 3 to 4 questions later
            updatedState = state.enqueueRecycledQuestion(recycledQ)
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

        val newCorrectCount = if (isCorrect) state.roundCorrectCount + 1 else state.roundCorrectCount

        _quizState.value = updatedState.copy(
            isEvaluated = true,
            isCorrect = isCorrect,
            roundCorrectCount = newCorrectCount,
            isSkipped = false,
            feedbackMessage = feedback,
            mustCopyAnswer = mustCopy,
            copyAnswerInput = "",
            isCopyAnswerUnlocked = !mustCopy
        )
        syncToCloud()
    }

    /**
     * "Override: I was right" button for Drills in case of a typo or microphone accent mismatch.
     */
    fun overrideQuizQuestionAsCorrect() {
        val state = _quizState.value
        val q = state.questions.getOrNull(state.currentIndex) ?: return
        if (!state.isEvaluated || state.isCorrect) return

        if (q.isSpacedReview) {
            viewModelScope.launch {
                try {
                    drillRepository.recordReview(q.phrase.id, wasCorrect = true)
                } catch (e: Exception) {
                    Log.e("MainVM", "Could not update spaced repetition for ${q.phrase.id}", e)
                }
            }
        } else {
            val rec = statsRepo.recordPhraseTouch(
                phraseId = q.phrase.id,
                touch = q.touchLevel,
                isCorrect = true
            )
            viewModelScope.launch {
                cloudRepo.saveLearnedPhrase(q.phrase, masteryLevel = if (rec.touchLevel >= 3) 5 else 3)
            }
        }

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

        _quizState.value = state.undoCurrentRecycle().copy(
            isCorrect = true,
            roundCorrectCount = state.roundCorrectCount + 1,
            feedbackMessage = "Marked Correct (Override Applied!) — '${q.phrase.manglish}'",
            mustCopyAnswer = false,
            copyAnswerInput = "",
            isCopyAnswerUnlocked = true
        )
        syncToCloud()
    }

    fun skipCurrentQuizQuestion() {
        autoAdvanceQuizJob?.cancel()
        val state = _quizState.value
        if (state.isEvaluated) return
        val q = state.questions.getOrNull(state.currentIndex) ?: return

        if (q.isSpacedReview) {
            viewModelScope.launch {
                try {
                    drillRepository.recordReview(q.phrase.id, wasCorrect = false)
                } catch (e: Exception) {
                    Log.e("MainVM", "Could not update spaced repetition for ${q.phrase.id}", e)
                }
            }
        } else {
            val rec = statsRepo.recordPhraseTouch(q.phrase.id, touch = q.touchLevel, isCorrect = false)
            viewModelScope.launch {
                cloudRepo.saveLearnedPhrase(q.phrase, masteryLevel = if (rec.touchLevel >= 3) 5 else 1)
            }
        }

        val recycledQ = if (q.isSpacedReview) {
            q.copy(id = "srs_retry_${q.id}_${System.currentTimeMillis()}")
        } else {
            val recycleTouch = if (q.touchLevel >= 3) 2 else 1
            DialectRepository.generateQuestionForPhraseAndTouch(
                phrase = q.phrase,
                touch = recycleTouch,
                levelIndex = state.levelIndex,
                indexInRound = state.currentIndex,
                questionType = state.practiceQuestionType,
                sourceDeck = DialectRepository.getPracticeDeck(state.levelIndex, statsRepo.stats.value.unlockedLevelIndex)
            )
        }
        _quizState.value = state.enqueueRecycledQuestion(recycledQ).copy(
            isEvaluated = true,
            isCorrect = false,
            isSkipped = true,
            feedbackMessage = "Skipped — '${q.phrase.manglish}'",
            mustCopyAnswer = false,
            copyAnswerInput = "",
            isCopyAnswerUnlocked = true
        )
        syncToCloud()
    }

    fun nextQuizQuestion() {
        autoAdvanceQuizJob?.cancel()
        val state = _quizState.value
        if (state.mustCopyAnswer && !state.isCopyAnswerUnlocked) return

        val nextIdx = state.currentIndex + 1
        if (nextIdx >= state.questions.size) {
            if (state.levelIndex == -1) {
                statsRepo.incrementQuizCompleted()
                _quizState.value = state.copy(isFinished = true)
            } else {
            val stage = DialectRepository.studyPlanStages.getOrNull(state.levelIndex)
            val deck = stage?.let { DialectRepository.decks.find { d -> d.id == it.deckId } }
            val levelPhrases = deck?.phrases.orEmpty()
            val records = statsRepo.stats.value.masteryRecords
            val targetRound = state.currentRoundNumber.coerceIn(1, 3)
            val allWordsCompletedRound = levelPhrases.isNotEmpty() && levelPhrases.all { phrase ->
                (records[phrase.id]?.touchLevel ?: 0) >= targetRound
            }
            val roundComplete = state.roundCorrectCount >= state.fixedRoundTargetCount &&
                allWordsCompletedRound

            if (roundComplete) {
                statsRepo.recordDrillRoundCompleted(state.levelIndex, targetRound)
                if (targetRound == 3) {
                    statsRepo.recordDrillCompletedForLevel(state.levelIndex)
                    _quizState.value = state.copy(isFinished = true)
                    checkForLevelUp()
                } else {
                    _quizState.value = state.copy(
                        showRoundCheckpoint = true,
                        completedRoundNumber = targetRound
                    )
                }
            } else {
                val incompletePhrases = levelPhrases.filter { phrase ->
                    (records[phrase.id]?.touchLevel ?: 0) < targetRound
                }
                val extraQuestions = mutableListOf<QuizQuestion>()
                incompletePhrases.forEachIndexed { i, phrase ->
                    extraQuestions.add(
                        DialectRepository.generateQuestionForPhraseAndTouch(
                            phrase,
                            targetRound,
                            state.levelIndex,
                            indexInRound = i,
                            questionType = state.practiceQuestionType
                        ).copy(recycleCount = state.recycleCounts[phrase.id] ?: 0)
                    )
                }
                val updatedQuestions = state.questions + extraQuestions
                _quizState.value = state.copy(
                    questions = updatedQuestions,
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
            }
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

    fun continueToNextRound() {
        val state = _quizState.value
        val nextRound = state.currentRoundNumber + 1
        startNewQuizSession(
            state.levelIndex,
            round = nextRound,
            questionType = state.practiceQuestionType
        )
    }

    fun updateTranslatorInput(text: String) {
        val query = translationQueryKey(text)
        if (query == translationQueryKey(_translatorState.value.inputText)) {
            _translatorState.value = _translatorState.value.copy(inputText = text)
            return
        }
        _translatorState.value = _translatorState.value.copy(
            inputText = text, results = emptyList(), isLoading = false, error = null
        )
        scheduleTranslation(translationDelayMillis(text))
    }

    fun translateInput() {
        val query = translationQueryKey(_translatorState.value.inputText)
        if (query == lastTranslationQuery && _translatorState.value.error == null) return
        scheduleTranslation(0)
    }

    private fun scheduleTranslation(waitMillis: Long) {
        translationJob?.cancel()
        val generation = ++translationGeneration
        val input = _translatorState.value.inputText.trim()
        val query = translationQueryKey(input)
        lastTranslationQuery = null
        if (input.isBlank()) return
        translationJob = viewModelScope.launch {
            delay(waitMillis)
            try {
                val matches = translatorService.vocabularyMatches(input)
                if (generation != translationGeneration) return@launch
                lastTranslationQuery = query
                _translatorState.value = _translatorState.value.copy(
                    results = matches, isLoading = translatorService.isConfigured, error = null
                )
                if (!translatorService.isConfigured) return@launch
                val alternatives = translatorService.translate(input)
                if (generation != translationGeneration) return@launch
                _translatorState.value = _translatorState.value.copy(
                    results = mergeTranslationResults(matches, alternatives), isLoading = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (generation != translationGeneration) return@launch
                _translatorState.value = _translatorState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Couldn't translate that. Please try again."
                )
            }
        }
    }

    fun dismissTranslator() {
        translationJob?.cancel()
        translationGeneration++
        lastTranslationQuery = null
        _translatorState.value = _translatorState.value.copy(isLoading = false)
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
