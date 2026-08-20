package me.dizzykitty3.androidtoolkitty.home

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.SystemShortcutsActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ItalicText
import me.dizzykitty3.androidtoolkitty.uicomponents.SystemSettingButton
import me.dizzykitty3.androidtoolkitty.uicomponents.Tip
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen
import me.dizzykitty3.androidtoolkitty.utils.OSVersion

@Composable
fun SysSettings() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()

    BaseCard(
        title = R.string.system_shortcuts,
        icon = Icons.Outlined.Settings,
        hasShowMore = true,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(SystemShortcutsActivity::class.java)
        }) {
        val settings = mutableListOf(
            Setting(S_ABOUT_PHONE, R.string.about_phone),
            Setting(S_SEARCH_SETTINGS, R.string.search_settings),
            // General
            Setting(S_WIFI, R.string.internet),
            Setting(S_BATTERY, R.string.battery),
            Setting(S_DISPLAY, R.string.display_settings),
            Setting(S_AUTO_ROTATE, R.string.auto_rotate_settings),
            Setting(S_SOUND, R.string.sound),
            Setting(S_BLUETOOTH, R.string.bluetooth_settings),
            Setting(S_DEFAULT_APPS, R.string.default_apps_settings),
            Setting(S_KEYBOARD, R.string.keyboard),
            Setting(S_BATTERY_OPTIMIZATION, R.string.battery_optimization_settings),
            Setting(S_CAPTION, R.string.caption_preferences),
            Setting(S_ACCOUNTS, R.string.accounts),
            Setting(S_VPN, R.string.vpn),
            Setting(S_NFC, R.string.nfc),
            // Permissions
            Setting(S_APP_NOTIFICATIONS, R.string.app_notifications),
            Setting(S_UNKNOWN_APPS, R.string.install_unknown_apps),
            Setting(S_MEDIA_MANAGEMENT, R.string.media_management),
            Setting(S_USAGE_ACCESS, R.string.usage_access_permission),
            Setting(S_OVERLAY, R.string.overlay_permission),
            Setting(S_MODIFY_SYSTEM, R.string.modify_system),
            Setting(S_NOTIFICATION_LISTENER, R.string.device_and_app_notifications),
            Setting(S_DND_ACCESS, R.string.do_not_disturb_access),
            Setting(S_ALARMS, R.string.alarms_n_reminders),
            Setting(S_ACCESSIBILITY, R.string.accessibility_settings),
            // Debugging
            Setting(S_LOCALE, R.string.language_settings),
            Setting(S_DATE, R.string.date_and_time_settings),
            Setting(S_DEVELOPER, R.string.developer_options)
        )
        if (!OSVersion.android13()) {
            settings.remove(Setting(S_APP_NOTIFICATIONS, R.string.app_notifications))
        }
        if (!OSVersion.android12()) {
            settings.remove(Setting(S_AUTO_ROTATE, R.string.auto_rotate_settings))
            settings.remove(Setting(S_ALARMS, R.string.alarms_n_reminders))
            settings.remove(Setting(S_MEDIA_MANAGEMENT, R.string.media_management))
        }
        if (!OSVersion.android10()) {
            settings.remove(Setting(S_SEARCH_SETTINGS, R.string.search_settings))
        }
        if (!OSVersion.android8()) {
            settings.remove(Setting(S_UNKNOWN_APPS, R.string.install_unknown_apps))
        }
        if (!OSVersion.android7()) {
            settings.remove(Setting(S_DEFAULT_APPS, R.string.default_apps_settings))
            settings.remove(Setting(S_VPN, R.string.vpn))
        }
        if (!OSVersion.android6()) {
            settings.remove(Setting(S_BATTERY_OPTIMIZATION, R.string.battery_optimization_settings))
            settings.remove(Setting(S_OVERLAY, R.string.overlay_permission))
            settings.remove(Setting(S_MODIFY_SYSTEM, R.string.modify_system))
        }
        val shownSettings = settings.filter { setting ->
            state.cardShownStates[setting.settingType] ?: true
        }

        if (!context.checkIsAutoTime()) Tip(R.string.auto_set_time_is_off_tip)

        val count = shownSettings.count()
        if (count == 0) {
            Text(buildAnnotatedString { ItalicText(R.string.no_options_enabled) })
        } else if (count <= 2) {
            shownSettings.subList(0, count).forEach { setting ->
                SystemSettingButton(
                    setting.settingType, setting.text
                )
            }
        } else {
            shownSettings.subList(0, 2).forEach { setting ->
                SystemSettingButton(
                    setting.settingType, setting.text
                )
            }
            shownSettings.subList(2, minOf(4, count)).forEach { setting ->
                SystemSettingButton(
                    setting.settingType, setting.text
                )
            }
        }
    }
}

private fun Context.checkIsAutoTime(): Boolean {
    val resolver: ContentResolver = this.contentResolver
    val isAutoTime = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME, 0)
    return isAutoTime == 1
}

data class Setting(val settingType: String, @param:StringRes val text: Int)
