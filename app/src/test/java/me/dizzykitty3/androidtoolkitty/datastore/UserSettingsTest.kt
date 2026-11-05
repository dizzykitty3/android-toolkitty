package me.dizzykitty3.androidtoolkitty.datastore

import me.dizzykitty3.androidtoolkitty.utils.SearchEngine
import me.dizzykitty3.androidtoolkitty.utils.VideoSearchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserSettingsTest {

    @Test
    fun isShown_defaultsToTrueAndHonorsPersistedState() {
        val settings = UserSettings.default().copy(shownItemStates = mapOf("card_search" to false))

        assertFalse(settings.isShown("card_search"))
        assertTrue(settings.isShown("card_unknown"))
    }

    @Test
    fun shortcuts_defaultToFourPinsAndHonorSavedOverrides() {
        val settings = UserSettings.default()
        listOf("setting_about_phone", "setting_search_settings", "setting_display", "setting_sound")
            .forEach { assertTrue(it, settings.isShown(it)) }
        assertFalse(settings.isShown("setting_wifi"))
        val customized = settings.copy(shownItemStates = mapOf(
            "setting_wifi" to true,
            "setting_display" to false,
        ))
        assertTrue(customized.isShown("setting_wifi"))
        assertFalse(customized.isShown("setting_display"))
    }

    @Test
    fun default_providesTheExpectedInitialExperience() {
        val settings = UserSettings.default()

        assertTrue(settings.dynamicColor)
        assertFalse(settings.autoClearClipboard)
        assertEquals(SearchEngine.GOOGLE, settings.searchEngine)
        assertEquals(VideoSearchEngine.YOUTUBE, settings.videoSearchEngine)
        assertFalse(settings.doNotRememberLastSearch)
        assertEquals(0, settings.lastSelectedPlatformIndex)
        assertEquals("", settings.typingContents)
        assertEquals("", settings.latitude)
        assertEquals("", settings.longitude)
        assertEquals(listOf(null, null, null), settings.customVolumes)
        assertEquals(0, settings.volumeButtonTapCount)
        assertNull(settings.wheelOfFortuneItems)
        assertTrue(settings.shownItemStates.isEmpty())
    }
}
