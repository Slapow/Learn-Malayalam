package com.example.data

import com.example.model.PhraseItem

private val spokenVerbForms = mapOf(
    "parayuka" to "para",
    "varuka" to "vaa",
    "kazhikkuka" to "kazhikku",
    "povuka" to "po",
    "pokuka" to "po",
    "kudikkuka" to "kudi",
    "irikkuka" to "iri",
    "kelkkuka" to "kelkku",
    "edukkuka" to "edu",
    "cheyyuka" to "cheyyu",
    "nokkuka" to "nokku",
    "nadakkuka" to "nadakku",
    "uranguka" to "urangu",
    "nilkkuka" to "nilkku",
    "vaanguka" to "vaangu"
)

private val spokenMalayalamVerbForms = mapOf(
    "പറയുക" to "പറ",
    "വരുക" to "വാ",
    "കഴിക്കുക" to "കഴിക്കൂ",
    "പോവുക" to "പോ",
    "പോകുക" to "പോ",
    "കുടിക്കുക" to "കുടി",
    "ഇരിക്കുക" to "ഇരി",
    "കേൾക്കുക" to "കേൾക്ക്",
    "എടുക്കുക" to "എടു",
    "ചെയ്യുക" to "ചെയ്യൂ",
    "നോക്കുക" to "നോക്ക്",
    "നടക്കുക" to "നടക്ക്",
    "ഉറങ്ങുക" to "ഉറങ്ങ്",
    "നിൽക്കുക" to "നിൽക്ക്",
    "വാങ്ങുക" to "വാങ്ങ്"
)

private val preferredManglishSpellings = listOf(
    Regex("""\bchaya(?=[a-z]*\b)""", RegexOption.IGNORE_CASE) to "chaaya",
    Regex("""\bnjan\b""", RegexOption.IGNORE_CASE) to "njaan",
    Regex("""\bkaapi(?=[a-z]*\b)""", RegexOption.IGNORE_CASE) to "kaappi"
)

private fun normalizeManglishSpelling(value: String): String =
    preferredManglishSpellings.fold(value) { text, (pattern, preferred) ->
        pattern.replace(text) { match ->
            if (match.value.first().isUpperCase()) {
                preferred.replaceFirstChar(Char::uppercase)
            } else {
                preferred
            }
        }
    }

private fun singleSpokenForm(value: String): String =
    Regex("""(?i)\s+vs\.?\s+""").split(value, limit = 2).first()
        .substringBefore('/')
        .trim()

internal fun cleanSpokenSeedPhrase(phrase: PhraseItem): PhraseItem {
    val manglish = singleSpokenForm(phrase.manglish)
        .let { spokenVerbForms[it.lowercase()] ?: it }
        .let(::normalizeManglishSpelling)
    val malayalam = singleSpokenForm(phrase.malayalam).let { spokenMalayalamVerbForms[it] ?: it }
    // Meanings are curated in the vocabulary sources. Keep complete translations
    // and grammatical distinctions instead of guessing from punctuation or length.
    val english = phrase.english.trim()
    val curatedExample = NoviceSpokenExamples.forPhraseId(phrase.id)
    val example = curatedExample?.copy(manglish = normalizeManglishSpelling(curatedExample.manglish))
        ?: SpokenExample(
            malayalam = "അമ്മേ, “$malayalam” എന്ന് പറയൂ.",
            manglish = "Amme, “$manglish” ennu parayoo.",
            english = "Mom, say “$english”."
        )

    return phrase.copy(
        manglish = manglish,
        malayalam = malayalam,
        english = english,
        regionalNuance = "",
        textbookAlternative = "",
        phoneticGuide = "",
        wordBreakdown = "",
        acceptedVariants = listOf(manglish.lowercase()),
        audioUtterance = malayalam,
        spokenExampleMalayalam = example.malayalam,
        spokenExampleManglish = example.manglish,
        spokenExampleEnglish = example.english
    )
}
