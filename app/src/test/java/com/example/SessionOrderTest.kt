package com.example

import com.example.model.SessionOrder
import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class SessionOrderTest {
    @Test
    fun `new sessions preserve all entries without changing the source list`() {
        val input = (0..9).map { it.toString() }
        val original = input.toList()
        val order = SessionOrder(Random(123))
        val sessions = (0 until 20).map { order.shuffled("deck", input) { it } }
        sessions.forEach { assertEquals(original.sorted(), it.sorted()) }
        assertEquals(original, input)
        assertTrue(sessions.distinct().size > 1)
        sessions.zipWithNext().forEach { (previous, next) ->
            assertNotEquals(previous.first(), next.first())
        }
    }

    @Test
    fun `two phrase sessions always alternate starting words`() {
        val order = SessionOrder(Random(21))
        var previous: String? = null
        repeat(30) {
            val next = order.shuffled("deck", listOf("a", "b")) { it }
            assertEquals(listOf("a", "b"), next.sorted())
            assertNotEquals(previous, next.first())
            previous = next.first()
        }
    }

    @Test
    fun `empty and single phrase decks are safe`() {
        val order = SessionOrder(Random(12))
        assertTrue(order.shuffled("empty", emptyList<String>()) { it }.isEmpty())
        repeat(3) {
            assertEquals(listOf("a"), order.shuffled("single", listOf("a")) { it })
            assertEquals(listOf("a", "a"), order.shuffled("duplicates", listOf("a", "a")) { it })
        }
    }

    @Test
    fun `history is isolated by deck and mode`() {
        // Deterministic initial shuffle always places b first.
        val order = SessionOrder(object : Random() {
            override fun nextBits(bitCount: Int): Int = 0
        })
        val input = listOf("a", "b")
        val first = order.shuffled("deck1:cards", input) { it }.first()
        assertEquals(first, order.shuffled("deck1:write", input) { it }.first())
        assertEquals(first, order.shuffled("deck2:cards", input) { it }.first())
        assertNotEquals(first, order.shuffled("deck1:cards", input) { it }.first())
    }

    @Test
    fun `match tiles retain both sides and compare phrase identity rather than tile identity`() {
        data class Tile(val id: String, val phraseId: String)
        val input = listOf(Tile("en_a", "a"), Tile("ml_a", "a"), Tile("en_b", "b"), Tile("ml_b", "b"))
        val order = SessionOrder(Random(7))
        var previous: String? = null
        repeat(30) {
            val tiles = order.shuffled("match", input) { it.phraseId }
            assertEquals(input.toSet(), tiles.toSet())
            assertEquals(input.size, tiles.size)
            assertNotEquals(previous, tiles.first().phraseId)
            previous = tiles.first().phraseId
        }
    }

    @Test
    fun `changing deck content and empty sessions retain safe first word handling`() {
        val order = SessionOrder(Random(8))
        val first = order.shuffled("deck", listOf("a", "b")) { it }.first()
        assertTrue(order.shuffled("deck", emptyList<String>()) { it }.isEmpty())
        assertNotEquals(first, order.shuffled("deck", listOf("a", "b")) { it }.first())
        assertEquals(listOf("c"), order.shuffled("deck", listOf("c")) { it })
        assertEquals(listOf("d"), order.shuffled("deck", listOf("d")) { it })
    }
}
