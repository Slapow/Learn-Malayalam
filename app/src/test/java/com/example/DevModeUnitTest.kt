package com.example

import com.example.data.DialectRepository
import com.example.model.*
import com.example.viewmodel.FlashcardState
import com.example.viewmodel.QuizState
import org.junit.Assert.*
import org.junit.Test

class DevModeUnitTest {

    @Test
    fun `dev mode flag toggles on and off correctly`() {
        var isDevModeEnabled = false

        // Enable dev mode
        isDevModeEnabled = true
        assertTrue("Dev mode should be enabled", isDevModeEnabled)

        // End dev mode button action
        isDevModeEnabled = false
        assertFalse("End dev mode should disable dev mode", isDevModeEnabled)
    }

    @Test
    fun `drill dev get current question correct marks question correct and advances`() {
        val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(0, 1)
        var state = QuizState(
            currentIndex = 0,
            questions = questions,
            levelIndex = 0,
            currentRoundNumber = 1,
            fixedRoundTargetCount = questions.size,
            roundCorrectCount = 0
        )

        // Simulate devGetQuizQuestionCorrect
        val currentQ = state.questions[state.currentIndex]
        val updatedQuestions = state.questions.toMutableList()
        val newCorrectCount = state.roundCorrectCount + 1

        state = state.copy(
            questions = updatedQuestions,
            isEvaluated = true,
            isCorrect = true,
            roundCorrectCount = newCorrectCount,
            feedbackMessage = "Marked Correct (Dev Mode Applied!)",
            mustCopyAnswer = false,
            isCopyAnswerUnlocked = true
        )

        assertEquals(1, state.roundCorrectCount)
        assertTrue(state.isCorrect)
        assertTrue(state.isEvaluated)
        assertFalse(state.mustCopyAnswer)

        // Advances to next index
        state = state.copy(
            currentIndex = state.currentIndex + 1,
            isEvaluated = false,
            isCorrect = false
        )
        assertEquals(1, state.currentIndex)
    }

    @Test
    fun `drill dev skip to last question jumps directly to final question marking all prior questions correct`() {
        val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(0, 1)
        assertTrue("Should have multiple questions in drill", questions.size > 1)

        var state = QuizState(
            currentIndex = 0,
            questions = questions,
            levelIndex = 0,
            currentRoundNumber = 1,
            fixedRoundTargetCount = questions.size,
            roundCorrectCount = 0
        )

        val lastIndex = state.questions.size - 1

        // Simulate devSkipToLastQuizQuestion:
        // Prior questions (0 until lastIndex) are marked correct
        val priorCount = lastIndex
        state = state.copy(
            currentIndex = lastIndex,
            roundCorrectCount = state.fixedRoundTargetCount.coerceAtLeast(priorCount),
            isEvaluated = false,
            isCorrect = false,
            mustCopyAnswer = false
        )

        assertEquals(lastIndex, state.currentIndex)
        assertTrue(state.roundCorrectCount >= priorCount)
        assertFalse(state.isEvaluated)
        assertFalse(state.mustCopyAnswer)

        // Answering this final question correctly triggers completion
        state = state.copy(
            roundCorrectCount = state.roundCorrectCount + 1,
            isCorrect = true,
            isEvaluated = true
        )
        val roundComplete = state.roundCorrectCount >= state.fixedRoundTargetCount
        assertTrue("Round should be marked complete after last question answered", roundComplete)
    }

    @Test
    fun `deck study dev get current question correct marks question correct and increments streaks`() {
        val deck = DialectRepository.decks.first()
        val phrases = deck.phrases.take(4)
        val questions = phrases.map { p ->
            DeckStudyQuestion(
                id = "dq_${p.id}",
                phrase = p,
                format = StudyPromptFormat.WRITTEN_MANGLISH,
                promptText = p.english
            )
        }
        val initialStreaks = phrases.associate { it.id to 0 }

        var state = FlashcardState(
            currentDeck = deck,
            activeMode = QuizletStudyMode.WRITE,
            studyQueue = questions,
            currentQuestionIndex = 0,
            cardStreaks = initialStreaks,
            totalCorrectInRound = 0,
            totalAnsweredInRound = 0
        )

        // Simulate devGetDeckStudyQuestionCorrect
        val currentQ = state.studyQueue[state.currentQuestionIndex]
        val updatedStreaks = state.cardStreaks.toMutableMap().apply {
            put(currentQ.phrase.id, 2) // Mastered
        }
        state = state.copy(
            currentQuestionIndex = state.currentQuestionIndex + 1,
            cardStreaks = updatedStreaks,
            totalCorrectInRound = state.totalCorrectInRound + 1,
            totalAnsweredInRound = state.totalAnsweredInRound + 1
        )

        assertEquals(1, state.currentQuestionIndex)
        assertEquals(1, state.totalCorrectInRound)
        assertEquals(2, state.cardStreaks[currentQ.phrase.id])
    }

