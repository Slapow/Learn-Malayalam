package com.example.data

import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], manifest = Config.NONE)
class TranslatorResponseTest {
    private fun response(count: Int = 2): String = JSONObject().apply {
        put("translations", JSONArray().apply {
            repeat(count) { index ->
                put(JSONObject().put("english", "Meaning $index").put("manglish", "Form $index")
                    .put("malayalam", "രൂപം").put("usageLabel", "Usage $index").put("note", ""))
            }
        })
        put("clarification", "Please specify the intended meaning.")
    }.toString()

    @Test
    fun `parses independent labelled cards and caps AI alternatives`() {
        val cards = parseTranslationAlternatives(response(6), false)
        assertEquals(4, cards.size)
        assertEquals("Meaning 0", cards[0].english)
        assertEquals("Usage 1", cards[1].usageLabel)
        assertTrue(cards.none { it.isDatabaseMatch })
    }

    @Test
    fun `no alternatives is valid when vocabulary already answers the query`() {
        assertTrue(parseTranslationAlternatives(response(0), true).isEmpty())
    }

    @Test
    fun `uncertain translation asks for clarification when no vocabulary matches`() {
        val error = assertThrows(IllegalStateException::class.java) {
            parseTranslationAlternatives(response(0), false)
        }
        assertEquals("Please specify the intended meaning.", error.message)
    }

    @Test
    fun `malformed response is rejected`() {
        assertThrows(JSONException::class.java) { parseTranslationAlternatives("{}", false) }
    }

    @Test
    fun `missing language fields are rejected rather than shown as empty cards`() {
        val json = JSONObject(response(1))
        json.getJSONArray("translations").getJSONObject(0).put("malayalam", " ")
        assertThrows(IllegalStateException::class.java) {
            parseTranslationAlternatives(json.toString(), false)
        }
    }
}
