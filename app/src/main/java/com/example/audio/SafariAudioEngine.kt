package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SafariAudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var toneGenerator: ToneGenerator? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private val ambientPlayer = JungleAmbientPlayer()

    var isSoundEnabled: Boolean = true
    var isVoiceEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
        private set

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            Log.e("SafariAudio", "Failed to init ToneGenerator", e)
        }
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("SafariAudio", "Failed to init TTS", e)
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            ambientPlayer.start()
        } else {
            ambientPlayer.stop()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            textToSpeech?.setPitch(1.2f) // Cheerful, higher pitch for kids
            textToSpeech?.setSpeechRate(0.85f) // Slightly slower for young explorers
        }
    }

    fun playTap() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun playCorrect() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 120)
                delay(120)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 160)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun playIncorrect() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun playCelebration() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
                delay(100)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 120)
                delay(120)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_SS, 200)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun speak(text: String) {
        if (!isVoiceEnabled || !isTtsReady) return
        try {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "safari_voice")
        } catch (e: Exception) {
            Log.e("SafariAudio", "Error speaking text", e)
        }
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            // ignore
        }
    }

    fun release() {
        ambientPlayer.stop()
        toneGenerator?.release()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
