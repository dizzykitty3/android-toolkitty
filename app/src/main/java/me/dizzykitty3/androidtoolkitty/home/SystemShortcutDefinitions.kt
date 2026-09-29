package me.dizzykitty3.androidtoolkitty.home

import android.os.Build
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

data class SystemShortcut(
    val shortcutType: String,
    @param:StringRes val title: Int,
    val isAvailable: () -> Boolean = { true },
)

val systemShortcutDefinitions = listOf(
    SystemShortcut(S_ABOUT_PHONE, R.string.about_phone),
    SystemShortcut(
        S_SEARCH_SETTINGS,
        R.string.search_settings
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q },
    // General
    SystemShortcut(S_WIFI, R.string.internet),
    SystemShortcut(S_BATTERY, R.string.battery),
    SystemShortcut(S_DISPLAY, R.string.display_settings),
    SystemShortcut(
        S_AUTO_ROTATE,
        R.string.auto_rotate_settings
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
    SystemShortcut(S_SOUND, R.string.sound),
    SystemShortcut(S_BLUETOOTH, R.string.bluetooth_settings),
    SystemShortcut(S_DEFAULT_APPS, R.string.default_apps_settings),
    SystemShortcut(S_KEYBOARD, R.string.keyboard),
    SystemShortcut(S_BATTERY_OPTIMIZATION, R.string.battery_optimization_settings),
    SystemShortcut(S_CAPTION, R.string.caption_preferences),
    SystemShortcut(S_ACCOUNTS, R.string.accounts),
    SystemShortcut(S_VPN, R.string.vpn),
    SystemShortcut(S_NFC, R.string.nfc),
    // Permissions
    SystemShortcut(
        S_APP_NOTIFICATIONS,
        R.string.app_notifications
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU },
    SystemShortcut(
        S_UNKNOWN_APPS,
        R.string.install_unknown_apps
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.O },
    SystemShortcut(
        S_MEDIA_MANAGEMENT,
        R.string.media_management
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
    SystemShortcut(S_USAGE_ACCESS, R.string.usage_access_permission),
    SystemShortcut(S_OVERLAY, R.string.overlay_permission),
    SystemShortcut(S_MODIFY_SYSTEM, R.string.modify_system),
    SystemShortcut(S_NOTIFICATION_LISTENER, R.string.device_and_app_notifications),
    SystemShortcut(S_DND_ACCESS, R.string.do_not_disturb_access),
    SystemShortcut(
        S_ALARMS,
        R.string.alarms_n_reminders
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
    SystemShortcut(S_ACCESSIBILITY, R.string.accessibility_settings),
    // Debugging
    SystemShortcut(S_LOCALE, R.string.language_settings),
    SystemShortcut(S_DATE, R.string.date_and_time_settings),
    SystemShortcut(S_DEVELOPER, R.string.developer_options),
)

fun availableSystemShortcuts(): List<SystemShortcut> =
    systemShortcutDefinitions.filter { it.isAvailable() }
