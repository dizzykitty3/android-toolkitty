package me.dizzykitty3.androidtoolkitty.ui.view.settings_screen

import android.content.Context
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavHostController
import me.dizzykitty3.androidtoolkitty.BuildConfig
import me.dizzykitty3.androidtoolkitty.CARD_3
import me.dizzykitty3.androidtoolkitty.EDIT_HOME_SCREEN
import me.dizzykitty3.androidtoolkitty.PERMISSION_REQUEST_SCREEN
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.data.sharedpreferences.SettingsSharedPref
import me.dizzykitty3.androidtoolkitty.ui.component.Bold
import me.dizzykitty3.androidtoolkitty.ui.component.CustomCard
import me.dizzykitty3.androidtoolkitty.ui.component.CustomScreen
import me.dizzykitty3.androidtoolkitty.ui.component.CustomSwitchRow
import me.dizzykitty3.androidtoolkitty.ui.component.GroupDivider
import me.dizzykitty3.androidtoolkitty.ui.component.GroupTitle
import me.dizzykitty3.androidtoolkitty.ui.component.IconAndTextPadding
import me.dizzykitty3.androidtoolkitty.ui.component.SpacerPadding
import me.dizzykitty3.androidtoolkitty.ui.component.WarningAlertDialogButton
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil
import me.dizzykitty3.androidtoolkitty.utils.OSVersion
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil
import me.dizzykitty3.androidtoolkitty.utils.ToastUtil
import me.dizzykitty3.androidtoolkitty.utils.URLUtil
import java.util.Locale

@Composable
fun SettingsScreen(navController: NavHostController) {
    CustomScreen {
        val view = LocalView.current
        val debuggingOptions = SettingsSharedPref.debuggingOptions
        var tapCount by remember { mutableIntStateOf(0) }

        CustomCard(titleRes = R.string.settings) {
            AppearanceOptions()
            GroupDivider()
            GeneralOptions()
            GroupDivider()
            CustomizeOptions(navController = navController)
            GroupDivider()
            UserSyncSection()
            GroupDivider()
            RuleUpdateSection()
            @Suppress("KotlinConstantConditions")
            AnimatedVisibility(
                visible = (debuggingOptions || (!debuggingOptions && tapCount >= 5)),
                enter = fadeIn(animationSpec = tween(durationMillis = 2000))
            ) {
                Column {
                    GroupDivider()
                    DebuggingOptions(navController)
                }
            }
        }

        CustomCard(titleRes = R.string.about) {
            GroupTitle(id = R.string.version)
            Row(
                modifier = Modifier.clickable {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    if (!debuggingOptions) {
                        tapCount++
                        when (tapCount) {
                            3 -> SnackbarUtil.snackbar(view, R.string.two_more_times)
                            4 -> SnackbarUtil.snackbar(view, R.string.one_more_time)
                            5 -> {
                                SnackbarUtil.snackbar(view, R.string.now_a_developer)
                                SettingsSharedPref.debuggingOptions = true
                            }
                        }
                    } else {
                        SnackbarUtil.snackbar(view, R.string.already_developer)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = stringResource(id = R.string.version)
                )
                IconAndTextPadding()
                Text(text = "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME}")
            }
            GroupDivider()
            ContributorAndThanksTo()
            GroupDivider()
            SourceAndLicenses()
        }
    }
}

@Composable
private fun AppearanceOptions() {
    val view = LocalView.current
    val settingsSharedPref = remember { SettingsSharedPref }
    var oneHandedMode by remember { mutableStateOf(settingsSharedPref.oneHandedMode) }
    var dynamicColor by remember { mutableStateOf(settingsSharedPref.dynamicColor) }
    var showDivider by remember { mutableStateOf(settingsSharedPref.showDivider) }
    var showEditVolumeOption by remember { mutableStateOf(settingsSharedPref.showEditVolumeOption) }
    val webpageShowMore = settingsSharedPref.enabledWebpageCardShowMore()
    val showWebpageShowMoreOption = remember { webpageShowMore }
    var mWebpageShowMore by remember { mutableStateOf(webpageShowMore) }
    val inversePrimary = MaterialTheme.colorScheme.inversePrimary.toArgb()
    val inverseOnSurface = MaterialTheme.colorScheme.inverseOnSurface.toArgb()

    GroupTitle(id = R.string.appearance)

    if (OSVersion.android12()) {
        CustomSwitchRow(textRes = R.string.material_you_dynamic_color, checked = dynamicColor) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            dynamicColor = it
            onClickDynamicColorButton(view, it, inverseOnSurface, inversePrimary, view.context)
        }
    }

    CustomSwitchRow(textRes = R.string.one_handed_mode, checked = oneHandedMode) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        oneHandedMode = it
        settingsSharedPref.oneHandedMode = it
    }

    CustomSwitchRow(textRes = R.string.show_divider, checked = showDivider) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        showDivider = it
        settingsSharedPref.showDivider = it
    }

    if (settingsSharedPref.addedCustomVolume()) CustomSwitchRow(
        textRes = R.string.show_edit_volume_option,
        checked = showEditVolumeOption
    ) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        showEditVolumeOption = it
        settingsSharedPref.showEditVolumeOption = it
    }

    if (showWebpageShowMoreOption) CustomSwitchRow(
        textRes = R.string.show_full_webpage_card,
        checked = mWebpageShowMore
    ) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        mWebpageShowMore = it
        settingsSharedPref.webpageCardShowMore = it
    }
}

