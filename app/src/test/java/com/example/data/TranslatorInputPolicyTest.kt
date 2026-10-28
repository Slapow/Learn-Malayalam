package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Test

class TranslatorInputPolicyTest {
    @Test
    fun `single words and unfinished phrases translate after a pause`() {
        listOf("hello", "ചായ", "I want tea").forEach {
            assertEquals(450L, translationDelayMillis(it))
        }
    }

    @Test
    fun `spaces and punctuation use the same debounce as letters`() {
        listOf("hello ", "I want tea.", "hello!", "ചായ?", "hello\n").forEach {
            assertEquals(450L, translationDelayMillis(it))
        }
    }

    @Test
    fun `whitespace edits share a query key without dropping sentence content`() {
        assertEquals("I want tea", translationQueryKey("  I  want\n tea "))
        assertEquals("", translationQueryKey(" \n "))
        assertEquals("I don't want tea", translationQueryKey("I don't want tea"))
    }

    @Test
    fun `missing and placeholder keys disable AI but real configuration is accepted`() {
        listOf("", " ", "MY_GEMINI_API_KEY", "YOUR_GEMINI_API_KEY", "null", "undefined").forEach {
            org.junit.Assert.assertFalse(isTranslatorKeyConfigured(it))
        }
        org.junit.Assert.assertTrue(isTranslatorKeyConfigured("test-key-for-validation"))
    }
}
