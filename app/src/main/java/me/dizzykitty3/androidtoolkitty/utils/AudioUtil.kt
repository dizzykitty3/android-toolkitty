package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import android.media.AudioManager
import android.view.View
import androidx.core.content.getSystemService
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar
import kotlin.math.roundToInt

private val Context.audioManager: AudioManager?
    get() = getSystemService()

val Context.mediaVolume: Int
    get() = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0

val Context.voiceCallVolume: Int
    get() = audioManager?.getStreamVolume(AudioManager.STREAM_VOICE_CALL) ?: 0

val Context.maxMediaVolumeIndex: Int
    get() = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 0

val Context.maxVoiceCallVolumeIndex: Int
    get() = audioManager?.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) ?: 0

private fun Context.setMediaVolume(volume: Int) =
    audioManager?.setStreamVolume(
        AudioManager.STREAM_MUSIC,
        volume,
        AudioManager.FLAG_SHOW_UI
    )

fun Context.setVoiceCallVolume(volume: Int) =
    audioManager?.setStreamVolume(
        AudioManager.STREAM_VOICE_CALL,
        volume,
        AudioManager.FLAG_SHOW_UI
    )

fun View.setVolume(volume: Int) {
    context.setMediaVolume(volume)
    this.showSnackbar(R.string.volume_changed)
}

fun View.setVolume(volume: Double) {
    context.setMediaVolume(volume.roundToInt())
    this.showSnackbar(R.string.volume_changed)
}
