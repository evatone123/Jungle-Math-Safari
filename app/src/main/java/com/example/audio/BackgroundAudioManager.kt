package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * BackgroundAudioManager
 *
 * Synthesizes calming, jungle-themed nature sounds and light background music
 * directly via real-time PCM audio streaming on Android.
 *
 * Generates:
 * 1. Jungle Nature Ambience: Low-pass filtered pinkish wind/canopy rustle modulated by an ultra-slow LFO (0.1 Hz)
 * 2. Soothing Water/Stream Flow or Forest Drops: Soft randomized stream trickles
 * 3. Light Pentatonic Music: Gentle wooden bamboo marimba/kalimba notes (C, D, E, G, A pentatonic scale)
 * 4. Distant Melodic Birds / Flute notes: Soft, cheerful occasional bird calls (E6, G6, A6)
 *
 * Zero external audio assets needed; zero licensing or missing asset issues. Plays smoothly offline.
 */
class BackgroundAudioManager {

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isPlaying: Boolean = false
        private set

    @Volatile
    var currentTheme: JungleAudioTheme = JungleAudioTheme.SERENE_RIVER

    @Volatile
    var masterVolume: Float = 0.45f // Gentle background balance

    private val sampleRate = 22050
    private val bufferSize = 2048

    // Pentatonic scale frequencies for soothing jungle melodies (C4, D4, E4, G4, A4, C5, D5, E5)
    private val melodyFrequencies = floatArrayOf(
        261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f, 587.33f, 659.25f
    )

