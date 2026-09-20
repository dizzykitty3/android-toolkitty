package me.dizzykitty3.androidtoolkitty.datastore

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserSettingsTest {

    @Test
    fun isShown_defaultsToTrueAndHonorsPersistedState() {
        val settings = UserSettings.default().copy(shownItemStates = mapOf("card_search" to false))

        assertFalse(settings.isShown("card_search"))
        assertTrue(settings.isShown("card_unknown"))
    }
}
