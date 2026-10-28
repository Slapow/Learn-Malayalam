package com.example.data

import com.example.model.PhraseItem
import org.junit.Assert.*
import org.junit.Test

class VocabularyTranslatorTest {
    private val vocabulary = VocabularyTranslator(listOf(
        PhraseItem("tea", "Chaaya", "ചായ", "Tea", acceptedVariants = listOf("chaya")),
        PhraseItem("coffee", "Kaappi", "കാപ്പി", "Coffee", levelNumber = 30),
        PhraseItem("brother", "Chettan", "ചേട്ടൻ", "Older brother", acceptedVariants = listOf("chetta")),
        PhraseItem("husband", "Achayan", "അച്ചായൻ", "Husband"),
        PhraseItem("want", "Venam", "വേണം", "Want"),
        PhraseItem("refuse", "Venda", "വേണ്ട", "Don't want"),
        PhraseItem("casual", "Nee", "നീ", "You (casual)"),
        PhraseItem("respectful", "Ningal", "നിങ്ങൾ", "You (respectful)")
    ))

    @Test
    fun `looks up English Manglish variants and Malayalam without destroying vowel marks`() {
        for (query in listOf("Tea!", "CHAAYA", "chaya", "ചായ")) {
            assertEquals("Tea", vocabulary.databaseTranslations(query).single().english)
        }
    }

    @Test
    fun `search includes vocabulary beyond the current learning level`() {
        assertEquals("Kaappi", vocabulary.databaseTranslations("coffee").single().manglish)
        assertEquals("coffee", vocabulary.relevantPhrases("I want coffee").first().id)
    }

    @Test
    fun `kinship matches keep the curated dialect meanings`() {
        assertEquals("Older brother", vocabulary.databaseTranslations("chetta").single().english)
        assertEquals("Husband", vocabulary.databaseTranslations("Achayan").single().english)
    }

    @Test
    fun `recognizes questions without removing text from inside words`() {
        assertEquals("Tea", vocabulary.databaseTranslations("What does chaaya mean?").single().english)
        assertEquals("Kaappi", vocabulary.databaseTranslations("How do I say coffee in Malayalam?").single().manglish)
        assertTrue(vocabulary.databaseTranslations("meaningful").isEmpty())
    }

    @Test
    fun `sentences are never replaced by a matching substring`() {
        assertTrue(vocabulary.databaseTranslations("I don't want tea").isEmpty())
        val references = vocabulary.relevantPhrases("I don't want tea").map { it.id }
        assertTrue("refuse" in references)
        assertTrue("tea" in references)
    }

    @Test
    fun `retains ambiguity between casual and respectful you`() {
        assertEquals(setOf("casual", "respectful"), vocabulary.exactMatches("you").map { it.id }.toSet())
        assertEquals("Nee", vocabulary.databaseTranslations("You (casual)").single().manglish)
        val cards = vocabulary.databaseTranslations("you")
        assertEquals(listOf("casual", "respectful"), cards.map { it.usageLabel })
        assertEquals(listOf("Nee", "Ningal"), cards.map { it.manglish })
    }

    @Test
    fun `unknown input has no fabricated local translation`() {
        assertTrue(vocabulary.databaseTranslations("an entirely unknown expression").isEmpty())
        assertTrue(vocabulary.relevantPhrases("zzzzzz").isEmpty())
        assertTrue(vocabulary.exactMatches("?! ").isEmpty())
    }

    @Test
    fun `AI duplicates never replace curated vocabulary but distinct forms remain`() {
        val local = vocabulary.databaseTranslations("you")
        val duplicate = local.first().copy(english = "You", isDatabaseMatch = false, usageLabel = "Informal")
        val alternative = TranslationResult("You (very respectful)", "Thaankal", "താങ്കൾ", usageLabel = "Formal")
        val merged = mergeTranslationResults(local, listOf(duplicate, alternative, alternative))
        assertEquals(local + alternative, merged)
        assertTrue(merged.first().isDatabaseMatch)
    }

    @Test
    fun `different meanings with the same spoken form are retained`() {
        val first = TranslationResult("First meaning", "form", "രൂപം")
        val second = first.copy(english = "Second meaning")
        assertEquals(listOf(first, second), mergeTranslationResults(listOf(first), listOf(second)))
    }
}
