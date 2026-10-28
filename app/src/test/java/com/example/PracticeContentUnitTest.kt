package com.example

import com.example.data.DialectRepository
import com.example.model.QuizType
import com.example.model.SessionOrder
import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class PracticeContentUnitTest {
    @Test
    fun `every drill round uses the exact entries and choices from its selected deck`() {
        DialectRepository.decks.forEach { deck ->
            val allowedAnswers = deck.phrases.map { it.manglish }.toSet()
            assertEquals(deck, DialectRepository.getPracticeDeck(deck.levelIndex))
            for (round in 1..3) {
                val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(deck.levelIndex, round)
                assertEquals(deck.phrases.groupingBy { it }.eachCount(), questions.groupingBy { it.phrase }.eachCount())
                assertTrue(questions.all { !it.isSpacedReview })
                questions.forEach { q ->
                    assertTrue(q.options.all { it in allowedAnswers })
                    assertTrue(q.phrase.manglish in q.acceptedManglishVariants)
                }
            }
        }
    }

    @Test
    fun `typing and speaking use the same deck content throughout all rounds`() {
        DialectRepository.decks.forEach { deck ->
            for (type in listOf(QuizType.MANGLISH_TYPING, QuizType.SPEAKING_PRACTICE)) {
                for (round in 1..3) {
                    val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(
                        deck.levelIndex, round, questionType = type
                    )
                    assertEquals(deck.phrases.groupingBy { it }.eachCount(), questions.groupingBy { it.phrase }.eachCount())
                    assertTrue(questions.all { it.type == type && it.prompt == it.phrase.english })
                }
            }
        }
    }

    @Test
    fun `overall practice matches the overall deck and excludes locked content`() {
        for (unlocked in listOf(0, 7, 18, 39)) {
            val deck = DialectRepository.buildOverallDeckForLevel(unlocked)
            assertEquals(deck, DialectRepository.getPracticeDeck(-1, unlocked))
            val allowedAnswers = deck.phrases.map { it.manglish }.toSet()
            for (round in 1..3) {
                val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(-1, round, unlocked)
                assertEquals(deck.phrases.groupingBy { it }.eachCount(), questions.groupingBy { it.phrase }.eachCount())
                assertTrue(questions.all { it.phrase.levelNumber <= unlocked })
                assertTrue(questions.flatMap { it.options }.all { it in allowedAnswers })
            }
        }
    }

    @Test
    fun `retrying earlier touches preserves the chosen typing or speaking mode`() {
        for (deck in DialectRepository.decks) {
            for (type in listOf(QuizType.MANGLISH_TYPING, QuizType.SPEAKING_PRACTICE)) {
                for (touch in 1..3) {
                    val phrase = deck.phrases.first()
                    val retry = DialectRepository.generateQuestionForPhraseAndTouch(
                        phrase = phrase, touch = touch, levelIndex = deck.levelIndex, questionType = type
                    )
                    assertEquals(phrase, retry.phrase)
                    assertEquals(type, retry.type)
                    assertEquals(phrase.english, retry.prompt)
                }
            }
        }
    }

    @Test
    fun `question formats follow the shuffled position while the curriculum stays unchanged`() {
        val deck = DialectRepository.decks.first()
        val original = deck.phrases.toList()
        for (round in 1..3) {
            val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(
                deck.levelIndex, round, orderPhrases = { it.reversed() }
            )
            assertEquals(original.reversed(), questions.map { it.phrase })
            questions.forEachIndexed { index, question ->
                val expected = when (round) {
                    1 -> if (index % 2 == 0) QuizType.DIALECT_CHOICE else QuizType.WORD_TILES
                    2 -> QuizType.WORD_TILES
                    else -> if (index % 2 == 0) QuizType.MANGLISH_TYPING else QuizType.SPEAKING_PRACTICE
                }
                assertEquals(expected, question.type)
                assertEquals(round, question.roundNumber)
            }
        }
        assertEquals(original, deck.phrases)
    }

    @Test
    fun `drill rounds and dedicated modes keep every phrase and avoid consecutive starting words`() {
        val deck = DialectRepository.decks.first()
        val order = SessionOrder(Random(42))
        for (type in listOf(null, QuizType.MANGLISH_TYPING, QuizType.SPEAKING_PRACTICE)) {
            var previousFirst: String? = null
            repeat(9) { session ->
                val questions = DialectRepository.generateDrillQuestionsForLevelAndRound(
                    deck.levelIndex, session % 3 + 1, questionType = type,
                    orderPhrases = { phrases -> order.shuffled("drill:${type?.name}", phrases) { it.id } }
                )
                val first = questions.first().phrase.id
                assertNotEquals(previousFirst, first)
                assertEquals(deck.phrases.groupingBy { it }.eachCount(), questions.groupingBy { it.phrase }.eachCount())
                if (type != null) assertTrue(questions.all { it.type == type })
                previousFirst = first
            }
        }
    }
}
