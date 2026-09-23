package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import org.junit.Assert.assertNull
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
    fun missingAudioService_returnsZeroVolumesAndAcceptsUpdatesSafely() {
        val context = object : ContextWrapper(RuntimeEnvironment.getApplication()) {
            override fun getSystemService(name: String): Any? =
                if (name == Context.AUDIO_SERVICE) null else super.getSystemService(name)
        }
        assertNull(context.getSystemService(AudioManager::class.java))

        context.setMediaVolume(5)
        context.setVoiceCallVolume(3)

        assertEquals(0, context.mediaVolume)
        assertEquals(0, context.voiceCallVolume)
        assertEquals(0, context.maxMediaVolumeIndex)
        assertEquals(0, context.maxVoiceCallVolumeIndex)
    }

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
