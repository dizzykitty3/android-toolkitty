package me.dizzykitty3.androidtoolkitty.utils

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import me.dizzykitty3.androidtoolkitty.S_ABOUT_PHONE
import me.dizzykitty3.androidtoolkitty.S_ACCESSIBILITY
import me.dizzykitty3.androidtoolkitty.S_ACCOUNTS
import me.dizzykitty3.androidtoolkitty.S_ALARMS
import me.dizzykitty3.androidtoolkitty.S_APP_NOTIFICATIONS
import me.dizzykitty3.androidtoolkitty.S_AUTO_ROTATE
import me.dizzykitty3.androidtoolkitty.S_BATTERY
import me.dizzykitty3.androidtoolkitty.S_BATTERY_OPTIMIZATION
import me.dizzykitty3.androidtoolkitty.S_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.S_CAPTION
import me.dizzykitty3.androidtoolkitty.S_DATE
import me.dizzykitty3.androidtoolkitty.S_DEFAULT_APPS
import me.dizzykitty3.androidtoolkitty.S_DEVELOPER
import me.dizzykitty3.androidtoolkitty.S_DISPLAY
import me.dizzykitty3.androidtoolkitty.S_DND_ACCESS
import me.dizzykitty3.androidtoolkitty.S_ENABLE_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.S_KEYBOARD
import me.dizzykitty3.androidtoolkitty.S_LOCALE
import me.dizzykitty3.androidtoolkitty.S_MEDIA_MANAGEMENT
import me.dizzykitty3.androidtoolkitty.S_MODIFY_SYSTEM
import me.dizzykitty3.androidtoolkitty.S_NFC
import me.dizzykitty3.androidtoolkitty.S_NOTIFICATION_LISTENER
import me.dizzykitty3.androidtoolkitty.S_OVERLAY
import me.dizzykitty3.androidtoolkitty.S_SEARCH_SETTINGS
import me.dizzykitty3.androidtoolkitty.S_SOUND
import me.dizzykitty3.androidtoolkitty.S_UNKNOWN_APPS
import me.dizzykitty3.androidtoolkitty.S_USAGE_ACCESS
import me.dizzykitty3.androidtoolkitty.S_VPN
import me.dizzykitty3.androidtoolkitty.S_WIFI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SystemShortcutIntentTest {

    @Test
    fun alwaysSupportedSettings_returnTheirExpectedIntentActions() {
        assertEquals(
            Settings.ACTION_DEVICE_INFO_SETTINGS,
            systemShortcutIntent(S_ABOUT_PHONE)?.action
        )
        assertEquals(Settings.ACTION_DISPLAY_SETTINGS, systemShortcutIntent(S_DISPLAY)?.action)
        assertEquals(Settings.ACTION_BLUETOOTH_SETTINGS, systemShortcutIntent(S_BLUETOOTH)?.action)
        assertEquals(Settings.ACTION_CAPTIONING_SETTINGS, systemShortcutIntent(S_CAPTION)?.action)
        assertEquals(
            Settings.ACTION_USAGE_ACCESS_SETTINGS,
            systemShortcutIntent(S_USAGE_ACCESS)?.action
        )
        assertEquals(Settings.ACTION_LOCALE_SETTINGS, systemShortcutIntent(S_LOCALE)?.action)
        assertEquals(Settings.ACTION_DATE_SETTINGS, systemShortcutIntent(S_DATE)?.action)
        assertEquals(Settings.ACTION_SOUND_SETTINGS, systemShortcutIntent(S_SOUND)?.action)
        assertEquals(
            Settings.ACTION_ACCESSIBILITY_SETTINGS,
            systemShortcutIntent(S_ACCESSIBILITY)?.action
        )
        assertEquals(
            Settings.ACTION_INPUT_METHOD_SETTINGS,
            systemShortcutIntent(S_KEYBOARD)?.action
        )
        assertEquals(
            Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS,
            systemShortcutIntent(S_DEVELOPER)?.action,
        )
        assertEquals(Settings.ACTION_WIFI_SETTINGS, systemShortcutIntent(S_WIFI)?.action)
        assertEquals(Intent.ACTION_POWER_USAGE_SUMMARY, systemShortcutIntent(S_BATTERY)?.action)
        assertEquals(Settings.ACTION_SYNC_SETTINGS, systemShortcutIntent(S_ACCOUNTS)?.action)
        assertEquals(Settings.ACTION_NFC_SETTINGS, systemShortcutIntent(S_NFC)?.action)
        assertEquals(
            BluetoothAdapter.ACTION_REQUEST_ENABLE,
            systemShortcutIntent(S_ENABLE_BLUETOOTH)?.action,
        )
    }

    @Test
    fun conditionalSettings_returnTheirExpectedIntentActionsOnSupportedSdk() {
        val expectedActions = mapOf(
            S_AUTO_ROTATE to Settings.ACTION_AUTO_ROTATE_SETTINGS,
            S_DEFAULT_APPS to Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS,
            S_BATTERY_OPTIMIZATION to Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,
            S_OVERLAY to Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            S_MODIFY_SYSTEM to Settings.ACTION_MANAGE_WRITE_SETTINGS,
            S_NOTIFICATION_LISTENER to Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS,
            S_DND_ACCESS to Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS,
            S_UNKNOWN_APPS to Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            S_ALARMS to Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            S_MEDIA_MANAGEMENT to Settings.ACTION_REQUEST_MANAGE_MEDIA,
            S_APP_NOTIFICATIONS to Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS,
            S_VPN to Settings.ACTION_VPN_SETTINGS,
            S_SEARCH_SETTINGS to Settings.ACTION_APP_SEARCH_SETTINGS,
        )

        expectedActions.forEach { (shortcutType, action) ->
            assertEquals(action, systemShortcutIntent(shortcutType)?.action)
        }
    }

    @Test
    @Config(sdk = [30])
    fun android11_omitsSettingsIntroducedInAndroid12And13() {
        listOf(S_AUTO_ROTATE, S_ALARMS, S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS).forEach {
            assertNull("Unsupported setting: $it", systemShortcutIntent(it))
        }
        assertEquals(
            Settings.ACTION_APP_SEARCH_SETTINGS,
            systemShortcutIntent(S_SEARCH_SETTINGS)?.action
        )
    }

    @Test
    @Config(sdk = [31])
    fun android12_enablesItsSettingsButNotAndroid13Notifications() {
        assertEquals(
            Settings.ACTION_AUTO_ROTATE_SETTINGS,
            systemShortcutIntent(S_AUTO_ROTATE)?.action
        )
        assertEquals(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            systemShortcutIntent(S_ALARMS)?.action
        )
        assertEquals(
            Settings.ACTION_REQUEST_MANAGE_MEDIA,
            systemShortcutIntent(S_MEDIA_MANAGEMENT)?.action
        )
        assertNull(systemShortcutIntent(S_APP_NOTIFICATIONS))
    }

    @Test
    @Config(sdk = [33])
    fun android13_enablesAllAppNotificationSettings() {
        assertEquals(
            Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS,
            systemShortcutIntent(S_APP_NOTIFICATIONS)?.action,
        )
    }

    @Test
    fun unknownSetting_returnsNoIntent() {
        assertNull(systemShortcutIntent("setting_unknown"))
    }
}
