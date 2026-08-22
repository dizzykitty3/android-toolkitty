package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SettingsApplications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.BuildConfig
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.SOURCE_CODE_URL
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.HomeCardId
import me.dizzykitty3.androidtoolkitty.preferences.LoggingPreferences
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomSwitchRow
import me.dizzykitty3.androidtoolkitty.uicomponents.IconAndTextPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openAppDetailSettings
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openAppLanguageSetting
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openURL
import me.dizzykitty3.androidtoolkitty.utils.OSVersion
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.versionName
import timber.log.Timber

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                ToolkitScreen(
                    title = R.string.settings,
                    dynamicColor = state.dynamicColor
                ) {
                    if (OSVersion.android12()) {
                        BaseCard(R.string.appearance) { Appearance() }
                    }
                    BaseCard(R.string.general) { General() }
                    BaseCard(R.string.app_info) { OtherSettings() }
                }
            }
        }
    }
}

@Composable
private fun Appearance() {
    val viewModel = LocalSettingsViewModel.current
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    if (OSVersion.android12()) {
        CustomSwitchRow(
            icon = Icons.Outlined.ColorLens,
            title = R.string.dynamic_color,
            checked = state.dynamicColor
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.toggleDynamicColor(it)
        }
    }

    // change app lang
    if (OSVersion.android13()) {
        SettingsLinkRow(
            icon = Icons.Outlined.Language,
            title = stringResource(R.string.language),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                view.context.openAppLanguageSetting()
            }
        )
    }
}

@Composable
private fun General() {
    val context = LocalContext.current
    val viewModel = LocalSettingsViewModel.current
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val showSearchCard = state.cardShownStates[HomeCardId.SEARCH.key] ?: true

    CustomSwitchRow(
        icon = Icons.Outlined.ClearAll,
        title = R.string.clear_clipboard_on_launch,
        checked = state.autoClearClipboard
    ) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        viewModel.toggleAutoClearClipboard(it)
    }

    // Switch to Bing Search
    if (showSearchCard) {
        CustomSwitchRow(
            icon = Icons.Outlined.Search,
            title = R.string.switch_to_bing_search,
            checked = state.switchToBingSearch
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.toggleSwitchToBingSearch(it)
        }
    }

    // edit home
    SettingsLinkRow(
        icon = Icons.Outlined.Edit,
        title = stringResource(R.string.customize_home),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(CustomizeHomeActivity::class.java)
        }
    )
}

@Composable
private fun OtherSettings() {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    var isLoggingEnabled by remember { mutableStateOf(LoggingPreferences.isEnabled) }

    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
        color = MaterialTheme.colorScheme.surfaceBright
    ) {
        Column(
            Modifier
                .requiredHeightIn(min = dimensionResource(R.dimen.height_setting_row))
                .fillMaxWidth(), verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1F), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info, contentDescription = null
                    )
                    IconAndTextPadding()
                    Text(view.context.versionName)
                }
            }
        }
    }

    if (BuildConfig.DEBUG) {
        CustomSwitchRow(
            icon = Icons.AutoMirrored.Outlined.EventNote,
            title = R.string.log_outputs,
            checked = true,
            enabled = false,
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    } else {
        CustomSwitchRow(
            icon = Icons.AutoMirrored.Outlined.EventNote,
            title = R.string.log_outputs,
            checked = isLoggingEnabled
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            isLoggingEnabled = it
            LoggingPreferences.isEnabled = it
            if (it) {
                Timber.plant(Timber.DebugTree())
            } else {
                Timber.uprootAll()
            }
        }
    }

    SettingsLinkRow(
        icon = Icons.Outlined.Code,
        title = stringResource(R.string.view_source_code),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openURL(SOURCE_CODE_URL)
        },
        trailingIcon = Icons.Outlined.ArrowOutward,
        trailingContentDescription = stringResource(R.string.view_source_code)
    )

    SettingsLinkRow(
        icon = Icons.Outlined.FileCopy,
        title = stringResource(R.string.licenses),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openScreen(LicensesActivity::class.java)
        }
    )

    SettingsLinkRow(
        icon = Icons.Outlined.SettingsApplications,
        title = stringResource(R.string.app_settings),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openAppDetailSettings()
        }
    )
}
