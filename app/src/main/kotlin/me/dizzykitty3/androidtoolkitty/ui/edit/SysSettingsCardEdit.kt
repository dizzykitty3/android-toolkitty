package me.dizzykitty3.androidtoolkitty.ui.edit

import android.view.HapticFeedbackConstants
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.SETTING_AUTO_ROTATE
import me.dizzykitty3.androidtoolkitty.SETTING_BATTERY_OPTIMIZATION
import me.dizzykitty3.androidtoolkitty.SETTING_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.SETTING_CAPTIONING
import me.dizzykitty3.androidtoolkitty.SETTING_DATE
import me.dizzykitty3.androidtoolkitty.SETTING_DEFAULT_APPS
import me.dizzykitty3.androidtoolkitty.SETTING_DEVELOPER
import me.dizzykitty3.androidtoolkitty.SETTING_DISPLAY
import me.dizzykitty3.androidtoolkitty.SETTING_LOCALE
import me.dizzykitty3.androidtoolkitty.SETTING_OVERLAY
import me.dizzykitty3.androidtoolkitty.SETTING_USAGE_ACCESS
import me.dizzykitty3.androidtoolkitty.SETTING_WRITE_SETTINGS
import me.dizzykitty3.androidtoolkitty.data.sharedpreferences.SettingsSharedPref
import me.dizzykitty3.androidtoolkitty.ui_components.Card
import me.dizzykitty3.androidtoolkitty.ui_components.CustomHideCardSettingSwitch
import me.dizzykitty3.androidtoolkitty.ui_components.GroupDivider
import me.dizzykitty3.androidtoolkitty.ui_components.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.OSVersion