@Composable
private fun GeneralOptions() {
    val view = LocalView.current
    val settingsSharedPref = remember { SettingsSharedPref }
    var autoClearClipboard by remember { mutableStateOf(settingsSharedPref.autoClearClipboard) }
    var showClipboardCard by remember { mutableStateOf(settingsSharedPref.getCardShowedState(CARD_3)) }
    var volumeSlideSteps by remember { mutableStateOf(settingsSharedPref.sliderIncrement5Percent) }
    var collapseKeyboard by remember { mutableStateOf(settingsSharedPref.collapseKeyboard) }
    var showSnackbarToConfirm by remember { mutableStateOf(settingsSharedPref.showSnackbar) }
    val inversePrimary = MaterialTheme.colorScheme.inversePrimary.toArgb()
    val inverseOnSurface = MaterialTheme.colorScheme.inverseOnSurface.toArgb()

    GroupTitle(R.string.general)

    CustomSwitchRow(
        textRes = R.string.clear_clipboard_on_launch,
        checked = autoClearClipboard
    ) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        autoClearClipboard = it
        // Automatically hide Clipboard Card when turning on Clear on Launch feature.
        if (autoClearClipboard && showClipboardCard) {
            showClipboardCard = false
            settingsSharedPref.saveCardShowedState(CARD_3, false)
            SnackbarUtil.snackbar(
                view,
                messageRes = R.string.clipboard_card_hidden,
                buttonTextRes = R.string.undo,
                textColor = inverseOnSurface,
                buttonColor = inversePrimary,
                buttonClickListener = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    settingsSharedPref.saveCardShowedState(CARD_3, true)
                }
            )
        }
        settingsSharedPref.autoClearClipboard = autoClearClipboard
    }

    CustomSwitchRow(textRes = R.string.set_slider_increment_5, checked = volumeSlideSteps) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        volumeSlideSteps = it
        settingsSharedPref.sliderIncrement5Percent = it
    }

    CustomSwitchRow(
        textRes = R.string.collapse_keyboard_when_back_to_app,
        checked = collapseKeyboard
    ) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        collapseKeyboard = it
        settingsSharedPref.collapseKeyboard = it
    }

    CustomSwitchRow(textRes = R.string.show_snackbar_to_confirm, checked = showSnackbarToConfirm) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        showSnackbarToConfirm = it
        settingsSharedPref.showSnackbar = it
    }
}

