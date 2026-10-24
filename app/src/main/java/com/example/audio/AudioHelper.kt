package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class AudioHelper(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean = false
        private set

    init {
        tts = TextToSpeech(context.applicationContext, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val malayalamLocale = Locale("ml", "IN")
            val langResult = tts?.setLanguage(malayalamLocale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Malayalam voice data not installed; fall back to Indian English or default
                tts?.setLanguage(Locale("en", "IN"))
            }
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(0.95f) // Slightly relaxed natural cadence
            isTtsReady = true
        } else {
            Log.w("AudioHelper", "TextToSpeech init failed")
        }
    }

    fun speak(malayalamText: String, manglishPhonetic: String = "") {
        speakMalayalam(malayalamText, manglishPhonetic)
    }

    fun speakMalayalam(malayalamText: String, manglishPhonetic: String = "", queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isTtsReady || tts == null) return
        try {
            val malayalamLocale = Locale("ml", "IN")
            val langResult = tts?.setLanguage(malayalamLocale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale("en", "IN"))
            }
        } catch (_: Exception) {}
        val textToSpeak = if (malayalamText.isNotBlank()) malayalamText else manglishPhonetic
        tts?.speak(textToSpeak, queueMode, null, "Utterance_ML_${System.currentTimeMillis()}")
    }

    fun speakEnglish(englishText: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isTtsReady || tts == null) return
        try {
            tts?.setLanguage(Locale.US)
        } catch (_: Exception) {}
        tts?.speak(englishText, queueMode, null, "Utterance_EN_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
    }

    fun startListening(
        onResult: (spokenText: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
        onReadyForSpeech: () -> Unit = {}
    ) {
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }

        if (speechRecognizer == null) {
            onError("Speech recognition not available on this device")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ml-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ml-IN")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                onReadyForSpeech()
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client speech error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                    SpeechRecognizer.ERROR_NETWORK -> "Network issue for speech recognition"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Speech timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please speak clearly."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy, please retry"
                    SpeechRecognizer.ERROR_SERVER -> "Speech server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timed out"
                    else -> "Speech recognition error ($error)"
                }
                onError(message)
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult(matches[0])
                } else {
                    onError("No speech matched")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            isListening = false
            onError("Could not start speech recognition: ${e.message}")
        }
    }

    fun stopListening() {
        isListening = false
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
    }
}
