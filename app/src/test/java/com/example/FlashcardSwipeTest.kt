package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.example.data.DialectRepository
import com.example.ui.screens.FlashcardsScreen
import com.example.viewmodel.FlashcardState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.ConscryptMode
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.SQLiteMode

@RunWith(RobolectricTestRunner::class)
// Native Android libraries are unavailable on Windows ARM; gestures need no native rendering.
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.LEGACY)
@SQLiteMode(SQLiteMode.Mode.LEGACY)
@ConscryptMode(ConscryptMode.Mode.OFF)
class FlashcardSwipeTest {
    @get:Rule
    val compose = createComposeRule()

    private var known = 0
    private var learning = 0
    private var flips = 0

    private fun showCards(count: Int = 3, advanceOnAnswer: Boolean = true) {
        val deck = DialectRepository.decks.first().let { it.copy(phrases = it.phrases.take(count)) }
        var state by mutableStateOf(FlashcardState(currentDeck = deck))
        fun advance() {
            if (!advanceOnAnswer) return
            val nextIndex = (state.currentCardIndex + 1) % count
            val nextId = deck.phrases[nextIndex].id
            state = state.copy(
                currentCardIndex = nextIndex,
                isFlipped = false,
                cardPresentationCount = state.cardPresentationCount + (nextId to ((state.cardPresentationCount[nextId] ?: 0) + 1))
            )
        }
        compose.setContent {
            MaterialTheme {
                FlashcardsScreen(
                    state = state,
                    onBack = {},
                    onSelectDeck = {},
                    onFlipCard = { flips++; state = state.copy(isFlipped = !state.isFlipped) },
                    onMarkKnown = { known++; advance() },
                    onMarkLearning = { learning++; advance() },
                    onSpeakPhrase = {}
                )
            }
        }
    }

    @Test
    fun `many drag updates submit once and the next card accepts another swipe`() {
        showCards()
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput {
            val start = Offset(width * 0.1f, height * 0.5f)
            down(start)
            repeat(20) { step ->
                moveTo(start + Offset(width * 0.8f * (step + 1) / 20, 0f), delayMillis = 4L)
            }
            up()
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, known) }
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, known)
            assertEquals(1, learning)
        }
    }

    @Test
    fun `short and cancelled gestures do not submit or block the next swipe`() {
        showCards()
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput {
            swipe(center, center + Offset(10f, 0f), durationMillis = 400L)
        }
        compose.waitForIdle()
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput {
            down(center)
            moveTo(center + Offset(15f, 0f))
            cancel()
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, known + learning) }
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, known) }
    }

    @Test
    fun `last remaining card can be marked learning repeatedly without locking`() {
        showCards(count = 1)
        repeat(2) {
            compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeLeft() }
            compose.waitForIdle()
        }
        compose.runOnIdle { assertEquals(2, learning) }
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, known) }
    }

    @Test
    fun `flipping a card does not invert the swipe direction`() {
        showCards()
        compose.onNodeWithTag("flashcards_tap_to_reveal_button").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, known)
            assertEquals(0, learning)
        }
        compose.onNodeWithTag("flashcards_tap_to_reveal_button").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, known)
            assertEquals(1, learning)
        }
    }

    @Test
    fun `dragging keeps the gesture surface fixed while the card moves`() {
        showCards()
        val area = compose.onNodeWithTag("flashcard_swipe_area")
        val originalBounds = area.fetchSemanticsNode().boundsInRoot
        area.performTouchInput {
            down(center)
            moveTo(center + Offset(width * 0.25f, 0f), delayMillis = 100L)
        }
        compose.waitForIdle()
        assertEquals(originalBounds, area.fetchSemanticsNode().boundsInRoot)
        area.performTouchInput { cancel() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, known + learning) }
    }

    @Test
    fun `swipe always unlocks even when the answer callback leaves the card unchanged`() {
        showCards(count = 1, advanceOnAnswer = false)
        repeat(2) {
            compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeRight() }
            compose.waitForIdle()
        }
        compose.runOnIdle { assertEquals(2, known) }
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, learning) }
    }

    @Test
    fun `cancelling a long drag returns the card without marking it`() {
        showCards()
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput {
            down(center)
            moveTo(center + Offset(width * 0.4f, 0f), delayMillis = 200L)
            cancel()
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, known + learning) }
        compose.onNodeWithTag("flashcard_swipe_area").performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, known) }
    }

    @Test
    fun `the second card follows the finger after the first swipe`() {
        showCards()
        val area = compose.onNodeWithTag("flashcard_swipe_area")
        area.performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, known) }
        val card = compose.onNodeWithTag("flashcard_flip_card", useUnmergedTree = true)
        val original = card.fetchSemanticsNode().boundsInRoot
        area.performTouchInput {
            down(center)
            moveTo(center - Offset(width * 0.3f, 0f), delayMillis = 200L)
        }
        compose.waitForIdle()
        assertNotEquals(original, card.fetchSemanticsNode().boundsInRoot)
        area.performTouchInput { cancel() }
    }

    @Test
    fun `tapping and swiping cards work together across card changes`() {
        showCards()
        val area = compose.onNodeWithTag("flashcard_swipe_area")
        area.performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, flips) }
        area.performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, learning) }
        area.performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(2, flips) }
        area.performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, known) }
    }
}