    fun start(theme: JungleAudioTheme = currentTheme) {
        currentTheme = theme
        if (isPlaying) return
        isPlaying = true

        playbackJob = scope.launch {
            try {
                val minBuffer = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(maxOf(minBuffer, bufferSize * 4))
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()

                val buffer = ShortArray(bufferSize)

                // 1. Wind & Canopy State
                var windFilter = 0f
                var lfoAngle = 0.0
                val lfoSpeed = 2.0 * PI * 0.10 / sampleRate // ~0.10 Hz gentle swaying canopy

                // 2. Stream / Water Trickle State
                var waterFilter = 0f
                var waterLfo = 0.0
                val waterLfoSpeed = 2.0 * PI * 0.25 / sampleRate

                // 3. Music Note State (Wooden Marimba / Kalimba / Bell)
                var currentFreq = melodyFrequencies[0]
                var notePhase = 0.0
                var noteSampleCount = 0
                var noteDurationSamples = (sampleRate * 2.0).toInt()
                var noteIndex = 0

                // 4. Bird / Bamboo Flute Chirp State
                var chirpTimer = sampleRate * 3
                var chirpActive = false
                var chirpPhase = 0.0
                var chirpFreq = 1318.5f
                var chirpSampleCount = 0
                val chirpDuration = (sampleRate * 0.38).toInt()

                while (isActive && isPlaying) {
                    val activeTheme = currentTheme
                    val vol = masterVolume

                    for (i in 0 until bufferSize) {
                        // --- A. Gentle Jungle Breeze (Soft low-pass noise with LFO) ---
                        val white = (Random.nextFloat() * 2f - 1f)
                        windFilter += 0.030f * (white - windFilter)
                        lfoAngle += lfoSpeed
                        if (lfoAngle > 2.0 * PI) lfoAngle -= 2.0 * PI
                        val lfoVol = (sin(lfoAngle).toFloat() * 0.35f + 0.65f)
                        val windSample = windFilter * lfoVol * 0.07f

                        // --- B. Water Stream or Nature Atmosphere ---
                        var waterSample = 0f
                        if (activeTheme == JungleAudioTheme.SERENE_RIVER || activeTheme == JungleAudioTheme.RAINFOREST_CALM) {
                            val waterWhite = (Random.nextFloat() * 2f - 1f)
                            waterFilter += 0.08f * (waterWhite - waterFilter)
                            waterLfo += waterLfoSpeed
                            if (waterLfo > 2.0 * PI) waterLfo -= 2.0 * PI
                            val streamMod = (sin(waterLfo).toFloat() * 0.3f + 0.7f)
                            waterSample = waterFilter * streamMod * 0.05f
                        }

                        // --- C. Light Background Music (Pentatonic Marimba/Flute) ---
                        if (noteSampleCount >= noteDurationSamples) {
                            noteSampleCount = 0
                            // Melodic interval step (+1, +2, or wrapping)
                            noteIndex = (noteIndex + Random.nextInt(1, 4)) % melodyFrequencies.size
                            currentFreq = melodyFrequencies[noteIndex]
                            noteDurationSamples = (sampleRate * (if (activeTheme == JungleAudioTheme.RAINFOREST_CALM) 2.6 else 1.9)).toInt()
                        }

                        val noteTime = noteSampleCount.toFloat() / noteDurationSamples
                        // Percussive bell/kalimba envelope: fast 5% attack, gentle exponential-style decay
                        val envelope = if (noteTime < 0.05f) {
                            noteTime / 0.05f
                        } else {
                            (1f - (noteTime - 0.05f) / 0.95f) * 0.85f
                        }

                        val noteStep = 2.0 * PI * currentFreq / sampleRate
                        notePhase += noteStep
                        if (notePhase > 2.0 * PI) notePhase -= 2.0 * PI

                        // Soft warm harmonic content (fundamental + soft octave + gentle third harmonic)
                        val noteWave = (
                            sin(notePhase).toFloat() * 0.70f +
                            sin(notePhase * 2.0).toFloat() * 0.20f +
                            sin(notePhase * 3.0).toFloat() * 0.08f
                        )
                        val musicSample = noteWave * envelope * 0.11f
                        noteSampleCount++

                        // --- D. Occasional Jungle Bird or Bamboo Flute Note ---
                        var chirpSample = 0f
                        chirpTimer--
                        if (chirpTimer <= 0 && !chirpActive) {
                            chirpActive = true
                            chirpSampleCount = 0
                            chirpFreq = when (activeTheme) {
                                JungleAudioTheme.CANOPY_BIRDS -> listOf(1318.5f, 1567.98f, 1760.0f, 2093.0f).random()
                                JungleAudioTheme.RAINFOREST_CALM -> listOf(1174.66f, 1318.5f, 1567.98f).random()
                                JungleAudioTheme.SERENE_RIVER -> listOf(1318.5f, 1567.98f).random()
                            }
                        }

                        if (chirpActive) {
                            val cStep = 2.0 * PI * chirpFreq / sampleRate
                            chirpPhase += cStep
                            if (chirpPhase > 2.0 * PI) chirpPhase -= 2.0 * PI

                            val cTime = chirpSampleCount.toFloat() / chirpDuration
                            // Bell curve envelope for bird chirp
                            val cEnv = if (cTime < 0.15f) (cTime / 0.15f) else (1f - (cTime - 0.15f) / 0.85f)
                            chirpSample = sin(chirpPhase).toFloat() * cEnv * 0.06f
                            chirpSampleCount++

                            if (chirpSampleCount >= chirpDuration) {
                                chirpActive = false
                                val nextDelaySec = if (activeTheme == JungleAudioTheme.CANOPY_BIRDS) Random.nextInt(4, 8) else Random.nextInt(7, 13)
                                chirpTimer = sampleRate * nextDelaySec
                            }
                        }

                        // --- E. Master Combination & Output Clamping ---
                        val rawSum = (windSample + waterSample + musicSample + chirpSample) * (vol / 0.45f)
                        val mixed = rawSum.coerceIn(-1f, 1f)
                        buffer[i] = (mixed * 32767f).toInt().toShort()
                    }

                    track.write(buffer, 0, bufferSize)
                }

                track.stop()
                track.release()
            } catch (e: Exception) {
                Log.e("BackgroundAudioManager", "Audio error", e)
            } finally {
                audioTrack = null
                isPlaying = false
            }
        }
    }

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // ignore
        }
        audioTrack = null
    }

    fun setTheme(theme: JungleAudioTheme) {
        currentTheme = theme
        if (!isPlaying) {
            start(theme)
        }
    }

    fun setVolume(volume: Float) {
        masterVolume = volume.coerceIn(0.1f, 1.0f)
    }
}
