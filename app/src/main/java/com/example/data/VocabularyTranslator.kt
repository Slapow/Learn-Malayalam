package com.example.data

import com.example.model.PhraseItem
import java.util.Locale

data class TranslationResult(
    val english: String,
    val manglish: String,
    val malayalam: String,
    val note: String = "",
    val isDatabaseMatch: Boolean = false,
    val usageLabel: String = ""
)

/** Searches the complete bundled curriculum, independently of unlocks or mastery. */
class VocabularyTranslator(private val phrases: List<PhraseItem>) {
    private data class SearchForm(val normalized: String, val words: Set<String>)
    private data class IndexedPhrase(val phrase: PhraseItem, val forms: List<SearchForm>)

    // Built once, on the worker performing the first lookup, rather than on each keystroke.
    private val index by lazy {
        phrases.map { phrase ->
            val forms = listOf(phrase.english, phrase.manglish, phrase.malayalam) + phrase.acceptedVariants
            IndexedPhrase(phrase, forms.map(::normalize).distinct().map { text ->
                SearchForm(text, text.split(' ').filter(String::isNotBlank).toSet())
            })
        }
    }
    private val exactIndex by lazy {
        buildMap<String, MutableList<PhraseItem>> {
            index.forEach { entry ->
                val keys = entry.forms.map { it.normalized } + normalize(entry.phrase.english.substringBefore(" ("))
                keys.distinct().filter(String::isNotBlank).forEach { key ->
                    getOrPut(key) { mutableListOf() }.add(entry.phrase)
                }
            }
        }
    }

    fun exactMatches(input: String): List<PhraseItem> {
        val query = normalize(translationQuery(input))
        if (query.isBlank()) return emptyList()
        return exactIndex[query].orEmpty().distinctBy { Triple(it.english, it.manglish, it.malayalam) }
    }

    fun databaseTranslations(input: String): List<TranslationResult> = exactMatches(input).map { phrase ->
        TranslationResult(
            english = phrase.english,
            manglish = phrase.manglish,
            malayalam = phrase.malayalam,
            note = phrase.regionalNuance,
            isDatabaseMatch = true,
            usageLabel = phrase.english.substringAfter(" (", "").substringBeforeLast(")")
        )
    }

    /** Retrieve from all levels locally; never attach learner history or account data. */
    fun relevantPhrases(input: String, limit: Int = 40): List<PhraseItem> {
        val query = normalize(translationQuery(input))
        val tokens = query.split(' ').filter(String::isNotBlank).toSet()
        if (tokens.isEmpty()) return emptyList()
        return index.map { entry ->
            val score = entry.forms.maxOf { form ->
                val overlap = form.words.count { it in tokens }
                when {
                    form.normalized == query -> 1000
                    form.words.isNotEmpty() && tokens.containsAll(form.words) -> 100 + form.words.size * 10
                    overlap > 0 -> overlap * 10
                    else -> 0
                }
            }
            entry.phrase to score
        }.filter { it.second > 0 }
            .sortedByDescending { it.second }
            .distinctBy { Triple(it.first.english, it.first.manglish, it.first.malayalam) }
            .take(limit)
            .map { it.first }
    }

    private fun translationQuery(input: String): String {
        val query = input.trim()
        meaningQuestion.matchEntire(query)?.let { return it.groupValues[1] }
        return query
            .replace(questionPrefix, "")
            .replace(languageSuffix, "")
    }

    private fun normalize(value: String): String = value
        .lowercase(Locale.ROOT)
        .replace(apostrophes, "")
        .replace(nonLetters, " ")
        .replace(whitespace, " ")
        .trim()

    private companion object {
        val meaningQuestion = Regex("""(?is)^what\s+does\s+(.+?)\s+mean\s*[?.!]*$""")
        val questionPrefix = Regex("""(?i)^(?:how\s+(?:do\s+I|to)\s+say|what(?:'s|\s+is)\s+the\s+word\s+for|translate)\s+""")
        val languageSuffix = Regex("""(?i)\s+(?:in|into|to)\s+(?:english|malayalam|manglish)\s*[?.!]*$""")
        val apostrophes = Regex("['’]")
        val nonLetters = Regex("""[^\p{L}\p{M}\p{N}\s]""")
        val whitespace = Regex("""\s+""")
    }
}

/** Vocabulary wins when AI repeats the same spoken form and meaning. */
internal fun mergeTranslationResults(
    vocabulary: List<TranslationResult>,
    alternatives: List<TranslationResult>
): List<TranslationResult> {
    fun normalized(text: String) = text.lowercase(Locale.ROOT)
        .replace(Regex("\\([^)]*\\)"), "")
        .replace(Regex("[^\\p{L}\\p{M}\\p{N}]"), "")
    return (vocabulary + alternatives).distinctBy {
        normalized(it.english) to normalized(it.malayalam)
    }
}
