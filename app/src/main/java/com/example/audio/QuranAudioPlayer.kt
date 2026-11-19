package com.example.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.data.local.QuranData
import com.example.data.local.QuranSurah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RecitationPlaybackState(
    val selectedSurahNumber: Int = 1,
    val currentAyahIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val repeatAyah: Boolean = false,
    val errorMessage: String? = null
)

class QuranAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow(RecitationPlaybackState())
    val state: StateFlow<RecitationPlaybackState> = _state.asStateFlow()

    fun selectSurah(surahNumber: Int) {
        stop()
        _state.value = _state.value.copy(
            selectedSurahNumber = surahNumber,
            currentAyahIndex = 0,
            errorMessage = null
        )
    }

    fun playAyah(surahNumber: Int, ayahIndex: Int) {
        val surah = QuranData.surahs.firstOrNull { it.surahNumber == surahNumber } ?: return
        val ayah = surah.ayahs.getOrNull(ayahIndex) ?: return

        releasePlayer()
        _state.value = _state.value.copy(
            selectedSurahNumber = surahNumber,
            currentAyahIndex = ayahIndex,
            isBuffering = true,
            isPlaying = false,
            errorMessage = null
        )

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(ayah.audioUrl)
                setOnPreparedListener { mp ->
                    runCatching {
                        val params = mp.playbackParams
                        params.speed = _state.value.playbackSpeed
                        mp.playbackParams = params
                    }
                    mp.start()
                    _state.value = _state.value.copy(isBuffering = false, isPlaying = true)
                }
                setOnCompletionListener {
                    handleAyahCompletion(surah, ayahIndex)
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = _state.value.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Audio stream unavailable offline or network restricted. Arabic & translation remain available."
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isBuffering = false,
                isPlaying = false,
                errorMessage = e.localizedMessage ?: "Unable to stream Quran recitation."
            )
        }
    }

    private fun handleAyahCompletion(surah: QuranSurah, completedIndex: Int) {
        val currentState = _state.value
        if (currentState.repeatAyah) {
            playAyah(surah.surahNumber, completedIndex)
        } else if (completedIndex + 1 < surah.ayahs.size) {
            playAyah(surah.surahNumber, completedIndex + 1)
        } else {
            _state.value = currentState.copy(isPlaying = false, isBuffering = false)
        }
    }

    fun togglePlayPause() {
        val current = _state.value
        val player = mediaPlayer
        if (player != null && current.isPlaying) {
            runCatching { player.pause() }
            _state.value = current.copy(isPlaying = false)
        } else if (player != null && !current.isBuffering && current.errorMessage == null) {
            val started = runCatching {
                player.start()
                true
            }.getOrDefault(false)
            if (started) {
                _state.value = current.copy(isPlaying = true)
            } else {
                playAyah(current.selectedSurahNumber, current.currentAyahIndex)
            }
        } else {
            playAyah(current.selectedSurahNumber, current.currentAyahIndex)
        }
    }

    fun toggleRepeatAyah() {
        _state.value = _state.value.copy(repeatAyah = !_state.value.repeatAyah)
    }

    fun cycleSpeed() {
        val next = when (_state.value.playbackSpeed) {
            1.0f -> 1.25f
            1.25f -> 0.85f
            else -> 1.0f
        }
        _state.value = _state.value.copy(playbackSpeed = next)
        mediaPlayer?.let { mp ->
            runCatching {
                if (mp.isPlaying) {
                    val params = mp.playbackParams
                    params.speed = next
                    mp.playbackParams = params
                }
            }
        }
    }

    fun stop() {
        releasePlayer()
        _state.value = _state.value.copy(isPlaying = false, isBuffering = false)
    }

    private fun releasePlayer() {
        runCatching {
            mediaPlayer?.stop()
        }
        runCatching {
            mediaPlayer?.release()
        }
        mediaPlayer = null
    }
}
