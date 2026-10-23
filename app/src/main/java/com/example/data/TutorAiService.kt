package com.example.data

import com.example.BuildConfig
import com.example.model.ChatMessage
import com.example.model.RoleplayScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TutorAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendRoleplayMessage(
        scenario: RoleplayScenario,
        conversationHistory: List<ChatMessage>,
        userMessage: String
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val apiResponse = callGeminiApi(apiKey, conversationHistory, userMessage)
                if (apiResponse != null) {
                    return@withContext apiResponse
                }
            } catch (_: Exception) {
                // Fall back gracefully to built-in Q&A dictionary engine
            }
        }

        return@withContext generateDirectTutorAnswer(userMessage)
    }

    private fun callGeminiApi(
        apiKey: String,
        history: List<ChatMessage>,
        userMessage: String
    ): ChatMessage? {
        val systemPrompt = """
            You are a simple, clear, beginner-friendly Malayalam language tutor for someone starting at Ground Level 0 who only knows English.
            Do NOT use any roleplay persona or grandma character. Keep your tone simple, direct, and helpful.
            Regional focus: Spoken Central Travancore Malayalam (Adoor / Pathanamthitta).

            CRITICAL RESPONSE FORMAT RULES:
            1. In the "english" field, ALWAYS answer in clear English using the exact pattern:
               'The word/phrase for <English term> is "<Manglish word>" (<phonetic pronunciation>).'
            2. In the "manglish" field, put ONLY the target Manglish word or short phrase (and phonetic guide).
            3. In the "malayalam" field, put the Malayalam script for visual reference.
            4. In the "note" field, provide a concise usage note explaining:
               - Whether speakers in Kerala just use the English word in casual speech (e.g., "Hello", "Thank you", "Sorry", "Please", "Bus", "Time").
               - Whether a word is only used in formal/textbook situations vs. casual/informal home speech (e.g., formal "Namaskaram" vs casual "Hello" or "Sugamaano?", formal "Nanni" vs English "Thanks", formal "-unnilla" vs Adoor "-oolla").

            Respond ONLY in valid JSON with keys:
            {
              "english": "The word for ... is \"...\" ...",
              "manglish": "...",
              "malayalam": "...",
              "note": "..."
            }
        """.trimIndent()

        val contentsArray = JSONArray()
        for (turn in history.takeLast(4)) {
            val role = if (turn.isUser) "user" else "model"
            val textContent = if (turn.isUser) turn.manglishText else turn.englishText
            contentsArray.put(
                JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().put("text", textContent)))
                }
            )
        }

        contentsArray.put(
            JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            }
        )

        val rootRequest = JSONObject().apply {
            put("contents", contentsArray)
            put(
                "systemInstruction",
                JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                }
            )
            put(
                "generationConfig",
                JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                }
            )
        }

        val requestUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(requestUrl)
            .post(rootRequest.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseBody = response.body?.string() ?: return null

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            val rawText = parts.getJSONObject(0).optString("text")

            val parsedJson = JSONObject(rawText)
            val english = parsedJson.optString("english")
            val manglish = parsedJson.optString("manglish")
            val malayalam = parsedJson.optString("malayalam")
            val note = parsedJson.optString("note").takeIf { it.isNotBlank() && it != "null" }

            if (english.isBlank() && manglish.isBlank()) return null

            return ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                manglishText = manglish,
                malayalamText = malayalam,
                englishText = english,
                correctionFeedback = note
            )
        }
    }

    private data class DictionaryEntry(
        val triggers: List<String>,
        val englishLabel: String,
        val manglishWord: String,
        val phonetic: String,
        val malayalamScript: String,
        val usageNote: String
    )

    private val entries = listOf(
        DictionaryEntry(
            triggers = listOf("hello", "hi", "hey", "greet", "greeting"),
            englishLabel = "hello",
            manglishWord = "Namaskaram (formal) / Hello or Sugamaano? (casual)",
            phonetic = "nuh-mus-KAA-rum / soo-guh-MAH-noh",
            malayalamScript = "നമസ്കാരം / ഹലോ / സുഖമാണോ?",
            usageNote = "In casual everyday speech in Adoor & across Kerala, people simply say the English word \"Hello\" or \"Hi\", or greet you by asking \"Sugamaano?\" (Are you doing well?). \"Namaskaram\" is formal and mostly used with strangers, elders in formal settings, or on the news."
        ),
        DictionaryEntry(
            triggers = listOf("thank you", "thanks", "thank"),
            englishLabel = "thank you",
            manglishWord = "Thanks / Thank you (casual) or Nanni (formal)",
            phonetic = "NUN-nee",
            malayalamScript = "താങ്ക്സ് / നന്ദി",
            usageNote = "In everyday spoken Malayalam, people almost always use the English words \"Thanks\" or \"Thank you\" (or just a warm smile and nod). The Malayalam word \"Nanni\" (നന്ദി) sounds very formal—like a speech or news broadcast—so don't use \"Nanni\" casually at home."
        ),
        DictionaryEntry(
            triggers = listOf("sorry", "excuse me", "apologize"),
            englishLabel = "sorry",
            manglishWord = "Sorry (casual) / Kshamikkanam (formal)",
            phonetic = "kshuh-MIK-kuh-num",
            malayalamScript = "സോറി / ക്ഷമിക്കണം",
            usageNote = "Locals use the English word \"Sorry\" 99% of the time in daily conversation. \"Kshamikkanam\" is formal/polite and only used for serious apologies."
        ),
        DictionaryEntry(
            triggers = listOf("please"),
            englishLabel = "please",
            manglishWord = "Please (casual) / Dayavaayi (formal)",
            phonetic = "duh-yuh-VAA-yee",
            malayalamScript = "പ്ലീസ് / ദയവായി",
            usageNote = "In spoken Malayalam, people either use the English word \"Please\" or soften the verb ending (like adding \"-o\" or \"ketto\"). \"Dayavaayi\" is strictly formal/written (like on signs or announcements)."
        ),
        DictionaryEntry(
            triggers = listOf("how are you", "how do you do", "whats up", "what's up", "doing well"),
            englishLabel = "how are you",
            manglishWord = "Sugamaano?",
            phonetic = "soo-guh-MAH-noh?",
            malayalamScript = "സുഖമാണോ?",
            usageNote = "Literally means \"Is it well/good?\". You can also pair it with \"Entha vishesham?\" (What's the news / What's new?). To reply \"I'm good\", just say \"Sugam!\" (soo-gum)."
        ),
        DictionaryEntry(
            triggers = listOf("good morning", "morning"),
            englishLabel = "good morning",
            manglishWord = "Good morning (casual) / Suprabhaatham (formal)",
            phonetic = "soo-pruh-BHAA-thum",
            malayalamScript = "ഗുഡ് മോർണിംഗ് / സുപ്രഭാതം",
            usageNote = "Everyone uses the English phrase \"Good morning\" in daily life, or asks \"Eneetto?\" (Did you wake up?) / \"Chaya kudicho?\" (Did you drink tea?) at home. \"Suprabhaatham\" is formal/literary."
        ),
        DictionaryEntry(
            triggers = listOf("bye", "goodbye", "see you", "leaving"),
            englishLabel = "goodbye / see you",
            manglishWord = "Njaan poyi varatte / Pinne kaanaam",
            phonetic = "NYAAN poh-yee vuh-RUT-tey / PIN-ney KAA-naam",
            malayalamScript = "ഞാൻ പോയി വരട്ടെ / പിന്നെ കാണാം",
            usageNote = "In Malayalam culture, you never say a permanent \"Goodbye\". Instead you say \"Pinne kaanaam\" (See you later) or \"Njaan poyi varatte\" (Let me go and come back), or simply the English \"Bye!\"."
        ),
        DictionaryEntry(
            triggers = listOf("yes", "yeah", "yep"),
            englishLabel = "yes",
            manglishWord = "Athe",
            phonetic = "UH-they",
            malayalamScript = "അതെ",
            usageNote = "\"Athe\" is the standard everyday word for \"Yes\". In very casual speech you'll also hear a long \"Aa\" (ആ) with a head nod."
        ),
        DictionaryEntry(
            triggers = listOf("no", "nope"),
            englishLabel = "no",
            manglishWord = "Illa / Alla / Venda",
            phonetic = "IL-lah / UL-lah / VEN-dah",
            malayalamScript = "ഇല്ല / അല്ല / വേണ്ട",
            usageNote = "Malayalam has 3 words for 'No' depending on context:\n• \"Illa\" = No (don't have / didn't happen)\n• \"Alla\" = No (not that one)\n• \"Venda\" = No thanks / I don't want it."
        ),
        DictionaryEntry(
            triggers = listOf("dont know", "don't know", "do not know", "idk"),
            englishLabel = "I don't know",
            manglishWord = "Enikku ariyoolla",
            phonetic = "eh-NIK-koo uh-ree-YOO-lah",
            malayalamScript = "എനിക്ക് അറിയൂല്ല",
            usageNote = "In Adoor/Central Travancore, people say \"ariyoolla\" (or \"ariyathilla\") instead of textbook \"ariyilla\". You can drop \"Enikku\" (to me) and just say \"Ariyoolla!\"."
        ),
        DictionaryEntry(
            triggers = listOf("water", "thirsty"),
            englishLabel = "water",
            manglishWord = "Vellam",
            phonetic = "VEL-lum",
            malayalamScript = "വെള്ളം",
            usageNote = "To ask \"Do you want water?\", say \"Vellam veno?\". To say \"I want water\", say \"Vellam venam\"."
        ),
        DictionaryEntry(
            triggers = listOf("tea", "chai", "coffee"),
            englishLabel = "tea",
            manglishWord = "Chaya",
            phonetic = "CHAH-yah",
            malayalamScript = "ചായ",
            usageNote = "Black tea without milk is \"Kattan chaya\" (കട്ടൻ ചായ). Coffee is \"Kaappi\" (കാപ്പി). \"Chaya veno?\" means \"Do you want tea?\"."
        ),
        DictionaryEntry(
            triggers = listOf("food", "eat", "hungry", "ate"),
            englishLabel = "food / did you eat",
            manglishWord = "Food kazhicho? / Choru kazhicho?",
            phonetic = "FOOD kuh-ZHI-choh? / CHOH-roo kuh-ZHI-choh?",
            malayalamScript = "ഫുഡ് കഴിച്ചോ? / ചോറ് കഴിച്ചോ?",
            usageNote = "In casual Manglish, people commonly use the English word \"Food\" + \"kazhicho?\" (did you eat?), or \"Choru\" (rice/meal). Formal textbook \"Bhakshanam\" is less common in casual youth speech."
        ),
        DictionaryEntry(
            triggers = listOf("what", "what is"),
            englishLabel = "what",
            manglishWord = "Entha?",
            phonetic = "EN-thah?",
            malayalamScript = "എന്താ?",
            usageNote = "Spoken shortcut for formal \"Enthaanu?\". In Central Travancore (Adoor), among close friends you'll also hear the slang \"Enthuvaadey?\" (What is this, bro?)."
        ),
        DictionaryEntry(
            triggers = listOf("where", "where is"),
            englishLabel = "where",
            manglishWord = "Evide?",
            phonetic = "EH-vi-day?",
            malayalamScript = "എവിടെ?",
            usageNote = "Put the person or place first, then \"evide?\": e.g., \"Amma evide?\" (Where is Mom?) or \"Bathroom evide?\" (Where is the bathroom?)."
        ),
        DictionaryEntry(
            triggers = listOf("who", "who is"),
            englishLabel = "who",
            manglishWord = "Aara?",
            phonetic = "AA-rah?",
            malayalamScript = "ആരാ?",
            usageNote = "Spoken form of formal \"Aaraanu?\". For example, \"Athu aara?\" means \"Who is that?\"."
        ),
        DictionaryEntry(
            triggers = listOf("when", "what time"),
            englishLabel = "when",
            manglishWord = "Eppo?",
            phonetic = "EP-poh?",
            malayalamScript = "എപ്പൊ?",
            usageNote = "Spoken shortcut for formal \"Eppol?\". For example: \"Eppo varum?\" (When will you come?)."
        ),
        DictionaryEntry(
            triggers = listOf("why"),
            englishLabel = "why",
            manglishWord = "Enthina?",
            phonetic = "en-THI-nah?",
            malayalamScript = "എന്തിനാ?",
            usageNote = "Spoken form of \"Enthinaanu?\" (For what / why?)."
        ),
        DictionaryEntry(
            triggers = listOf("how much", "price", "cost"),
            englishLabel = "how much",
            manglishWord = "Ethra aayi? / Ethraya?",
            phonetic = "ETH-ruh AA-yee?",
            malayalamScript = "എത്രയായി?",
            usageNote = "Used at shops or with auto drivers: \"Ethra aayi?\" (How much did it come to?)."
        ),
        DictionaryEntry(
            triggers = listOf("i ", "me", "myself"),
            englishLabel = "I / me",
            manglishWord = "Njaan",
            phonetic = "NYAAN",
            malayalamScript = "ഞാൻ",
            usageNote = "Used in every register (both formal and informal). For \"To me / I have\", use \"Enikku\" (eh-NIK-koo)."
        ),
        DictionaryEntry(
            triggers = listOf("you"),
            englishLabel = "you",
            manglishWord = "Nee (casual) / Ningal (polite/plural)",
            phonetic = "NEE / NING-gul",
            malayalamScript = "നീ / നിങ്ങൾ",
            usageNote = "Use \"Nee\" only with close friends, siblings, or younger people. With elders or strangers, use \"Ningal\" or their family title (like \"Chetta\" for elder brother or \"Ammachi\" for grandma)."
        ),
        DictionaryEntry(
            triggers = listOf("mom", "mother", "dad", "father", "grandma", "grandpa", "family"),
            englishLabel = "family terms",
            manglishWord = "Appa (Dad), Amma (Mom), Ammachi (Grandma), Appachan (Grandpa)",
            phonetic = "UP-pah, UM-mah, UM-mah-chee, UP-puh-chun",
            malayalamScript = "അപ്പ, അമ്മ, അമ്മാച്ചി, അപ്പച്ചൻ",
            usageNote = "In Central Travancore (Adoor / Pathanamthitta), \"Appa\", \"Ammachi\", and \"Appachan\" are the natural everyday family titles instead of textbook \"Pithavu\" or \"Muthassi\"."
        ),
        DictionaryEntry(
            triggers = listOf("come", "coming", "wont come", "won't come"),
            englishLabel = "come / won't come",
            manglishWord = "Vaa (Come!) / Varaam (I'll come) / Varoolla (Won't come)",
            phonetic = "VAA / vuh-RAAM / vuh-ROO-lah",
            malayalamScript = "വാ / വരാം / വരൂല്ല",
            usageNote = "In Adoor spoken dialect, \"won't come\" is \"varoolla\" (or \"varathilla\") instead of textbook \"varunnilla\"."
        ),
        DictionaryEntry(
            triggers = listOf("go", "going", "wont go", "won't go"),
            englishLabel = "go / won't go",
            manglishWord = "Pokaam (Let's go) / Pokoolla (Won't go)",
            phonetic = "poh-KAAM / poh-KOO-lah",
            malayalamScript = "പോകാം / പോകൂല്ല",
            usageNote = "\"Pokoolla\" is the casual Central Travancore contraction for formal \"pokunnilla\"."
        ),
        DictionaryEntry(
            triggers = listOf("good", "nice", "awesome", "great"),
            englishLabel = "good / awesome",
            manglishWord = "Nallathu (Good) / Polichu! (Awesome!)",
            phonetic = "NUL-luh-thoo / poh-LI-choo!",
            malayalamScript = "നല്ലത് / പൊളിച്ചു!",
            usageNote = "In casual conversation, people also use the English word \"Super!\" or slang \"Polichu!\" to mean \"Awesome / Nailed it!\"."
        ),
        DictionaryEntry(
            triggers = listOf("understand", "got it", "okay", "ok"),
            englishLabel = "understand / okay",
            manglishWord = "Manassilaayo? (Did you understand?) / Manassilaayi (I understood) / Sheri (Okay)",
            phonetic = "muh-nus-si-LAA-yoh? / SHEH-ree",
            malayalamScript = "മനസ്സിലായോ? / മനസ്സിലായി / ശരി",
            usageNote = "Locals very often just say \"Okay\" or \"Sheri\" (Alright). Adding \"ketto\" at the end of a sentence also means \"okay? / got it?\"."
        )
    )

    private fun generateDirectTutorAnswer(userMessage: String): ChatMessage {
        val cleanQuery = userMessage.lowercase()
            .replace("how do i say", "")
            .replace("how to say", "")
            .replace("what is the word for", "")
            .replace("what's the word for", "")
            .replace("what does", "")
            .replace("mean", "")
            .replace("in malayalam", "")
            .replace("in manglish", "")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        // 1. Check built-in Q&A dictionary entries first
        val matchedEntry = entries.firstOrNull { entry ->
            entry.triggers.any { trigger ->
                cleanQuery == trigger.trim() ||
                    cleanQuery.split(" ").contains(trigger.trim()) ||
                    (trigger.contains(" ") && cleanQuery.contains(trigger.trim()))
            }
        }

        if (matchedEntry != null) {
            return ChatMessage(
                id = "loc_${System.currentTimeMillis()}",
                isUser = false,
                englishText = "The word for ${matchedEntry.englishLabel} is \"${matchedEntry.manglishWord}\" (pronounced ${matchedEntry.phonetic}).",
                manglishText = "${matchedEntry.manglishWord} (${matchedEntry.phonetic})",
                malayalamText = matchedEntry.malayalamScript,
                correctionFeedback = matchedEntry.usageNote
            )
        }

        // 2. Check all 36 phrases in DialectRepository
        val matchedPhrase = DialectRepository.allPhrases.firstOrNull { p ->
            val pManglish = p.manglish.lowercase().replace(Regex("[^a-z0-9 ]"), "")
            val pEnglish = p.english.lowercase().replace(Regex("[^a-z0-9 ]"), "")
            cleanQuery.isNotBlank() && (
                pManglish.contains(cleanQuery) ||
                    pEnglish.contains(cleanQuery) ||
                    cleanQuery.contains(pManglish)
                )
        }

        if (matchedPhrase != null) {
            return ChatMessage(
                id = "loc_${System.currentTimeMillis()}",
                isUser = false,
                englishText = "The word/phrase for \"${matchedPhrase.english}\" is \"${matchedPhrase.manglish}\" (pronounced ${matchedPhrase.phoneticGuide}).",
                manglishText = "${matchedPhrase.manglish} (${matchedPhrase.phoneticGuide})",
                malayalamText = matchedPhrase.malayalam,
                correctionFeedback = buildString {
                    if (matchedPhrase.wordBreakdown.isNotBlank()) {
                        append("Word-by-word: ${matchedPhrase.wordBreakdown}. ")
                    }
                    if (matchedPhrase.regionalNuance.isNotBlank()) {
                        append(matchedPhrase.regionalNuance)
                    }
                    if (matchedPhrase.textbookAlternative.isNotBlank()) {
                        append(" (Formal textbook version: \"${matchedPhrase.textbookAlternative}\").")
                    }
                }
            )
        }

        // 3. Helpful fallback showing common beginner examples
        return ChatMessage(
            id = "loc_${System.currentTimeMillis()}",
            isUser = false,
            englishText = "Here are the most useful beginner words for \"$userMessage\":\n• The word for hello is \"Hello\" (casual) or \"Namaskaram\" (formal)\n• The word for how are you is \"Sugamaano?\" (soo-guh-MAH-noh)\n• The word for yes / no is \"Athe\" / \"Illa\"\n• The word for I / you is \"Njaan\" / \"Nee\"",
            manglishText = "Sugamaano? / Athe / Illa / Njaan",
            malayalamText = "സുഖമാണോ? / അതെ / ഇല്ല / ഞാൻ",
            correctionFeedback = "Tip: Try asking me \"How do I say hello?\", \"How do I say thank you?\", \"How do I say I don't know?\", \"How do I say water?\", or \"How do I say where?\"!"
        )
    }
}
