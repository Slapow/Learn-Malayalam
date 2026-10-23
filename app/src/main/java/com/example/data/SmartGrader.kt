package com.example.data

import com.example.model.PhraseItem
import kotlin.math.min

data class GradeResult(
    val isCorrect: Boolean,
    val isAlmostExactTypo: Boolean = false,
    val scorePercent: Int = 0,
    val feedbackHeadline: String,
    val detailedExplanation: String
)

/**
 * Duolingo + Quizlet Smart Grading engine for Manglish typing & speech recognition.
 * - Normalizes Manglish Roman transliteration variations (e.g. njaan/njan, varoolla/varoola).
 * - Calculates edit distance to forgive 1-character typos while alerting the learner ("Almost exact! Watch the spelling").
 * - Verifies "Copy Answer When Wrong" inputs so the learner practices the exact phrase before continuing.
 */
object SmartGrader {

    fun cleanText(raw: String): String {
        return raw.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9\\u0D00-\\u0D7F ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Phonetic normalization for Manglish so beginners aren't penalized for 'njaan' vs 'njan'
     * or 'varoolla' vs 'varoola'.
     */
    fun normalizeManglishPhonetics(input: String): String {
        return cleanText(input)
            .replace("njaan", "njan")
            .replace("aa", "a")
            .replace("ee", "i")
            .replace("oo", "u")
            .replace("ll", "l")
            .replace("tt", "t")
            .replace("pp", "p")
            .replace("mm", "m")
            .replace("cc", "c")
            .replace("yy", "y")
            .replace("kk", "k")
    }

    fun gradeWrittenResponse(userAnswer: String, phrase: PhraseItem): GradeResult {
        val userClean = cleanText(userAnswer)
        if (userClean.isBlank()) {
            return GradeResult(
                isCorrect = false,
                scorePercent = 0,
                feedbackHeadline = "Try typing the Manglish!",
                detailedExplanation = "Correct answer: \"${phrase.manglish}\" (${phrase.phoneticGuide})"
            )
        }

        val candidates = (listOf(phrase.manglish) + phrase.acceptedVariants).map { cleanText(it) }.filter { it.isNotBlank() }
        val targetClean = cleanText(phrase.manglish)

        // 1. Exact match against primary or accepted variants
        if (candidates.any { it == userClean }) {
            return GradeResult(
                isCorrect = true,
                isAlmostExactTypo = false,
                scorePercent = 100,
                feedbackHeadline = "Polichu! (Awesome!) Spot-on Manglish!",
                detailedExplanation = "\"${phrase.manglish}\" = \"${phrase.english}\""
            )
        }

        // 2. Phonetic match (e.g., 'njan' vs 'njaan', 'varoola' vs 'varoolla')
        val userPhonetic = normalizeManglishPhonetics(userClean)
        if (candidates.any { normalizeManglishPhonetics(it) == userPhonetic }) {
            return GradeResult(
                isCorrect = true,
                isAlmostExactTypo = true,
                scorePercent = 95,
                feedbackHeadline = "Correct! (Nice phonetic spelling!)",
                detailedExplanation = "Full cadence spelling: \"${phrase.manglish}\" (${phrase.phoneticGuide})"
            )
        }

        // 3. Duolingo/Quizlet 1-character typo tolerance for words >= 4 chars
        val bestDistance = candidates.minOfOrNull { levenshtein(userClean, it) } ?: 99
        val maxAllowedTypos = if (targetClean.length >= 12) 2 else if (targetClean.length >= 4) 1 else 0
        if (bestDistance <= maxAllowedTypos) {
            return GradeResult(
                isCorrect = true,
                isAlmostExactTypo = true,
                scorePercent = 90,
                feedbackHeadline = "Correct! (You had a tiny typo)",
                detailedExplanation = "You typed \"$userAnswer\" — exact spelling is \"${phrase.manglish}\"."
            )
        }

        // 4. Incorrect -> Trigger Quizlet "Copy Answer to Continue"
        return GradeResult(
            isCorrect = false,
            isAlmostExactTypo = false,
            scorePercent = 0,
            feedbackHeadline = "Not quite! Let's lock it into memory:",
            detailedExplanation = "Type \"${phrase.manglish}\" below to continue. It will shuffle back into the deck so you can master it!"
        )
    }

    fun gradeSpokenResponse(spokenTranscript: String, phrase: PhraseItem): GradeResult {
        val cleanSpoken = cleanText(spokenTranscript)
        if (cleanSpoken.isBlank()) {
            return GradeResult(
                isCorrect = false,
                scorePercent = 0,
                feedbackHeadline = "Didn't catch your voice yet!",
                detailedExplanation = "Tap the mic and say: \"${phrase.manglish}\" (${phrase.phoneticGuide})"
            )
        }

        // Check Malayalam script match if Android speech recognizer returned Malayalam script
        if (spokenTranscript.contains(phrase.malayalam.take(3)) || phrase.malayalam.contains(spokenTranscript.trim())) {
            return GradeResult(
                isCorrect = true,
                scorePercent = 100,
                feedbackHeadline = "Native Pronunciation! Recognized Malayalam script!",
                detailedExplanation = "Heard: \"$spokenTranscript\" • \"${phrase.manglish}\""
            )
        }

        val candidates = (listOf(phrase.manglish) + phrase.acceptedVariants).map { cleanText(it) }.filter { it.isNotBlank() }
        val spokenPhonetic = normalizeManglishPhonetics(cleanSpoken)

        // Check phonetic overlap or word overlap
        val targetWords = cleanText(phrase.manglish).split(" ").filter { it.isNotBlank() }
        val spokenWords = cleanSpoken.split(" ").filter { it.isNotBlank() }

        val anyCandidateMatches = candidates.any { cand ->
            val candPhonetic = normalizeManglishPhonetics(cand)
            candPhonetic == spokenPhonetic ||
                candPhonetic.contains(spokenPhonetic) && spokenPhonetic.length >= 3 ||
                spokenPhonetic.contains(candPhonetic) ||
                levenshtein(spokenPhonetic, candPhonetic) <= 2
        }

        val wordOverlapCount = targetWords.count { tw ->
            val twNorm = normalizeManglishPhonetics(tw)
            spokenWords.any { sw ->
                val swNorm = normalizeManglishPhonetics(sw)
                swNorm == twNorm || levenshtein(swNorm, twNorm) <= 1
            }
        }

        val hasEnoughWordOverlap = targetWords.isNotEmpty() &&
            (wordOverlapCount.toFloat() / targetWords.size.toFloat()) >= 0.5f

        if (anyCandidateMatches || hasEnoughWordOverlap) {
            return GradeResult(
                isCorrect = true,
                scorePercent = 96,
                feedbackHeadline = "Super Cadence! You sounded like an Adoor local!",
                detailedExplanation = "Heard: \"$spokenTranscript\" — Target: \"${phrase.manglish}\" (${phrase.phoneticGuide})"
            )
        }

        return GradeResult(
            isCorrect = false,
            scorePercent = 40,
            feedbackHeadline = "Heard \"$spokenTranscript\" — let's practice the exact sound!",
            detailedExplanation = "Say or type \"${phrase.manglish}\" (${phrase.phoneticGuide}) to continue. We'll shuffle this card back in!"
        )
    }

    /**
     * Checks if the user's "Copy Answer" input matches the target Manglish (with phonetic tolerance)
     */
    fun matchesCopyAnswer(copyInput: String, phrase: PhraseItem): Boolean {
        val cleanInput = cleanText(copyInput)
        if (cleanInput.isBlank()) return false
        val targetClean = cleanText(phrase.manglish)
        if (cleanInput == targetClean) return true
        val inputPhonetic = normalizeManglishPhonetics(cleanInput)
        val candidates = (listOf(phrase.manglish) + phrase.acceptedVariants).map { normalizeManglishPhonetics(it) }
        return candidates.any { it == inputPhonetic || levenshtein(inputPhonetic, it) <= 1 }
    }

    private fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = IntArray(lhsLength + 1) { it }
        var newCost = IntArray(lhsLength + 1) { 0 }

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLength]
    }
}
