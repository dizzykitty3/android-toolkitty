package me.dizzykitty3.androidtoolkitty.utils

import android.bluetooth.BluetoothAdapter
import android.provider.Settings
import me.dizzykitty3.androidtoolkitty.S_ACCESSIBILITY
import me.dizzykitty3.androidtoolkitty.S_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.S_CAPTION
import me.dizzykitty3.androidtoolkitty.S_DATE
import me.dizzykitty3.androidtoolkitty.S_DISPLAY
import me.dizzykitty3.androidtoolkitty.S_ENABLE_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.S_KEYBOARD
import me.dizzykitty3.androidtoolkitty.S_LOCALE
import me.dizzykitty3.androidtoolkitty.S_SOUND
import me.dizzykitty3.androidtoolkitty.S_USAGE_ACCESS
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
            BluetoothAdapter.ACTION_REQUEST_ENABLE,
            systemSettingsIntent(S_ENABLE_BLUETOOTH)?.action,
        )
    }

    @Test
    fun unknownSetting_returnsNoIntent() {
        assertNull(systemSettingsIntent("setting_unknown"))
    }
}