@Composable
fun SysSettingsCardEdit() {
    Card(title = R.string.customize_system_settings_card) {
        val view = LocalView.current
        val settingsSharedPref = remember { SettingsSharedPref }
        var mIsShowSetting1 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_DISPLAY
                )
            )
        }
        var mIsShowSetting2 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_AUTO_ROTATE
                )
            )
        }
        var mIsShowSetting3 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_BLUETOOTH
                )
            )
        }
        var mIsShowSetting4 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_DEFAULT_APPS
                )
            )
        }
        var mIsShowSetting5 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_BATTERY_OPTIMIZATION
                )
            )
        }
        var mIsShowSetting6 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_CAPTIONING
                )
            )
        }
        var mIsShowSetting7 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_USAGE_ACCESS
                )
            )
        }
        var mIsShowSetting8 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_OVERLAY
                )
            )
        }
        var mIsShowSetting9 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_WRITE_SETTINGS
                )
            )
        }
        var mIsShowSetting10 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_LOCALE
                )
            )
        }
        var mIsShowSetting11 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_DATE
                )
            )
        }
        var mIsShowSetting12 by remember {
            mutableStateOf(
                settingsSharedPref.getCardShowedState(
                    SETTING_DEVELOPER
                )
            )
        }

        CustomHideCardSettingSwitch(
            text = R.string.open_display_settings,
            card = SETTING_DISPLAY,
            isChecked = mIsShowSetting1
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting1 = newState
            settingsSharedPref.saveCardShowedState(SETTING_DISPLAY, newState)
        }
        if (OSVersion.a12()) {
            CustomHideCardSettingSwitch(
                text = R.string.open_auto_rotate_settings,
                card = SETTING_AUTO_ROTATE,
                isChecked = mIsShowSetting2
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowSetting2 = newState
                settingsSharedPref.saveCardShowedState(SETTING_AUTO_ROTATE, newState)
            }
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_bluetooth_settings,
            card = SETTING_BLUETOOTH,
            isChecked = mIsShowSetting3
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting3 = newState
            settingsSharedPref.saveCardShowedState(SETTING_BLUETOOTH, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_default_apps_settings,
            card = SETTING_DEFAULT_APPS,
            isChecked = mIsShowSetting4
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting4 = newState
            settingsSharedPref.saveCardShowedState(SETTING_DEFAULT_APPS, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_battery_optimization_settings,
            card = SETTING_BATTERY_OPTIMIZATION,
            isChecked = mIsShowSetting5
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting5 = newState
            settingsSharedPref.saveCardShowedState(SETTING_BATTERY_OPTIMIZATION, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_caption_preferences,
            card = SETTING_CAPTIONING,
            isChecked = mIsShowSetting6
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting6 = newState
            settingsSharedPref.saveCardShowedState(SETTING_CAPTIONING, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_usage_access_permission,
            card = SETTING_USAGE_ACCESS,
            isChecked = mIsShowSetting7
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting7 = newState
            settingsSharedPref.saveCardShowedState(SETTING_USAGE_ACCESS, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_overlay_permission,
            card = SETTING_OVERLAY,
            isChecked = mIsShowSetting8
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting8 = newState
            settingsSharedPref.saveCardShowedState(SETTING_OVERLAY, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_write_permission,
            card = SETTING_WRITE_SETTINGS,
            isChecked = mIsShowSetting9
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting9 = newState
            settingsSharedPref.saveCardShowedState(SETTING_WRITE_SETTINGS, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_language_settings,
            card = SETTING_LOCALE,
            isChecked = mIsShowSetting10
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting10 = newState
            settingsSharedPref.saveCardShowedState(SETTING_LOCALE, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_date_and_time_settings,
            card = SETTING_DATE,
            isChecked = mIsShowSetting11
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting11 = newState
            settingsSharedPref.saveCardShowedState(SETTING_DATE, newState)
        }
        CustomHideCardSettingSwitch(
            text = R.string.open_developer_options,
            card = SETTING_DEVELOPER,
            isChecked = mIsShowSetting12
        ) { newState ->
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            mIsShowSetting12 = newState
            settingsSharedPref.saveCardShowedState(SETTING_DEVELOPER, newState)
        }

        GroupDivider()

        Button(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                onClickChangeAllCardsButton(false)
                mIsShowSetting1 = false
                mIsShowSetting2 = false
                mIsShowSetting3 = false
                mIsShowSetting4 = false
                mIsShowSetting5 = false
                mIsShowSetting6 = false
                mIsShowSetting7 = false
                mIsShowSetting8 = false
                mIsShowSetting9 = false
                mIsShowSetting10 = false
                mIsShowSetting11 = false
                mIsShowSetting12 = false
            },
            elevation = ButtonDefaults.buttonElevation(1.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = stringResource(id = R.string.hide_all_options),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            SpacerPadding()
            Text(text = stringResource(R.string.hide_all_options))
        }

        Button(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                onClickChangeAllCardsButton(true)
                mIsShowSetting1 = true
                mIsShowSetting2 = true
                mIsShowSetting3 = true
                mIsShowSetting4 = true
                mIsShowSetting5 = true
                mIsShowSetting6 = true
                mIsShowSetting7 = true
                mIsShowSetting8 = true
                mIsShowSetting9 = true
                mIsShowSetting10 = true
                mIsShowSetting11 = true
                mIsShowSetting12 = true
            },
            elevation = ButtonDefaults.buttonElevation(1.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = stringResource(id = R.string.show_all_options),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            SpacerPadding()
            Text(text = stringResource(R.string.show_all_options))
        }
    }
}

private fun onClickChangeAllCardsButton(isShow: Boolean) {
    val settingList = listOf(
        SETTING_DISPLAY,
        SETTING_AUTO_ROTATE,
        SETTING_BLUETOOTH,
        SETTING_DEFAULT_APPS,
        SETTING_BATTERY_OPTIMIZATION,
        SETTING_CAPTIONING,
        SETTING_USAGE_ACCESS,
        SETTING_OVERLAY,
        SETTING_WRITE_SETTINGS,
        SETTING_LOCALE,
        SETTING_DATE,
        SETTING_DEVELOPER
    )
    val settingsViewModel = SettingsSharedPref

    for (setting in settingList) {
        settingsViewModel.saveCardShowedState(setting, isShow)
    }
}