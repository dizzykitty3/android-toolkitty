package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
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
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.theme.AppTheme
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomHideCardSettingSwitch
import me.dizzykitty3.androidtoolkitty.uicomponents.Screen
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.Tip
import me.dizzykitty3.androidtoolkitty.utils.OSVersion

@AndroidEntryPoint
class SystemShortcutsCustomizeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                AppTheme(
                    dynamicColor = state.dynamicColor
                ) {
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ) { innerPadding ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                                )
                        ) {
                            Screen(screenTitle = R.string.customize_system_settings_card) {
                                SystemShortcutsPinOptionsComposable()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemShortcutsPinOptionsComposable() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()

    BaseCard(R.string.customize_system_settings_card) {
        val haptic = LocalHapticFeedback.current

        val settingList = listOf(
            S_ABOUT_PHONE, S_SEARCH_SETTINGS, S_WIFI, S_BATTERY, S_DISPLAY,
            S_AUTO_ROTATE, S_SOUND, S_BLUETOOTH, S_DEFAULT_APPS, S_KEYBOARD,
            S_BATTERY_OPTIMIZATION, S_CAPTION, S_ACCOUNTS, S_VPN, S_NFC,
            S_APP_NOTIFICATIONS, S_UNKNOWN_APPS, S_MEDIA_MANAGEMENT,
            S_USAGE_ACCESS, S_OVERLAY, S_MODIFY_SYSTEM, S_NOTIFICATION_LISTENER,
            S_DND_ACCESS, S_ALARMS, S_ACCESSIBILITY, S_LOCALE, S_DATE, S_DEVELOPER
        )

        val settingTextMap = mapOf(
            S_ABOUT_PHONE to R.string.about_phone,
            S_SEARCH_SETTINGS to R.string.search_settings,
            S_WIFI to R.string.internet,
            S_BATTERY to R.string.battery,
            S_DISPLAY to R.string.display_settings,
            S_AUTO_ROTATE to R.string.auto_rotate_settings,
            S_SOUND to R.string.sound,
            S_BLUETOOTH to R.string.bluetooth_settings,
            S_DEFAULT_APPS to R.string.default_apps_settings,
            S_KEYBOARD to R.string.keyboard,
            S_BATTERY_OPTIMIZATION to R.string.battery_optimization_settings,
            S_CAPTION to R.string.caption_preferences,
            S_ACCOUNTS to R.string.accounts,
            S_VPN to R.string.vpn,
            S_NFC to R.string.nfc,
            S_APP_NOTIFICATIONS to R.string.app_notifications,
            S_UNKNOWN_APPS to R.string.install_unknown_apps,
            S_MEDIA_MANAGEMENT to R.string.media_management,
            S_USAGE_ACCESS to R.string.usage_access_permission,
            S_OVERLAY to R.string.overlay_permission,
            S_MODIFY_SYSTEM to R.string.modify_system,
            S_NOTIFICATION_LISTENER to R.string.device_and_app_notifications,
            S_DND_ACCESS to R.string.do_not_disturb_access,
            S_ALARMS to R.string.alarms_n_reminders,
            S_ACCESSIBILITY to R.string.accessibility_settings,
            S_LOCALE to R.string.language_settings,
            S_DATE to R.string.date_and_time_settings,
            S_DEVELOPER to R.string.developer_options
        )

        Tip(R.string.sys_settings_tip)

        settingList.forEach { setting ->
            val show = when (setting) {
                S_SEARCH_SETTINGS -> OSVersion.android10()
                S_AUTO_ROTATE -> OSVersion.android12()
                S_DEFAULT_APPS -> OSVersion.android7()
                S_BATTERY_OPTIMIZATION -> OSVersion.android6()
                S_VPN -> OSVersion.android7()
                S_APP_NOTIFICATIONS -> OSVersion.android13()
                S_UNKNOWN_APPS -> OSVersion.android8()
                S_MEDIA_MANAGEMENT -> OSVersion.android12()
                S_OVERLAY -> OSVersion.android6()
                S_MODIFY_SYSTEM -> OSVersion.android6()
                S_NOTIFICATION_LISTENER -> OSVersion.android5Point1()
                S_DND_ACCESS -> OSVersion.android6()
                S_ALARMS -> OSVersion.android12()
                else -> true
            }

            if (show) {
                val settingText = settingTextMap[setting] ?: return@forEach
                CustomHideCardSettingSwitch(
                    text = settingText,
                    isChecked = state.cardShownStates[setting] ?: true
                ) { newState ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    vm.saveShownState(setting, newState)
                }
            }
        }

        SpacerPadding()

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                settingList.forEach { setting -> vm.saveShownState(setting, false) }
            }) {
            Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = stringResource(R.string.hide_all_options),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            SpacerPadding()
            Text(stringResource(R.string.hide_all_options))
        }
    }
}
