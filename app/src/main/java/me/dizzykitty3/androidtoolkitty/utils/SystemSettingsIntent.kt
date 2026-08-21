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

fun systemSettingsIntent(settingType: String): Intent? = when (settingType) {
    S_DISPLAY -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
    S_AUTO_ROTATE -> if (OSVersion.android12()) Intent(Settings.ACTION_AUTO_ROTATE_SETTINGS) else null
    S_BLUETOOTH -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
    S_DEFAULT_APPS -> if (OSVersion.android7()) Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS) else null
    S_BATTERY_OPTIMIZATION -> if (OSVersion.android6()) Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS) else null
    S_CAPTION -> Intent(Settings.ACTION_CAPTIONING_SETTINGS)
    S_USAGE_ACCESS -> Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    S_OVERLAY -> if (OSVersion.android6()) Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) else null
    S_MODIFY_SYSTEM -> if (OSVersion.android6()) Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS) else null
    S_LOCALE -> Intent(Settings.ACTION_LOCALE_SETTINGS)
    S_DATE -> Intent(Settings.ACTION_DATE_SETTINGS)
    S_DEVELOPER -> Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
    S_ENABLE_BLUETOOTH -> Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    S_WIFI -> Intent(Settings.ACTION_WIFI_SETTINGS)
    S_BATTERY -> Intent(ACTION_POWER_USAGE_SUMMARY)
    S_ACCESSIBILITY -> Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    S_NOTIFICATION_LISTENER -> if (OSVersion.android5Point1()) Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS) else null
    S_DND_ACCESS -> if (OSVersion.android6()) Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS) else null
    S_UNKNOWN_APPS -> if (OSVersion.android8()) Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES) else null
    S_ALARMS -> if (OSVersion.android12()) Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM) else null
    S_MEDIA_MANAGEMENT -> if (OSVersion.android12()) Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA) else null
    S_APP_NOTIFICATIONS -> if (OSVersion.android13()) Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS) else null
    S_ACCOUNTS -> Intent(Settings.ACTION_SYNC_SETTINGS)
    S_VPN -> if (OSVersion.android7()) Intent(Settings.ACTION_VPN_SETTINGS) else null
    S_SEARCH_SETTINGS -> if (OSVersion.android10()) Intent(Settings.ACTION_APP_SEARCH_SETTINGS) else null
    S_SOUND -> Intent(Settings.ACTION_SOUND_SETTINGS)
    S_ABOUT_PHONE -> Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)
    S_KEYBOARD -> Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
    S_NFC -> Intent(Settings.ACTION_NFC_SETTINGS)
    else -> null
}
