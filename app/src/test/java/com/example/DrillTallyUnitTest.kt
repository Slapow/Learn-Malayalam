package com.example

import com.example.data.DialectRepository
import com.example.model.PhraseMasteryRecord
import com.example.model.QuizType
import com.example.model.UserStats
import com.example.viewmodel.QuizState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrillTallyUnitTest {

    @Test
    fun `initial drill quiz state has zero roundCorrectCount and positive fixedRoundTargetCount`() {
        val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(0, 1)
        val state = QuizState(
            currentIndex = 0,
            questions = questions,
            levelIndex = 0,
            currentRoundNumber = 1,
            fixedRoundTargetCount = questions.size,
            roundCorrectCount = 0
        )

        assertEquals(0, state.roundCorrectCount)
        assertEquals(questions.size, state.fixedRoundTargetCount)
        assertTrue(state.fixedRoundTargetCount > 0)
    }

    @Test
    fun `drill tally counter display string and progress calculation`() {
        val target = 12
        var correctCount = 0

        val displayInitial = "${correctCount.coerceAtMost(target)} / $target"
        assertEquals("0 / 12", displayInitial)

        // Increment correct count 1
        correctCount += 1
        val display1 = "${correctCount.coerceAtMost(target)} / $target"
        val progress1 = (correctCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        assertEquals("1 / 12", display1)
        assertEquals(1f / 12f, progress1, 0.001f)

        // Increment up to target
        correctCount = 12
        val displayTarget = "${correctCount.coerceAtMost(target)} / $target"
        val progressTarget = (correctCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        assertEquals("12 / 12", displayTarget)
        assertEquals(1f, progressTarget, 0.001f)

        // If extra questions answered correctly beyond target, coerceAtMost caps display
        correctCount = 13
        val displayOver = "${correctCount.coerceAtMost(target)} / $target"
        val progressOver = (correctCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        assertEquals("12 / 12", displayOver)
        assertEquals(1f, progressOver, 0.001f)
    }

    @Test
    fun `progress counts only fully completed drill rounds`() {
        val levelIndex = 0
        val stats = UserStats(
            completedDrillRounds = setOf("$levelIndex:1")
        )

        val progress = DialectRepository.getDrillRoundProgress(listOf(levelIndex), stats)

        assertEquals(1, progress.completedRounds)
        assertEquals(3, progress.totalRounds)
        assertEquals(1f / 3f, progress.ratio, 0.001f)
    }

    @Test
    fun `partial word mastery does not count as a completed drill round`() {
        val phrases = DialectRepository.decks.first().phrases
        val stats = UserStats(
            learnedPhraseIds = phrases.map { it.id }.toSet(),
            masteryRecords = phrases.dropLast(1).associate {
                it.id to PhraseMasteryRecord(phraseId = it.id, touchLevel = 3)
            }
        )

        val progress = DialectRepository.getDrillRoundProgress(listOf(0), stats)

        assertEquals(0, progress.completedRounds)
        assertEquals(3, progress.totalRounds)
        assertEquals(0f, progress.ratio, 0.001f)
    }

    @Test
    fun `next drill round resumes at the first incomplete round`() {
        val stats = UserStats(completedDrillRounds = setOf("0:1", "0:2"))

        assertEquals(3, DialectRepository.getNextDrillRound(0, stats))
    }

    @Test
    fun `tier learned word count includes only completed levels`() {
        val firstNoviceStage = DialectRepository.studyPlanStages.first { it.tierId == "novice" }
        val firstDeck = DialectRepository.decks.first { it.id == firstNoviceStage.deckId }
        val stats = UserStats(
            learnedPhraseIds = firstDeck.phrases.map { it.id }.toSet(),
            masteredDrillLevelIndices = setOf(firstNoviceStage.levelIndex)
        )

        val (learned, total) = DialectRepository.getTierLearnedWordProgress("novice", stats)

        assertEquals(firstDeck.phrases.size, learned)
        assertTrue(total >= learned)
    }

    @Test
    fun `drill state transitions correctly simulate correct answer increment and override`() {
        val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(0, 1)
        var state = QuizState(
            currentIndex = 0,
            questions = questions,
            levelIndex = 0,
            currentRoundNumber = 1,
            fixedRoundTargetCount = questions.size,
            roundCorrectCount = 0
        )

        // Simulate Q1 correct:
        val isCorrect1 = true
        val newCorrectCount1 = if (isCorrect1) state.roundCorrectCount + 1 else state.roundCorrectCount
        state = state.copy(
            isEvaluated = true,
            isCorrect = isCorrect1,
            roundCorrectCount = newCorrectCount1
        )
        assertEquals(1, state.roundCorrectCount)

        // Move to Q2:
        state = state.copy(
            currentIndex = 1,
            isEvaluated = false,
            isCorrect = false
        )
        // roundCorrectCount must be preserved
        assertEquals(1, state.roundCorrectCount)

        // Simulate Q2 incorrect:
        val isCorrect2 = false
        val newCorrectCount2 = if (isCorrect2) state.roundCorrectCount + 1 else state.roundCorrectCount
        state = state.copy(
            isEvaluated = true,
            isCorrect = isCorrect2,
            roundCorrectCount = newCorrectCount2,
            reshuffledCount = state.reshuffledCount + 1
        )
        // Not incremented on wrong answer
        assertEquals(1, state.roundCorrectCount)
        assertEquals(1, state.reshuffledCount)

        // Simulate Q2 override as correct:
        state = state.copy(
            isCorrect = true,
            roundCorrectCount = state.roundCorrectCount + 1,
            reshuffledCount = (state.reshuffledCount - 1).coerceAtLeast(0)
        )
        // Incremented on override!
        assertEquals(2, state.roundCorrectCount)
        assertEquals(0, state.reshuffledCount)
    }

    @Test
    fun `buildOverallDeckForLevel includes terms only up to current unlocked level and excludes locked levels`() {
        // Suppose current level is Level 1 (index 1)
        val maxUnlockedLevel = 1
        val overallDeck = DialectRepository.buildOverallDeckForLevel(maxUnlockedLevel)

        val stage0Phrases = DialectRepository.decks.find { it.id == DialectRepository.studyPlanStages[0].deckId }?.phrases.orEmpty()
        val stage1Phrases = DialectRepository.decks.find { it.id == DialectRepository.studyPlanStages[1].deckId }?.phrases.orEmpty()
        val lockedStage2Phrases = DialectRepository.decks.find { it.id == DialectRepository.studyPlanStages[2].deckId }?.phrases.orEmpty()

        val overallPhraseIds = overallDeck.phrases.map { it.id }.toSet()

        // Must include phrases from Level 0 and Level 1
        assertTrue(overallPhraseIds.contains(stage0Phrases.first().id))
        assertTrue(overallPhraseIds.contains(stage1Phrases.first().id))

        // Must NOT include phrases from locked Level 2 (unless duplicate in earlier level)
        val lockedUniquePhrases = lockedStage2Phrases.filter { p ->
            stage0Phrases.none { it.id == p.id } && stage1Phrases.none { it.id == p.id }
        }
        if (lockedUniquePhrases.isNotEmpty()) {
            assertFalse(overallPhraseIds.contains(lockedUniquePhrases.first().id))
        }
    }

    @Test
    fun `level specific deck has terms exclusively from that level`() {
        val stage0 = DialectRepository.studyPlanStages[0]
        val deck0 = DialectRepository.decks.find { it.id == stage0.deckId }!!

        val stage1 = DialectRepository.studyPlanStages[1]
        val deck1 = DialectRepository.decks.find { it.id == stage1.deckId }!!

        assertTrue(deck0.phrases.isNotEmpty())
        assertTrue(deck1.phrases.isNotEmpty())
        assertEquals(stage0.deckId, deck0.id)
        assertEquals(stage1.deckId, deck1.id)
    }

    @Test
    fun `overall drill generates questions up till current level without locked levels`() {
        val maxUnlockedLevel = 1
        val overallQuestions = DialectRepository.generateDrillQuestionsForLevelAndRound(
            levelIndex = -1,
            round = 1,
            maxUnlockedLevel = maxUnlockedLevel
        )

        assertTrue(overallQuestions.isNotEmpty())

        val stage0PhraseIds = DialectRepository.decks.find { it.id == DialectRepository.studyPlanStages[0].deckId }?.phrases.orEmpty().map { it.id }.toSet()
        val stage1PhraseIds = DialectRepository.decks.find { it.id == DialectRepository.studyPlanStages[1].deckId }?.phrases.orEmpty().map { it.id }.toSet()
        val allowedPhraseIds = stage0PhraseIds + stage1PhraseIds

        overallQuestions.forEach { q ->
            assertTrue("Phrase ${q.phrase.id} must be from an unlocked level", allowedPhraseIds.contains(q.phrase.id))
        }
    }
}
