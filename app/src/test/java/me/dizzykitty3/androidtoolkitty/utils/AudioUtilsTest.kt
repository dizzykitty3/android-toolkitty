package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AudioUtilsTest {

    @Test
    fun voiceCallVolume_readsAndUpdatesTheVoiceCallStream() {
        val context: Context = RuntimeEnvironment.getApplication()
        val targetVolume = 1

        assertTrue(context.maxMediaVolumeIndex > 0)
        assertTrue(context.maxVoiceCallVolumeIndex >= targetVolume)
        context.setVoiceCallVolume(targetVolume)

        assertEquals(targetVolume, context.voiceCallVolume)
    }

    @Test
    fun mediaVolume_readsAndUpdatesTheMediaStream() {
        val context: Context = RuntimeEnvironment.getApplication()
        val targetVolume = 1

        assertTrue(context.maxMediaVolumeIndex >= targetVolume)
        context.setMediaVolume(targetVolume)

        assertEquals(targetVolume, context.mediaVolume)
    }
}
