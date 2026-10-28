package com.example

import com.example.model.PhraseItem
import com.example.model.QuizQuestion
import com.example.model.QuizType
import com.example.viewmodel.QuizState
import com.example.viewmodel.enqueueRecycledQuestion
import com.example.viewmodel.undoCurrentRecycle
import org.junit.Assert.*
import org.junit.Test

class QuizRecyclingTest {
    private fun question(id: String, phraseId: String = "word", type: QuizType = QuizType.MANGLISH_TYPING) =
        QuizQuestion(
            id = id,
            type = type,
            phrase = PhraseItem(phraseId, "chaaya", "ചായ", "Tea"),
            prompt = "Tea"
        )

    @Test
    fun `each wrong answer increments the next appearance in every drill and practice format`() {
        QuizType.entries.forEach { type ->
            val original = question("original", type = type)
            var state = QuizState(questions = listOf(original))
            for (count in 1..5) {
                val current = state.questions[state.currentIndex]
                // Ordinary retries are regenerated with unrelated IDs and default metadata.
                val retry = question("fresh_$count", type = type)
                state = state.enqueueRecycledQuestion(retry)
                assertEquals(count, state.recycleCounts[original.phrase.id])
                assertEquals(count, state.questions.last().recycleCount)
                assertEquals(count - 1, state.questions[state.currentIndex].recycleCount)
                assertEquals(current, state.questions[state.currentIndex])
                assertEquals(type, state.questions.last().type)
                state = state.copy(currentIndex = state.questions.lastIndex)
            }
            assertEquals(0, original.recycleCount)
            assertEquals(5, state.reshuffledCount)
        }
    }

    @Test
    fun `recycling preserves interleaving and tracks different words independently`() {
        val original = question("a", "a")
        val others = (1..5).map { question("b$it", "b$it") }
        val input = QuizState(questions = listOf(original) + others, fixedRoundTargetCount = 6)
        val updated = input.enqueueRecycledQuestion(question("retry_a", "a"))
        assertEquals("retry_a", updated.questions[4].id)
        assertEquals(1, updated.questions[4].recycleCount)
        assertEquals(input.questions, updated.questions.filterNot { it.id == "retry_a" })
        assertEquals(6, updated.fixedRoundTargetCount)
        val second = updated.copy(currentIndex = 1).enqueueRecycledQuestion(question("retry_b", "b1"))
        assertEquals(mapOf("a" to 1, "b1" to 1), second.recycleCounts)
        assertEquals(emptyMap<String, Int>(), input.recycleCounts)
    }

    @Test
    fun `spaced review copies increment rather than inheriting an old count`() {
        val current = question("srs").copy(isSpacedReview = true, recycleCount = 2)
        val state = QuizState(questions = listOf(current), recycleCounts = mapOf("word" to 2))
        val updated = state.enqueueRecycledQuestion(current.copy(id = "srs_retry"))
        assertEquals(3, updated.questions.last().recycleCount)
        assertTrue(updated.questions.last().isSpacedReview)
    }

    @Test
    fun `override removes only the latest retry and rolls back its increment`() {
        val current = question("current").copy(recycleCount = 1)
        val other = question("other", "other")
        val futureOriginal = question("future_original")
        val state = QuizState(
            questions = listOf(current, other, futureOriginal),
            recycleCounts = mapOf("word" to 1),
            reshuffledCount = 1
        )
        val recycled = state.enqueueRecycledQuestion(question("retry"))
        val corrected = recycled.undoCurrentRecycle()
        assertEquals(state, corrected)
        assertTrue(corrected.questions.any { it.id == futureOriginal.id })
        assertEquals(1, corrected.questions.first().recycleCount)
    }

    @Test
    fun `overriding the first failure clears its count but leaves other words alone`() {
        val state = QuizState(
            questions = listOf(question("original")),
            recycleCounts = mapOf("other" to 3),
            reshuffledCount = 3
        )
        assertEquals(state, state.enqueueRecycledQuestion(question("retry")).undoCurrentRecycle())
        assertEquals(state, state.undoCurrentRecycle())
    }
}
