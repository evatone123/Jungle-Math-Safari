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

class JungleAmbientPlayer {

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isPlaying: Boolean = false
        private set

    private val sampleRate = 22050
    private val bufferSize = 2048

    // Pentatonic scale frequencies for soothing jungle melody (C4, D4, E4, G4, A4, C5, D5)
    private val melodyFrequencies = floatArrayOf(
        261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f, 587.33f
    )

    fun start() {
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

                // Synthesis State
                var windFilter = 0f
                var lfoAngle = 0.0
                val lfoSpeed = 2.0 * PI * 0.12 / sampleRate // ~0.12 Hz gentle swaying wind

                // Music Note State
                var currentFreq = melodyFrequencies[0]
                var notePhase = 0.0
                var noteSampleCount = 0
                val noteDurationSamples = (sampleRate * 2.2).toInt() // 2.2s per soft note
                var noteIndex = 0

                // Occasional Bird / Flute Chirp State
                var chirpTimer = sampleRate * 3
                var chirpActive = false
                var chirpPhase = 0.0
                var chirpFreq = 1200f
                var chirpSampleCount = 0
                val chirpDuration = (sampleRate * 0.35).toInt()

                while (isActive && isPlaying) {
                    for (i in 0 until bufferSize) {
                        // 1. Soft Jungle Breeze / Leaves (Low-pass pinkish noise + LFO)
                        val white = (Random.nextFloat() * 2f - 1f)
                        windFilter += 0.035f * (white - windFilter)
                        lfoAngle += lfoSpeed
                        if (lfoAngle > 2.0 * PI) lfoAngle -= 2.0 * PI
                        val lfoVol = (sin(lfoAngle).toFloat() * 0.4f + 0.6f)
                        val windSample = windFilter * lfoVol * 0.08f

                        // 2. Soft Ambient Pentatonic Music Note
                        if (noteSampleCount >= noteDurationSamples) {
                            noteSampleCount = 0
                            noteIndex = (noteIndex + Random.nextInt(1, 4)) % melodyFrequencies.size
                            currentFreq = melodyFrequencies[noteIndex]
                        }

                        val noteTime = noteSampleCount.toFloat() / noteDurationSamples
                        // Soft envelope: attack 10%, decay curve
                        val envelope = if (noteTime < 0.1f) {
                            noteTime / 0.1f
                        } else {
                            (1f - (noteTime - 0.1f) / 0.9f) * 0.85f
                        }

                        val noteStep = 2.0 * PI * currentFreq / sampleRate
                        notePhase += noteStep
                        if (notePhase > 2.0 * PI) notePhase -= 2.0 * PI

                        // Soft warm sine + subtle overtone
                        val noteWave = (sin(notePhase).toFloat() * 0.75f + sin(notePhase * 2.0).toFloat() * 0.15f)
                        val musicSample = noteWave * envelope * 0.12f
                        noteSampleCount++

                        // 3. Occasional Distant Jungle Bird / Bamboo Flute (every ~6-8s)
                        var chirpSample = 0f
                        chirpTimer--
                        if (chirpTimer <= 0 && !chirpActive) {
                            chirpActive = true
                            chirpSampleCount = 0
                            chirpFreq = if (Random.nextBoolean()) 1318.5f else 1567.98f // E6 or G6
                        }

                        if (chirpActive) {
                            val cStep = 2.0 * PI * chirpFreq / sampleRate
                            chirpPhase += cStep
                            if (chirpPhase > 2.0 * PI) chirpPhase -= 2.0 * PI

                            val cTime = chirpSampleCount.toFloat() / chirpDuration
                            val cEnv = if (cTime < 0.15f) (cTime / 0.15f) else (1f - (cTime - 0.15f) / 0.85f)
                            chirpSample = sin(chirpPhase).toFloat() * cEnv * 0.06f
                            chirpSampleCount++

                            if (chirpSampleCount >= chirpDuration) {
                                chirpActive = false
                                chirpTimer = sampleRate * Random.nextInt(6, 11)
                            }
                        }

                        // Combined Soft Jungle Atmosphere
                        val mixed = (windSample + musicSample + chirpSample).coerceIn(-1f, 1f)
                        buffer[i] = (mixed * 32767f).toInt().toShort()
                    }

                    track.write(buffer, 0, bufferSize)
                }

                track.stop()
                track.release()
            } catch (e: Exception) {
                Log.e("JungleAmbientPlayer", "Audio error", e)
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
}
