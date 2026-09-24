package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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
    fun homeCardOrder_movesAtomicallyAndResetPreservesVisibility() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(PreferenceDataStoreFactory.create(scope = backgroundScope) { file })
        val defaults = listOf("card_a", "card_b", "card_c", "card_d")
        repository.saveShownState("card_d", false)
        List(3) { launch { repository.moveHomeCard("card_d", -1, defaults) } }.joinAll()
        assertEquals(listOf("card_d", "card_a", "card_b", "card_c"), repository.settingsFlow.first().homeCardOrder)
        repository.moveHomeCard("card_d", -1, defaults)
        repository.moveHomeCard("missing", 1, defaults)
        repository.moveHomeCard("card_d", 2, defaults)
        assertEquals(listOf("card_d", "card_a", "card_b", "card_c"), repository.settingsFlow.first().homeCardOrder)
        repository.moveHomeCard("card_d", 1, defaults)
        assertEquals(listOf("card_a", "card_d", "card_b", "card_c"), repository.settingsFlow.first().homeCardOrder)

        repository.resetHomeCardOrder()
        assertEquals(emptyList<String>(), repository.settingsFlow.first().homeCardOrder)
        assertFalse(repository.settingsFlow.first().isShown("card_d"))
    }

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
    fun saveShownState_keepsRecognizedKeysAndFiltersUnrelatedKeys() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )

        repository.saveShownState("setting_vpn", false)
        repository.saveShownState("unrelated_key", false)

        val settings = repository.settingsFlow.first { !it.isShown("setting_vpn") }
        assertFalse(settings.isShown("setting_vpn"))
        assertEquals(mapOf("setting_vpn" to false), settings.shownItemStates)
        assertEquals(true, settings.isShown("unrelated_key"))
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

    @Test
    fun reEnablingSearchHistory_allowsTypingContentsToBeSavedAgain() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )

        repository.setDoNotRememberLastSearch(true)
        repository.setDoNotRememberLastSearch(false)
        repository.updateTypingContents("remember again")
        repository.toggleAutoClearClipboard(true)

        val settings = repository.settingsFlow.first { it.typingContents == "remember again" }
        assertFalse(settings.doNotRememberLastSearch)
        assertEquals("remember again", settings.typingContents)
        assertEquals(true, settings.autoClearClipboard)
    }

    @Test
    fun remainingUserInputs_arePersistedWithoutAffectingOtherSettings() = runTest {
        val file = newPreferencesFile()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )
        val wheelItems = "{\"items\":[\"Tea\",\"Coffee\"]}"

        repository.updateLastSelectedPlatformIndex(4)
        repository.updateLatitude("25.0330")
        repository.updateLongitude("121.5654")
        repository.toggleHaveTappedAddButton(true)
        repository.updateWheelOfFortuneItems(wheelItems)

        val settings = repository.settingsFlow.first { it.wheelOfFortuneItems == wheelItems }
        assertEquals(4, settings.lastSelectedPlatformIndex)
        assertEquals("25.0330", settings.latitude)
        assertEquals("121.5654", settings.longitude)
        assertEquals(true, settings.haveTappedAddButton)
        assertEquals(wheelItems, settings.wheelOfFortuneItems)
    }

    @Test
    fun concurrentTapCountUpdates_preserveEveryIncrementAndExistingSettings() = runTest {
        val file = newPreferencesFile()
        val dataStore = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
        val repository = SettingsRepository(dataStore)
        dataStore.edit { preferences ->
            preferences[intPreferencesKey("have_tapped_volume_button")] = 7
        }
        repository.updateTypingContents("keep this search")

        List(100) {
            launch { repository.increaseVolumeButtonTapCount() }
        }.joinAll()

        val settings = repository.settingsFlow.first()
        assertEquals(107, settings.volumeButtonTapCount)
        assertEquals("keep this search", settings.typingContents)
    }

    @Test
    fun settings_surviveClosingAndReopeningTheDataStore() = runTest {
        val file = newPreferencesFile()
        val writerJob = Job()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(
                scope = CoroutineScope(coroutineContext + writerJob),
            ) { file },
        )
        val expected = UserSettings.default().copy(
            searchEngine = SearchEngine.ECOSIA,
            typingContents = "saved search",
            customVolume = 42,
            shownItemStates = mapOf("card_search" to false),
            homeCardOrder = listOf("card_maps", "card_search"),
        )
        try {
            repository.setSearchEngine(SearchEngine.ECOSIA)
            repository.updateTypingContents("saved search")
            repository.updateCustomVolume(42)
            repository.saveShownState("card_search", false)
            repository.moveHomeCard("card_maps", -1, listOf("card_search", "card_maps"))
        } finally {
            writerJob.cancelAndJoin()
        }

        val reopened = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )
        assertEquals(expected, reopened.settingsFlow.first())
    }

    @Test
    fun disabledSearchHistory_staysClearedAfterReopeningTheDataStore() = runTest {
        val file = newPreferencesFile()
        val writerJob = Job()
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(
                scope = CoroutineScope(coroutineContext + writerJob),
            ) { file },
        )
        try {
            repository.updateTypingContents("private search")
            repository.setDoNotRememberLastSearch(true)
            repository.updateTypingContents("must not survive restart")
        } finally {
            writerJob.cancelAndJoin()
        }

        val reopened = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )
        assertEquals(
            UserSettings.default().copy(doNotRememberLastSearch = true),
            reopened.settingsFlow.first(),
        )
    }

    @Test
    fun concurrentSearchUpdates_doNotRetainTextWhenPrivacyIsEnabled() = runTest {
        val file = newPreferencesFile()
        val dataStore = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
        val repository = SettingsRepository(dataStore)
        repository.updateTypingContents("previous search")
        repository.updateLatitude("25.0330")

        val writersBefore = List(10) { index ->
            launch { repository.updateTypingContents("before $index") }
        }
        val enablePrivacy = launch { repository.setDoNotRememberLastSearch(true) }
        val writersAfter = List(10) { index ->
            launch { repository.updateTypingContents("after $index") }
        }
        (writersBefore + enablePrivacy + writersAfter).joinAll()

        val settings = repository.settingsFlow.first()
        assertEquals(true, settings.doNotRememberLastSearch)
        assertEquals("", settings.typingContents)
        assertEquals("25.0330", settings.latitude)
        assertNull(dataStore.data.first()[stringPreferencesKey("typing_contents")])
    }

    private fun newPreferencesFile(): File =
        File.createTempFile("toolkitty-settings-", ".preferences_pb").apply {
            delete()
            deleteOnExit()
        }
}
