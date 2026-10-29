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

enum class SystemShortcutCategory(@param:StringRes val title: Int) {
    GENERAL(R.string.general),
    CONNECTIONS(R.string.network_connections),
    LANGUAGE_TIME(R.string.language_time),
    ACCESSIBILITY(R.string.assistive_features),
    PERMISSIONS(R.string.permissions_special_access),
    DEVELOPER(R.string.developer_tools),
}

data class SystemShortcut(
    val shortcutType: String,
    @param:StringRes val title: Int,
    val category: SystemShortcutCategory = SystemShortcutCategory.GENERAL,
    val isAvailable: () -> Boolean = { true },
)

val systemShortcutDefinitions = listOf(
    SystemShortcut(S_ABOUT_PHONE, R.string.about_phone),
    SystemShortcut(
        S_SEARCH_SETTINGS,
        R.string.search_settings
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q },
    // General
    SystemShortcut(S_WIFI, R.string.internet, SystemShortcutCategory.CONNECTIONS),
    SystemShortcut(S_BATTERY, R.string.battery),
    SystemShortcut(S_DISPLAY, R.string.display_settings),
    SystemShortcut(
        S_AUTO_ROTATE,
        R.string.auto_rotate_settings
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
    SystemShortcut(S_SOUND, R.string.sound),
    SystemShortcut(S_BLUETOOTH, R.string.bluetooth_settings, SystemShortcutCategory.CONNECTIONS),
    SystemShortcut(S_DEFAULT_APPS, R.string.default_apps_settings),
    SystemShortcut(S_KEYBOARD, R.string.keyboard),
    SystemShortcut(S_BATTERY_OPTIMIZATION, R.string.battery_optimization_settings, SystemShortcutCategory.PERMISSIONS),
    SystemShortcut(S_CAPTION, R.string.caption_preferences, SystemShortcutCategory.ACCESSIBILITY),
    SystemShortcut(S_ACCOUNTS, R.string.accounts),
    SystemShortcut(S_VPN, R.string.vpn, SystemShortcutCategory.CONNECTIONS),
    SystemShortcut(S_NFC, R.string.nfc, SystemShortcutCategory.CONNECTIONS),
    SystemShortcut(
        S_APP_NOTIFICATIONS,
        R.string.app_notifications, SystemShortcutCategory.PERMISSIONS
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU },
    SystemShortcut(
        S_UNKNOWN_APPS,
        R.string.install_unknown_apps, SystemShortcutCategory.PERMISSIONS
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.O },
    SystemShortcut(
        S_MEDIA_MANAGEMENT,
        R.string.media_management, SystemShortcutCategory.PERMISSIONS
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
    SystemShortcut(S_USAGE_ACCESS, R.string.usage_access_permission, SystemShortcutCategory.PERMISSIONS),
    SystemShortcut(S_OVERLAY, R.string.overlay_permission, SystemShortcutCategory.PERMISSIONS),
    SystemShortcut(S_MODIFY_SYSTEM, R.string.modify_system, SystemShortcutCategory.PERMISSIONS),
    SystemShortcut(S_NOTIFICATION_LISTENER, R.string.device_and_app_notifications, SystemShortcutCategory.PERMISSIONS),
    SystemShortcut(S_DND_ACCESS, R.string.do_not_disturb_access, SystemShortcutCategory.PERMISSIONS),
    SystemShortcut(
        S_ALARMS,
        R.string.alarms_n_reminders, SystemShortcutCategory.PERMISSIONS
    ) { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
    SystemShortcut(S_ACCESSIBILITY, R.string.accessibility_settings, SystemShortcutCategory.ACCESSIBILITY),
    SystemShortcut(S_LOCALE, R.string.language_settings, SystemShortcutCategory.LANGUAGE_TIME),
    SystemShortcut(S_DATE, R.string.date_and_time_settings, SystemShortcutCategory.LANGUAGE_TIME),
    SystemShortcut(S_DEVELOPER, R.string.developer_options, SystemShortcutCategory.DEVELOPER),
).sortedBy { it.category.ordinal }

fun availableSystemShortcuts(): List<SystemShortcut> =
    systemShortcutDefinitions.filter { it.isAvailable() }
