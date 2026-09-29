package me.dizzykitty3.androidtoolkitty.home

import android.content.Context
import me.dizzykitty3.androidtoolkitty.S_ALARMS
import me.dizzykitty3.androidtoolkitty.S_APP_NOTIFICATIONS
import me.dizzykitty3.androidtoolkitty.S_AUTO_ROTATE
import me.dizzykitty3.androidtoolkitty.S_MEDIA_MANAGEMENT
import me.dizzykitty3.androidtoolkitty.S_SEARCH_SETTINGS
import me.dizzykitty3.androidtoolkitty.S_UNKNOWN_APPS
import me.dizzykitty3.androidtoolkitty.utils.systemShortcutIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SystemShortcutDefinitionsTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    @Config(sdk = [25])
    fun android71_hidesShortcutsIntroducedAfterItsRelease() {
        assertUnavailableShortcuts(
            S_UNKNOWN_APPS, S_SEARCH_SETTINGS, S_AUTO_ROTATE,
            S_ALARMS, S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS,
        )
    }

    @Test
    @Config(sdk = [26, 28])
    fun android8Through9_exposesUnknownAppShortcutsButNotNewerShortcuts() {
        assertUnavailableShortcuts(
            S_SEARCH_SETTINGS, S_AUTO_ROTATE, S_ALARMS,
            S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS,
        )
    }

    @Test
    @Config(sdk = [29, 30])
    fun android10And11_exposeSearchShortcutsButNotNewerShortcuts() {
        assertUnavailableShortcuts(S_AUTO_ROTATE, S_ALARMS, S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS)
    }

    @Test
    @Config(sdk = [31, 32])
    fun android12And12L_hideOnlyAllAppNotificationShortcut() {
        assertUnavailableShortcuts(S_APP_NOTIFICATIONS)
    }

    @Test
    @Config(sdk = [33])
    fun android13_exposesEveryDefinedShortcut() {
        assertUnavailableShortcuts()
    }

    private fun assertUnavailableShortcuts(vararg unavailableShortcutTypes: String) {
        val unavailable = unavailableShortcutTypes.toSet()
        val available = availableSystemShortcuts()
        assertEquals(
            unavailable,
            (systemShortcutDefinitions - available.toSet()).map { it.shortcutType }.toSet(),
        )
        assertEquals(
            systemShortcutDefinitions.filterNot { it.shortcutType in unavailable },
            available,
        )
        available.forEach { shortcut ->
            assertNotNull(shortcut.shortcutType, systemShortcutIntent(shortcut.shortcutType))
        }
    }

    @Test
    fun definitions_haveUniqueTypesAndAvailableSettingsAreAValidSubset() {
        val shortcutTypes = systemShortcutDefinitions.map { it.shortcutType }
        val availableShortcuts = availableSystemShortcuts()

        assertEquals(shortcutTypes.size, shortcutTypes.distinct().size)
        assertTrue(availableShortcuts.isNotEmpty())
        assertTrue(availableShortcuts.all { it in systemShortcutDefinitions })
        assertTrue(availableShortcuts.all { it.isAvailable() })
    }

    @Test
    fun currentAndroidVersion_exposesEveryDefinedSystemShortcut() {
        assertEquals(systemShortcutDefinitions, availableSystemShortcuts())
    }

    @Test
    fun definitions_referenceNonBlankTitleResources() {
        systemShortcutDefinitions.forEach { shortcut ->
            assertTrue(context.getString(shortcut.title).isNotBlank())
        }
    }

    @Test
    fun availableShortcuts_allResolveToSystemIntents() {
        availableSystemShortcuts().forEach { shortcut ->
            assertNotNull(systemShortcutIntent(shortcut.shortcutType))
        }
    }
}
