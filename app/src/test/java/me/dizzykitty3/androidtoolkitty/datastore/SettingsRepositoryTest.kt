package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.dizzykitty3.androidtoolkitty.utils.SearchEngine
import me.dizzykitty3.androidtoolkitty.utils.VideoSearchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsRepositoryTest {

    @Test
    fun settingsFlow_returnsDefaultsForANewDataStore() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )

        assertEquals(UserSettings.default(), repository.settingsFlow.first())
    }

    @Test
    fun updates_arePersistedAndExposedBySettingsFlow() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )

        repository.toggleDynamicColor(false)
        repository.setSearchEngine(SearchEngine.ECOSIA)
        repository.setVideoSearchEngine(VideoSearchEngine.BILIBILI)
        repository.saveShownState("card_search", false)
        repository.updateCustomVolume(42)
        repository.increaseVolumeButtonTapCount()

        val settings = repository.settingsFlow.first { it.customVolume == 42 }
        assertFalse(settings.dynamicColor)
        assertEquals(SearchEngine.ECOSIA, settings.searchEngine)
        assertEquals(VideoSearchEngine.BILIBILI, settings.videoSearchEngine)
        assertFalse(settings.isShown("card_search"))
        assertEquals(1, settings.volumeButtonTapCount)
    }

    @Test
    fun doNotRememberLastSearch_removesAndPreventsSavingTypingContents() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )

        repository.updateTypingContents("first search")
        repository.setDoNotRememberLastSearch(true)
        repository.updateTypingContents("must not persist")

        val settings = repository.settingsFlow.first { it.doNotRememberLastSearch }
        assertEquals("", settings.typingContents)
        assertNull(settings.customVolume)
    }

    private fun newPreferencesFile(): File =
        File.createTempFile("toolkitty-settings-", ".preferences_pb").apply {
            delete()
            deleteOnExit()
        }
}