@Composable
private fun CustomizeOptions(navController: NavHostController) {
    val view = LocalView.current

    GroupTitle(R.string.customize)

    OutlinedButton(
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
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

@Composable
private fun DebuggingOptions(navController: NavHostController) {
    val view = LocalView.current
    val settingsSharedPref = remember { SettingsSharedPref }
    var uiTesting by remember { mutableStateOf(settingsSharedPref.uiTesting) }

    GroupTitle(R.string.debugging)

    Text(text = "Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT}")
    Text(text = "Language =  ${Locale.getDefault()}")

    CustomSwitchRow(textRes = R.string.ui_testing, checked = uiTesting) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        uiTesting = it
        settingsSharedPref.uiTesting = it
    }

    OutlinedButton(onClick = {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        navController.navigate(PERMISSION_REQUEST_SCREEN)
    }) {
        Text(text = stringResource(id = R.string.go_to_permission_request_screen))
    }

    OutlinedButton(onClick = {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        IntentUtil.restartApp(view.context)
    }) {
        Text(text = stringResource(id = R.string.restart_app))
    }

    WarningAlertDialogButton(
        buttonText = stringResource(R.string.erase_all_app_data),
        dialogMessageTitle = stringResource(R.string.warning),
        dialogMessage = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.warning_erase_all_data_1))
                    append(" ")
                    Bold(R.string.warning_erase_all_data_2)
                    append("\n\n")
                    append(stringResource(R.string.warning_erase_all_data_3))
                }
            )
        },
        positiveButtonText = stringResource(R.string.erase_all_data),
        negativeButtonText = null,
        onClickAction = {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            settingsSharedPref.eraseAllData()
            IntentUtil.finishApp(view.context)
        }
    )
}

private fun onClickDynamicColorButton(
    view: View,
    isDynamicColor: Boolean,
    inverseOnSurface: Int,
    inversePrimary: Int,
    context: Context
) {
    val showSnackbarToConfirm = SettingsSharedPref.showSnackbar

    SettingsSharedPref.dynamicColor = isDynamicColor
    if (showSnackbarToConfirm) {
        SnackbarUtil.snackbar(
            view,
            messageRes = R.string.requires_restart_do_it_now,
            buttonTextRes = R.string.restart,
            textColor = inverseOnSurface,
            buttonColor = inversePrimary,
            buttonClickListener = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                IntentUtil.restartApp(context)
            }
        )
    } else {
        IntentUtil.restartApp(context)
    }
}

@Composable
private fun ContributorAndThanksTo() {
    GroupTitle(id = R.string.contributors)
    DeveloperProfileLink("dizzykitty3")
    SpacerPadding()
    DeveloperProfileLink("HongjieCN")
    SpacerPadding()
    GroupTitle(id = R.string.inspired_by)
    ThanksTo("tengusw/share_to_clipboard")
    SpacerPadding()
    ThanksTo("hashemi-hossein/memory-guardian")
}

@Composable
private fun DeveloperProfileLink(name: String) {
    Row {
        val view = LocalView.current

        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = stringResource(id = R.string.developer_profile_link)
        )
        IconAndTextPadding()
        Row(
            modifier = Modifier.clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                IntentUtil.openURL(
                    "${URLUtil.prefixOf(URLUtil.Platform.GITHUB)}$name",
                    view.context
                )
            }
        ) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ThanksTo(link: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val view = LocalView.current
        val sourceCodeURL = "https://github.com/$link"

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .clickable {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    IntentUtil.openURL(sourceCodeURL, view.context)
                }
        ) {
            Text(
                text = link,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SourceAndLicenses() {
    GroupTitle(id = R.string.source_and_licenses)
    GitHubRepoLink()
}

@Composable
private fun GitHubRepoLink() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val view = LocalView.current
        val sourceCodeURL = "https://github.com/dizzykitty3/AndroidToolKitty"

        Icon(
            imageVector = Icons.Outlined.Code,
            contentDescription = stringResource(id = R.string.developer_profile_link)
        )
        IconAndTextPadding()
        Row(
            modifier = Modifier.clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                ToastUtil.toast(R.string.all_help_welcomed)
                IntentUtil.openURL(sourceCodeURL, view.context)
            }
        ) {
            Text(
                text = stringResource(R.string.source_code_on_github),
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = stringResource(id = R.string.developer_profile_link),
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}