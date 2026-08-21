package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.maxMediaVolumeIndex
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar
import kotlin.math.roundToInt

@AndroidEntryPoint
class VolumeCustomizeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                ToolkitScreen(
                    title = R.string.edit_custom_volume,
                    dynamicColor = state.dynamicColor
                ) {
                    VolumeCustomizeComposable()
                }
            }
        }
    }
}

@Composable
private fun VolumeCustomizeComposable() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val activity = LocalActivity.current
    val maxVolume = view.context.maxMediaVolumeIndex
    var morePreciseSlider by remember { mutableStateOf(false) }

    var newCustomVolume by remember {
        if (state.customVolume < 0) {
            mutableFloatStateOf(0f)
        } else {
            mutableFloatStateOf(state.customVolume.toFloat())
        }
    }

    BaseCard(R.string.edit) {
        Slider(
            value = newCustomVolume, onValueChange = {
                if (morePreciseSlider) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                newCustomVolume = it
            }, valueRange = 0f..100f, steps = if (morePreciseSlider) {
                0
            } else {
                9
            }
        )
        Text("${newCustomVolume.roundToInt()}% -> ${(newCustomVolume * 0.01 * maxVolume).roundToInt()}/$maxVolume")

        SpacerPadding()
        SpacerPadding()

        Row(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.weight(1F)) {
                TextButton(onClick = { morePreciseSlider = !morePreciseSlider }) {
                    Text(
                        text = stringResource(R.string.precise_switch),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row {
                Button(
                    {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if ((newCustomVolume * 0.01 * maxVolume).roundToInt() == 0) {
                            if (newCustomVolume.roundToInt() != 0) {
                                view.showSnackbar(R.string.volume_steps_limited)
                            }
                            return@Button
                        } else {
                            vm.updateCustomVolume(newCustomVolume.roundToInt())
                            activity?.finish()
                        }
                    }) { Text(stringResource(R.string.save)) }
            }
        }
    }
}
