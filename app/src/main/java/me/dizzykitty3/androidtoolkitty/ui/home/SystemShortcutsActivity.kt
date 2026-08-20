package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.S_ABOUT_PHONE
import me.dizzykitty3.androidtoolkitty.S_ACCESSIBILITY
import me.dizzykitty3.androidtoolkitty.S_NFC
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.availableSystemSettings
import me.dizzykitty3.androidtoolkitty.theme.AppTheme
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.LabelAndValueTextRow
import me.dizzykitty3.androidtoolkitty.uicomponents.LabelText
import me.dizzykitty3.androidtoolkitty.uicomponents.Screen
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.SystemSettingButton
import me.dizzykitty3.androidtoolkitty.utils.DateUtil
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen
import me.dizzykitty3.androidtoolkitty.utils.StringUtil

@AndroidEntryPoint
class SystemShortcutsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

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
                        Screen(screenTitle = R.string.system_shortcuts) {
                            SystemShortcutsComposable()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemShortcutsComposable() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val settings = availableSystemSettings().filterNot { it.settingType == S_ABOUT_PHONE }

    val i1 = settings.indexOfFirst { it.settingType == S_NFC } + 1
    val i2 = settings.indexOfFirst { it.settingType == S_ACCESSIBILITY } + 1
    val i3 = settings.count()

    BaseCard(R.string.device_info) {
        Column(Modifier.fillMaxWidth()) {
            LabelAndValueTextRow("manufacturer", StringUtil.manufacturer)
            LabelAndValueTextRow("model", StringUtil.model)
            LabelAndValueTextRow("device", StringUtil.device)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(0.4F)) {
                    LabelText("os_ver")
                }
                Row(Modifier.weight(0.6F)) {
                    Box(Modifier.horizontalScroll(rememberScrollState())) {
                        Text(
                            text = StringUtil.osVer, modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                context.openScreen(AndroidVersionsActivity::class.java)
                            }, color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            LabelAndValueTextRow("locale", StringUtil.sysLocale)
            LabelAndValueTextRow("time_zone", DateUtil.sysTimeZone)
            SystemSettingButton(S_ABOUT_PHONE, R.string.about_phone)
        }
    }
    BaseCard(R.string.general) {
        settings.subList(0, i1).forEach { setting ->
            SystemSettingButton(
                setting.settingType, setting.text
            )
        }
    }
    BaseCard(R.string.permissions) {
        settings.subList(i1, i2).forEach { setting ->
            SystemSettingButton(
                setting.settingType, setting.text
            )
        }
    }
    BaseCard(R.string.debugging) {
        settings.subList(i2, i3).forEach { setting ->
            SystemSettingButton(
                setting.settingType, setting.text
            )
        }
    }

    // edit
    Button(onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        context.openScreen(SystemShortcutsCustomizeActivity::class.java)
    }) { Text(stringResource(R.string.customize_system_settings_card)) }

    SpacerPadding()
}
