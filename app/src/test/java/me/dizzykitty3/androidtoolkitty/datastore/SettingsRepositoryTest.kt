package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
        repository.increaseVolumeButtonTapCount()

        val settings = repository.settingsFlow.first { it.customVolume == 42 }
        assertFalse(settings.dynamicColor)
        assertEquals(SearchEngine.ECOSIA, settings.searchEngine)
        assertEquals(VideoSearchEngine.BILIBILI, settings.videoSearchEngine)
        assertFalse(settings.isShown("card_search"))
        assertEquals(2, settings.volumeButtonTapCount)
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

    @Test
    fun invalidStoredValues_fallBackToDefaultsAndIgnoreUnrecognizedShownStateKeys() = runTest {
        val file = newPreferencesFile()
        val dataStore = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
        val repository = SettingsRepository(dataStore)

        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("search_engine")] = "UNKNOWN"
            preferences[stringPreferencesKey("video_search_engine")] = "VIMEO"
            preferences[intPreferencesKey("custom_volume")] = Int.MIN_VALUE
            preferences[booleanPreferencesKey("card_search")] = false
            preferences[booleanPreferencesKey("setting_theme")] = true
            preferences[booleanPreferencesKey("other_key")] = false
        }

        val settings = repository.settingsFlow.first()
        assertEquals(UserSettings.default().searchEngine, settings.searchEngine)
        assertEquals(UserSettings.default().videoSearchEngine, settings.videoSearchEngine)
        assertNull(settings.customVolume)
        assertFalse(settings.isShown("card_search"))
        assertEquals(
            mapOf("card_search" to false, "setting_theme" to true),
            settings.shownItemStates,
        )
    }

    @Test
    fun typingContents_arePersistedWhenRememberingLastSearchIsEnabled() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )

        repository.updateTypingContents("remember this")

        assertEquals("remember this", repository.settingsFlow.first().typingContents)
    }

    private fun newPreferencesFile(): File =
        File.createTempFile("toolkitty-settings-", ".preferences_pb").apply {
            delete()
            deleteOnExit()
        }
}