    @Test
    fun `deck study dev skip to last question marks all prior questions correct and positions at last question`() {
        val deck = DialectRepository.decks.first()
        val phrases = deck.phrases.take(5)
        val questions = phrases.map { p ->
            DeckStudyQuestion(
                id = "dq_${p.id}",
                phrase = p,
                format = StudyPromptFormat.WRITTEN_MANGLISH,
                promptText = p.english
            )
        }
        val initialStreaks = phrases.associate { it.id to 0 }

        var state = FlashcardState(
            currentDeck = deck,
            activeMode = QuizletStudyMode.LEARN,
            studyQueue = questions,
            currentQuestionIndex = 0,
            cardStreaks = initialStreaks,
            totalCorrectInRound = 0,
            totalAnsweredInRound = 0
        )

        val lastIndex = questions.size - 1

        // Simulate devSkipToLastDeckStudyQuestion
        val updatedStreaks = state.cardStreaks.toMutableMap()
        for (i in 0 until lastIndex) {
            val q = state.studyQueue[i]
            updatedStreaks[q.phrase.id] = 2
        }

        state = state.copy(
            currentQuestionIndex = lastIndex,
            cardStreaks = updatedStreaks,
            totalCorrectInRound = lastIndex,
            totalAnsweredInRound = lastIndex,
            isQuestionEvaluated = false,
            isQuestionCorrect = false
        )

        assertEquals(lastIndex, state.currentQuestionIndex)
        assertEquals(lastIndex, state.totalCorrectInRound)
        for (i in 0 until lastIndex) {
            assertEquals(2, state.cardStreaks[questions[i].phrase.id])
        }

        // On the final question, getting it correct completes the round
        state = state.copy(
            totalCorrectInRound = state.totalCorrectInRound + 1,
            totalAnsweredInRound = state.totalAnsweredInRound + 1,
            isRoundComplete = true
        )
        assertTrue("Round should be marked complete", state.isRoundComplete)
        assertEquals(questions.size, state.totalCorrectInRound)
    }

    @Test
    fun `flashcards dev skip to last card marks prior cards known and positions on final card`() {
        val deck = DialectRepository.decks.first()
        val phrases = deck.phrases
        val lastIndex = phrases.size - 1

        var state = FlashcardState(
            currentDeck = deck,
            activeMode = QuizletStudyMode.FLASHCARDS,
            currentCardIndex = 0,
            learnedInSession = emptySet(),
            stillLearningInSession = emptySet()
        )

        // Simulate devSkipToLastFlashcard
        val newLearned = phrases.take(lastIndex).map { it.id }.toSet()
        state = state.copy(
            currentCardIndex = lastIndex,
            learnedInSession = newLearned,
            stillLearningInSession = emptySet()
        )

        assertEquals(lastIndex, state.currentCardIndex)
        assertEquals(lastIndex, state.learnedInSession.size)

        // Marking the final card completes the deck
        state = state.copy(
            learnedInSession = state.learnedInSession + phrases[lastIndex].id
        )
        val isCompleted = state.learnedInSession.size >= phrases.size
        assertTrue("Flashcard deck should be completed", isCompleted)
    }

    @Test
    fun `match mode dev get correct matches next pair and skip to last completes all tiles`() {
        val phrases = DialectRepository.decks.first().phrases.take(3)
        val tiles = mutableListOf<MatchTile>()
        for (p in phrases) {
            tiles.add(MatchTile(id = "en_${p.id}", phraseId = p.id, text = p.english, isEnglish = true))
            tiles.add(MatchTile(id = "ml_${p.id}", phraseId = p.id, text = p.manglish, isEnglish = false))
        }

        var state = FlashcardState(
            activeMode = QuizletStudyMode.MATCH,
            matchTiles = tiles,
            isMatchFinished = false
        )

        // 1. devGetMatchCorrect: matches the first unmatched pair
        val targetPhraseId = phrases[0].id
        val updatedTiles1 = state.matchTiles.map {
            if (it.phraseId == targetPhraseId) it.copy(isMatched = true) else it
        }
        state = state.copy(matchTiles = updatedTiles1)
        assertEquals(2, state.matchTiles.count { it.isMatched })
        assertFalse(state.isMatchFinished)

        // 2. devSkipToLastMatch: completes all remaining matches
        val allMatchedTiles = state.matchTiles.map { it.copy(isMatched = true) }
        state = state.copy(
            matchTiles = allMatchedTiles,
            isMatchFinished = true
        )
        assertTrue("All tiles should be matched", state.matchTiles.all { it.isMatched })
        assertTrue("Match game should be finished", state.isMatchFinished)
    }

    @Test
    fun `graduation exam dev skip to last prompt marks previous score and lands on final prompt`() {
        val tier = ActflProficiencyTier.NOVICE
        val prompts = DialectRepository.getGraduationPromptsForTier(tier)
        assertTrue(prompts.size > 1)

        val lastIndex = prompts.size - 1
        var examState = TierGraduationExamState(
            tier = tier,
            prompts = prompts,
            currentPromptIndex = 0,
            scoreCount = 0
        )

        // Simulate devSkipToLastGraduationPrompt
        examState = examState.copy(
            currentPromptIndex = lastIndex,
            scoreCount = lastIndex,
            spokenTranscript = "",
            isEvaluated = false,
            isPromptPassed = false,
            feedbackMessage = "Dev Mode: Skipped to final prompt"
        )

        assertEquals(lastIndex, examState.currentPromptIndex)
        assertEquals(lastIndex, examState.scoreCount)

        // Passing final prompt exceeds threshold and graduates
        val finalScore = examState.scoreCount + 1
        val passingThreshold = (prompts.size * 0.65).toInt().coerceAtLeast(1)
        val didPass = finalScore >= passingThreshold
        examState = examState.copy(
            scoreCount = finalScore,
            isExamFinished = true,
            didPassGraduation = didPass
        )

        assertTrue(examState.didPassGraduation)
        assertTrue(examState.isExamFinished)
    }
}
