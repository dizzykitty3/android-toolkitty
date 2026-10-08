package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SettingsApplications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.BuildConfig
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.SOURCE_CODE_URL
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.preferences.LoggingPreferences
import me.dizzykitty3.androidtoolkitty.ui.home.HomeCardId
import me.dizzykitty3.androidtoolkitty.ui.home.SystemShortcutsCustomizeActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseScreen
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomSwitchRow
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openAppDetailSettings
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openAppLanguageSetting
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openURL
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.versionName
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
                BaseScreen(
                    largeTitle = true,
                    title = R.string.settings,
                    dynamicColor = state.dynamicColor
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        SettingsSection(R.string.appearance) { Appearance() }
                        CardSpacePadding()
                    }
                    SettingsSection(R.string.general) { General() }
                    CardSpacePadding()
                    SettingsSection(R.string.app_info) { OtherSettings() }
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

    CustomSwitchRow(
        icon = Icons.Outlined.ColorLens,
        title = R.string.dynamic_color,
        checked = state.dynamicColor
    ) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        viewModel.toggleDynamicColor(it)
    }

    // change app lang
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        SettingsLinkRow(
            icon = Icons.Outlined.Language,
            title = R.string.language,
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
    val isSearchCardShown = state.isShown(HomeCardId.SEARCH.preferenceKey)
    val isSystemShortcutsCardShown = state.isShown(HomeCardId.SYSTEM_SHORTCUTS.preferenceKey)

    CustomSwitchRow(
        icon = Icons.Outlined.ClearAll,
        title = R.string.clear_clipboard_on_launch,
        checked = state.autoClearClipboard
    ) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        viewModel.toggleAutoClearClipboard(it)
    }

    // edit home
    SettingsLinkRow(
        icon = Icons.Outlined.Edit,
        title = R.string.customize_home,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(CustomizeHomeActivity::class.java)
        }
    )

    if (isSearchCardShown) {
        SettingsLinkRow(
            icon = Icons.Outlined.Search,
            title = R.string.search_settings,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                context.openScreen(SearchSettingsActivity::class.java)
            }
        )
    }

    if (isSystemShortcutsCardShown) {
        SettingsLinkRow(
            icon = Icons.Outlined.Settings,
            title = R.string.customize_system_shortcuts,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                context.openScreen(SystemShortcutsCustomizeActivity::class.java)
            }
        )
    }
}

@Composable
private fun OtherSettings() {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    var isLoggingEnabled by remember { mutableStateOf(LoggingPreferences.isEnabled) }

    SettingsInfoRow(
        icon = Icons.Outlined.Info,
        title = R.string.version,
        text = view.context.versionName,
    )

    val isDebugBuild = BuildConfig.DEBUG
    CustomSwitchRow(
        icon = Icons.AutoMirrored.Outlined.EventNote,
        title = R.string.log_outputs,
        checked = isDebugBuild || isLoggingEnabled,
        enabled = !isDebugBuild,
    ) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (!isDebugBuild) {
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
        title = R.string.view_source_code,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openURL(SOURCE_CODE_URL)
        },
        trailingIcon = Icons.Outlined.ArrowOutward,
        trailingContentDescription = stringResource(R.string.view_source_code)
    )

    SettingsLinkRow(
        icon = Icons.Outlined.FileCopy,
        title = R.string.licenses,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openScreen(LicensesActivity::class.java)
        }
    )

    SettingsLinkRow(
        icon = Icons.Outlined.SettingsApplications,
        title = R.string.app_settings,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openAppDetailSettings()
        }
    )
}
