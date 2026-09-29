package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.S_ABOUT_PHONE
import me.dizzykitty3.androidtoolkitty.S_ACCESSIBILITY
import me.dizzykitty3.androidtoolkitty.S_NFC
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.SystemShortcut
import me.dizzykitty3.androidtoolkitty.home.availableSystemShortcuts
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.LabelAndValueTextRow
import me.dizzykitty3.androidtoolkitty.uicomponents.LabelText
import me.dizzykitty3.androidtoolkitty.uicomponents.Section
import me.dizzykitty3.androidtoolkitty.uicomponents.SystemShortcutButton
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
import me.dizzykitty3.androidtoolkitty.utils.DateUtils
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import me.dizzykitty3.androidtoolkitty.utils.StringUtils

@AndroidEntryPoint
class SystemShortcutsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            ToolkitScreen(
                title = R.string.system_shortcuts,
                dynamicColor = state.dynamicColor
            ) {
                SystemShortcutsComposable()
            }
        }
    }
}

@Composable
private fun SystemShortcutsComposable() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val shortcuts = availableSystemShortcuts().filterNot { it.shortcutType == S_ABOUT_PHONE }

    val permissionsStartIndex = shortcuts.indexOfFirst { it.shortcutType == S_NFC } + 1
    val debuggingStartIndex = shortcuts.indexOfFirst { it.shortcutType == S_ACCESSIBILITY } + 1
    val shortcutsEndIndex = shortcuts.size

    Section(R.string.device_info) {
        Column(Modifier.fillMaxWidth()) {
            LabelAndValueTextRow(R.string.manufacturer, StringUtils.manufacturer)
            LabelAndValueTextRow(R.string.model, StringUtils.model)
            LabelAndValueTextRow(R.string.device, StringUtils.device)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(0.4F)) {
                    LabelText(R.string.os_version)
                }
                Row(Modifier.weight(0.6F)) {
                    Box(Modifier.horizontalScroll(rememberScrollState())) {
                        Text(
                            text = StringUtils.osVersion, modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                context.openScreen(AndroidVersionsActivity::class.java)
                            }, color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            LabelAndValueTextRow(R.string.locale, StringUtils.sysLocale)
            LabelAndValueTextRow(R.string.time_zone, DateUtils.sysTimeZone)
            SystemShortcutButton(S_ABOUT_PHONE, R.string.about_phone)
        }
    }
    CardSpacePadding()
    SystemShortcutsGroup(
        R.string.general, shortcuts.subList(0, permissionsStartIndex)
    )
    CardSpacePadding()
    SystemShortcutsGroup(
        R.string.permissions, shortcuts.subList(permissionsStartIndex, debuggingStartIndex)
    )
    CardSpacePadding()
    SystemShortcutsGroup(
        R.string.debugging, shortcuts.subList(debuggingStartIndex, shortcutsEndIndex)
    )
}

@Composable
private fun SystemShortcutsGroup(
    @StringRes title: Int,
    shortcuts: List<SystemShortcut>,
) {
    Section(title) {
        shortcuts.forEach { shortcut ->
            SystemShortcutButton(shortcut.shortcutType, shortcut.title)
        }
    }
}
