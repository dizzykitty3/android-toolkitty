package me.dizzykitty3.androidtoolkitty.home

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
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
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.SystemShortcutsActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ItalicText
import me.dizzykitty3.androidtoolkitty.uicomponents.SystemSettingButton
import me.dizzykitty3.androidtoolkitty.uicomponents.Tip
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen

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
        val settings = availableSystemSettings()
        val shownSettings = settings.filter { setting ->
            state.cardShownStates[setting.settingType] ?: true
        }

        if (!context.checkIsAutoTime()) Tip(R.string.auto_set_time_is_off_tip)

        if (shownSettings.isEmpty()) {
            Text(buildAnnotatedString { ItalicText(R.string.no_options_enabled) })
        } else {
            shownSettings.take(4).forEach { setting ->
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
