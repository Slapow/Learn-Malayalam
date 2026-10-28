package com.example.model

import kotlin.random.Random

/** Keeps new sessions random without repeating their first phrase when another is available. */
internal class SessionOrder(private val random: Random = Random.Default) {
    private val previousFirstIds = mutableMapOf<String, String>()

    fun <T> shuffled(key: String, items: List<T>, phraseId: (T) -> String): List<T> {
        val shuffled = items.shuffled(random).toMutableList()
        if (shuffled.isEmpty()) return shuffled
        val previousFirst = previousFirstIds[key]
        if (phraseId(shuffled.first()) == previousFirst) {
            val alternatives = shuffled.indices.filter { phraseId(shuffled[it]) != previousFirst }
            if (alternatives.isNotEmpty()) {
                val replacement = alternatives.random(random)
                val first = shuffled[0]
                shuffled[0] = shuffled[replacement]
                shuffled[replacement] = first
            }
        }
        previousFirstIds[key] = phraseId(shuffled.first())
        return shuffled
    }
}
