package me.dizzykitty3.androidtoolkitty.home

import androidx.annotation.StringRes
import me.dizzykitty3.androidtoolkitty.R
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
import me.dizzykitty3.androidtoolkitty.utils.OSVersion

data class Setting(
    val settingType: String,
    @param:StringRes val text: Int,
    val isAvailable: () -> Boolean = { true },
)

val systemSettingDefinitions = listOf(
    Setting(S_ABOUT_PHONE, R.string.about_phone),
    Setting(S_SEARCH_SETTINGS, R.string.search_settings) { OSVersion.android10() },
    // General
    Setting(S_WIFI, R.string.internet),
    Setting(S_BATTERY, R.string.battery),
    Setting(S_DISPLAY, R.string.display_settings),
    Setting(S_AUTO_ROTATE, R.string.auto_rotate_settings) { OSVersion.android12() },
    Setting(S_SOUND, R.string.sound),
    Setting(S_BLUETOOTH, R.string.bluetooth_settings),
    Setting(S_DEFAULT_APPS, R.string.default_apps_settings) { OSVersion.android7() },
    Setting(S_KEYBOARD, R.string.keyboard),
    Setting(
        S_BATTERY_OPTIMIZATION,
        R.string.battery_optimization_settings,
    ) { OSVersion.android6() },
    Setting(S_CAPTION, R.string.caption_preferences),
    Setting(S_ACCOUNTS, R.string.accounts),
    Setting(S_VPN, R.string.vpn) { OSVersion.android7() },
    Setting(S_NFC, R.string.nfc),
    // Permissions
    Setting(S_APP_NOTIFICATIONS, R.string.app_notifications) { OSVersion.android13() },
    Setting(S_UNKNOWN_APPS, R.string.install_unknown_apps) { OSVersion.android8() },
    Setting(S_MEDIA_MANAGEMENT, R.string.media_management) { OSVersion.android12() },
    Setting(S_USAGE_ACCESS, R.string.usage_access_permission),
    Setting(S_OVERLAY, R.string.overlay_permission) { OSVersion.android6() },
    Setting(S_MODIFY_SYSTEM, R.string.modify_system) { OSVersion.android6() },
    Setting(S_NOTIFICATION_LISTENER, R.string.device_and_app_notifications),
    Setting(S_DND_ACCESS, R.string.do_not_disturb_access) { OSVersion.android6() },
    Setting(S_ALARMS, R.string.alarms_n_reminders) { OSVersion.android12() },
    Setting(S_ACCESSIBILITY, R.string.accessibility_settings),
    // Debugging
    Setting(S_LOCALE, R.string.language_settings),
    Setting(S_DATE, R.string.date_and_time_settings),
    Setting(S_DEVELOPER, R.string.developer_options),
)

fun availableSystemSettings(): List<Setting> =
    systemSettingDefinitions.filter { it.isAvailable() }
