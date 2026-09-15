package me.dizzykitty3.androidtoolkitty.utils

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.Intent.ACTION_POWER_USAGE_SUMMARY
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

private fun intentIfSupported(isSupported: Boolean, action: String): Intent? =
    if (isSupported) Intent(action) else null

fun systemSettingsIntent(settingType: String): Intent? = when (settingType) {
    S_DISPLAY -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
    S_AUTO_ROTATE -> intentIfSupported(OSVersion.android12(), Settings.ACTION_AUTO_ROTATE_SETTINGS)
    S_BLUETOOTH -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
    S_DEFAULT_APPS -> intentIfSupported(OSVersion.android7(), Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
    S_BATTERY_OPTIMIZATION -> intentIfSupported(OSVersion.android6(), Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    S_CAPTION -> Intent(Settings.ACTION_CAPTIONING_SETTINGS)
    S_USAGE_ACCESS -> Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    S_OVERLAY -> intentIfSupported(OSVersion.android6(), Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    S_MODIFY_SYSTEM -> intentIfSupported(OSVersion.android6(), Settings.ACTION_MANAGE_WRITE_SETTINGS)
    S_LOCALE -> Intent(Settings.ACTION_LOCALE_SETTINGS)
    S_DATE -> Intent(Settings.ACTION_DATE_SETTINGS)
    S_DEVELOPER -> Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
    S_ENABLE_BLUETOOTH -> Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    S_WIFI -> Intent(Settings.ACTION_WIFI_SETTINGS)
    S_BATTERY -> Intent(ACTION_POWER_USAGE_SUMMARY)
    S_ACCESSIBILITY -> Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    S_NOTIFICATION_LISTENER -> intentIfSupported(OSVersion.android5Point1(), Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    S_DND_ACCESS -> intentIfSupported(OSVersion.android6(), Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
    S_UNKNOWN_APPS -> intentIfSupported(OSVersion.android8(), Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
    S_ALARMS -> intentIfSupported(OSVersion.android12(), Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    S_MEDIA_MANAGEMENT -> intentIfSupported(OSVersion.android12(), Settings.ACTION_REQUEST_MANAGE_MEDIA)
    S_APP_NOTIFICATIONS -> intentIfSupported(OSVersion.android13(), Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS)
    S_ACCOUNTS -> Intent(Settings.ACTION_SYNC_SETTINGS)
    S_VPN -> intentIfSupported(OSVersion.android7(), Settings.ACTION_VPN_SETTINGS)
    S_SEARCH_SETTINGS -> intentIfSupported(OSVersion.android10(), Settings.ACTION_APP_SEARCH_SETTINGS)
    S_SOUND -> Intent(Settings.ACTION_SOUND_SETTINGS)
    S_ABOUT_PHONE -> Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)
    S_KEYBOARD -> Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
    S_NFC -> Intent(Settings.ACTION_NFC_SETTINGS)
    else -> null
}
