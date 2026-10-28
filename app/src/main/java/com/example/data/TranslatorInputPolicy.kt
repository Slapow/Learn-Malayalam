package com.example.data

import java.util.Locale

/** Whitespace-only edits must not repeat a request for the same phrase. */
private val translationWhitespace = Regex("\\s+")
internal fun translationQueryKey(input: String): String = input.trim().replace(translationWhitespace, " ")

/** Every edit gets the same pause; Done and Retry explicitly bypass this delay. */
internal fun translationDelayMillis(@Suppress("UNUSED_PARAMETER") input: String): Long = 450L

internal fun isTranslatorKeyConfigured(key: String): Boolean {
    val value = key.trim()
    return value.isNotEmpty() && value.lowercase(Locale.ROOT) !in setOf(
        "my_gemini_api_key", "your_gemini_api_key", "null", "undefined"
    )
}
