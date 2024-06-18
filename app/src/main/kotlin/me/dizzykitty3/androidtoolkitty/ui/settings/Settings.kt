package me.dizzykitty3.androidtoolkitty.ui.settings

import android.view.HapticFeedbackConstants
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import me.dizzykitty3.androidtoolkitty.CARD_3
import me.dizzykitty3.androidtoolkitty.EDIT_HOME_SCREEN
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.data.sharedpreferences.SettingsSharedPref
import me.dizzykitty3.androidtoolkitty.ui_components.Card
import me.dizzykitty3.androidtoolkitty.ui_components.CustomScreen
import me.dizzykitty3.androidtoolkitty.ui_components.CustomSwitchRow
import me.dizzykitty3.androidtoolkitty.ui_components.GroupDivider
import me.dizzykitty3.androidtoolkitty.ui_components.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil
import me.dizzykitty3.androidtoolkitty.utils.OSVersion
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil

@Composable
fun Settings(navController: NavHostController) {
    CustomScreen {
        Appearance()
        General(navController)
        Card(title = R.string.online_features) {
            var showPrivacyDisclaimer by remember { mutableStateOf(SettingsSharedPref.showPrivacyDisclaimer) }
            if (showPrivacyDisclaimer) {
                Text(stringResource(R.string.service_provider_privacy_disclaimer))
                SpacerPadding()
                Text("These features are developed and maintained by yanqishui.work")
                TextButton({
                    showPrivacyDisclaimer = false
                    SettingsSharedPref.showPrivacyDisclaimer = false
                }) { Text("Accept") }
            }
            if (!showPrivacyDisclaimer) {
                PreferencesSync()
                GroupDivider()
                RulesUpdate()
            }
        }
        AboutAndDebugging(navController)
    }
}

@Composable
private fun Appearance() {
    val view = LocalView.current
    val settingsSharedPref = remember { SettingsSharedPref }
    var oneHandedMode by remember { mutableStateOf(settingsSharedPref.oneHandedMode) }
    var dynamicColor by remember { mutableStateOf(settingsSharedPref.dynamicColor) }
    var systemVolumeUI by remember { mutableStateOf(settingsSharedPref.showSystemVolumeUI) }

    Card(title = R.string.appearance) {
        if (OSVersion.android12()) {
            CustomSwitchRow(text = R.string.material_you_dynamic_color, checked = dynamicColor) {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                dynamicColor = it
                SettingsSharedPref.dynamicColor = it
                IntentUtil.restartApp(view.context)
            }
        }

        CustomSwitchRow(text = R.string.one_handed_mode, checked = oneHandedMode) {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            oneHandedMode = it
            settingsSharedPref.oneHandedMode = it
        }

        CustomSwitchRow(text = R.string.show_system_volume_ui, checked = systemVolumeUI) {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            systemVolumeUI = it
            settingsSharedPref.showSystemVolumeUI = it
        }
    }
}

@Composable
private fun General(navController: NavHostController) {
    val view = LocalView.current
    val settingsSharedPref = remember { SettingsSharedPref }
    var autoClearClipboard by remember { mutableStateOf(settingsSharedPref.autoClearClipboard) }
    var showClipboardCard by remember { mutableStateOf(settingsSharedPref.getCardShowedState(CARD_3)) }
    val inversePrimary = MaterialTheme.colorScheme.inversePrimary.toArgb()
    val inverseOnSurface = MaterialTheme.colorScheme.inverseOnSurface.toArgb()
    val haveTappedWebpageCardShowMore = settingsSharedPref.haveTappedWebpageCardShowMore
    var webpageShowMore by remember { mutableStateOf(settingsSharedPref.keepWebpageCardShowMore) }

    Card(title = R.string.general) {
        CustomSwitchRow(
            text = R.string.clear_clipboard_on_launch,
            checked = autoClearClipboard
        ) {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            autoClearClipboard = it
            // Automatically hide Clipboard Card when turning on Clear on Launch feature.
            if (autoClearClipboard && showClipboardCard) {
                showClipboardCard = false
                settingsSharedPref.saveCardShowedState(CARD_3, false)
                SnackbarUtil.show(
                    view,
                    message = R.string.clipboard_card_hidden,
                    buttonText = R.string.undo,
                    textColor = inverseOnSurface,
                    buttonColor = inversePrimary,
                    buttonClickListener = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        settingsSharedPref.saveCardShowedState(CARD_3, true)
                    }
                )
            }
            settingsSharedPref.autoClearClipboard = autoClearClipboard
        }

        if (haveTappedWebpageCardShowMore) CustomSwitchRow(
            text = R.string.keep_showing_full_webpage_card,
            checked = webpageShowMore
        ) {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            webpageShowMore = it
            settingsSharedPref.keepWebpageCardShowMore = it
        }

        OutlinedButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                navController.navigate(EDIT_HOME_SCREEN)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(id = R.string.customize_my_home_page),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            SpacerPadding()
            Text(text = stringResource(R.string.customize_my_home_page))
        }
    }
}