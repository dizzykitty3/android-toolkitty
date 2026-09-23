package me.dizzykitty3.androidtoolkitty.utils

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import me.dizzykitty3.androidtoolkitty.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SystemSettingsIntentTest {

    @Test
    fun alwaysSupportedSettings_returnTheirExpectedIntentActions() {
        assertEquals(Settings.ACTION_DEVICE_INFO_SETTINGS, systemSettingsIntent(S_ABOUT_PHONE)?.action)
        assertEquals(Settings.ACTION_DISPLAY_SETTINGS, systemSettingsIntent(S_DISPLAY)?.action)
        assertEquals(Settings.ACTION_BLUETOOTH_SETTINGS, systemSettingsIntent(S_BLUETOOTH)?.action)
        assertEquals(Settings.ACTION_CAPTIONING_SETTINGS, systemSettingsIntent(S_CAPTION)?.action)
        assertEquals(Settings.ACTION_USAGE_ACCESS_SETTINGS, systemSettingsIntent(S_USAGE_ACCESS)?.action)
        assertEquals(Settings.ACTION_LOCALE_SETTINGS, systemSettingsIntent(S_LOCALE)?.action)
        assertEquals(Settings.ACTION_DATE_SETTINGS, systemSettingsIntent(S_DATE)?.action)
        assertEquals(Settings.ACTION_SOUND_SETTINGS, systemSettingsIntent(S_SOUND)?.action)
        assertEquals(Settings.ACTION_ACCESSIBILITY_SETTINGS, systemSettingsIntent(S_ACCESSIBILITY)?.action)
        assertEquals(Settings.ACTION_INPUT_METHOD_SETTINGS, systemSettingsIntent(S_KEYBOARD)?.action)
        assertEquals(
            Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS,
            systemSettingsIntent(S_DEVELOPER)?.action,
        )
        assertEquals(Settings.ACTION_WIFI_SETTINGS, systemSettingsIntent(S_WIFI)?.action)
        assertEquals(Intent.ACTION_POWER_USAGE_SUMMARY, systemSettingsIntent(S_BATTERY)?.action)
        assertEquals(Settings.ACTION_SYNC_SETTINGS, systemSettingsIntent(S_ACCOUNTS)?.action)
        assertEquals(Settings.ACTION_NFC_SETTINGS, systemSettingsIntent(S_NFC)?.action)
        assertEquals(
            BluetoothAdapter.ACTION_REQUEST_ENABLE,
            systemSettingsIntent(S_ENABLE_BLUETOOTH)?.action,
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

        expectedActions.forEach { (settingType, action) ->
            assertEquals(action, systemSettingsIntent(settingType)?.action)
        }
    }

    @Test
    @Config(sdk = [30])
    fun android11_omitsSettingsIntroducedInAndroid12And13() {
        listOf(S_AUTO_ROTATE, S_ALARMS, S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS).forEach {
            assertNull("Unsupported setting: $it", systemSettingsIntent(it))
        }
        assertEquals(Settings.ACTION_APP_SEARCH_SETTINGS, systemSettingsIntent(S_SEARCH_SETTINGS)?.action)
    }

    @Test
    @Config(sdk = [31])
    fun android12_enablesItsSettingsButNotAndroid13Notifications() {
        assertEquals(Settings.ACTION_AUTO_ROTATE_SETTINGS, systemSettingsIntent(S_AUTO_ROTATE)?.action)
        assertEquals(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, systemSettingsIntent(S_ALARMS)?.action)
        assertEquals(Settings.ACTION_REQUEST_MANAGE_MEDIA, systemSettingsIntent(S_MEDIA_MANAGEMENT)?.action)
        assertNull(systemSettingsIntent(S_APP_NOTIFICATIONS))
    }

    @Test
    @Config(sdk = [33])
    fun android13_enablesAllAppNotificationSettings() {
        assertEquals(
            Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS,
            systemSettingsIntent(S_APP_NOTIFICATIONS)?.action,
        )
    }

    @Test
    fun unknownSetting_returnsNoIntent() {
        assertNull(systemSettingsIntent("setting_unknown"))
    }
}
