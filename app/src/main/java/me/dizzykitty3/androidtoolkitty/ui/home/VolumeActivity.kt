package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.MediaVolume
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
import me.dizzykitty3.androidtoolkitty.utils.maxVoiceCallVolumeIndex
import me.dizzykitty3.androidtoolkitty.utils.setVoiceCallVolume
import me.dizzykitty3.androidtoolkitty.utils.voiceCallVolume
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar

@AndroidEntryPoint
class VolumeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                ToolkitScreen(
                    title = R.string.volume,
                    dynamicColor = state.dynamicColor
                ) {
                    val view = LocalView.current
                    val haptic = LocalHapticFeedback.current

                    BaseCard(R.string.media_volume) { MediaVolume(isHome = false) }
                    BaseCard(R.string.voice_call_volume) {
                        OutlinedButton({
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val index = view.context.maxVoiceCallVolumeIndex
                            view.context.setVoiceCallVolume(index)
                            if (view.context.voiceCallVolume == index) {
                                view.showSnackbar(R.string.volume_changed)
                            }
                        }) { Text(stringResource(R.string.max_out_voice_call_volume)) }
                    }
                }
            }
        }
    }
}
