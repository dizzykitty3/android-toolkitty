package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseScreen
import me.dizzykitty3.androidtoolkitty.uicomponents.Section
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.CUSTOM_VOLUME_SLOT_COUNT
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtils.showSnackbar
import me.dizzykitty3.androidtoolkitty.utils.VolumeSaveResult
import me.dizzykitty3.androidtoolkitty.utils.maxMediaVolumeIndex
import me.dizzykitty3.androidtoolkitty.utils.validateVolumeSlot
import kotlin.math.roundToInt

private const val MAX_VOLUME_PERCENT = 100f
private const val COARSE_SLIDER_STEPS = 9
internal const val EXTRA_VOLUME_SLOT = "volume_slot"

internal fun Context.editVolumeSlot(slot: Int) {
    if (slot !in 0 until CUSTOM_VOLUME_SLOT_COUNT) return
    startActivity(Intent(this, VolumeCustomizeActivity::class.java).apply {
        putExtra(EXTRA_VOLUME_SLOT, slot)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

@AndroidEntryPoint
class VolumeCustomizeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val slot = intent.getIntExtra(EXTRA_VOLUME_SLOT, -1)
        if (slot !in 0 until CUSTOM_VOLUME_SLOT_COUNT) {
            finish()
            return
        }
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                BaseScreen(
                    title = R.string.edit_custom_volume,
                    dynamicColor = state.dynamicColor
                ) {
                    VolumeCustomizeComposable(slot)
                }
            }
        }
    }
}

@Composable
private fun VolumeCustomizeComposable(slot: Int) {
    val vm = LocalSettingsViewModel.current
    val loadedState by vm.persistedSettings.collectAsStateWithLifecycle(initialValue = null)
    val state = loadedState
    if (state == null) {
        Text(stringResource(R.string.loading_volume_slots))
        return
    }
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val activity = LocalActivity.current
    val maxVolume = view.context.maxMediaVolumeIndex
    var morePreciseSlider by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    var newCustomVolume by rememberSaveable(slot) {
        mutableFloatStateOf(state.customVolumes.getOrNull(slot)?.toFloat() ?: 0f)
    }
    val savedPercent = newCustomVolume.roundToInt()
    val validation = validateVolumeSlot(slot, savedPercent, maxVolume, state.customVolumes)
    val sliderSteps = if (morePreciseSlider) 0 else COARSE_SLIDER_STEPS
    val sliderState = remember(sliderSteps) {
        SliderState(
            value = newCustomVolume,
            steps = sliderSteps,
            trackRange = 0f..MAX_VOLUME_PERCENT,
        )
    }
    sliderState.value = newCustomVolume

    Section {
        Text(stringResource(R.string.edit_volume_slot, slot + 1))
        Slider(
            enabled = !saving && maxVolume > 0,
            state = sliderState,
            onValueChange = {
                if (morePreciseSlider) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                if (it.isFinite()) newCustomVolume = it.coerceIn(0f, MAX_VOLUME_PERCENT)
            },
        )
        // Preview the same integer percentage that will be persisted and applied.
        Text(customVolumeLabel(savedPercent.toFloat(), maxVolume))
        if (validation != VolumeSaveResult.SAVED) {
            Text(
                stringResource(
                    if (validation == VolumeSaveResult.DUPLICATE)
                        R.string.duplicate_volume_slot else R.string.invalid_volume_slot
                )
            )
        }

        SpacerPadding()
        SpacerPadding()

        Row(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.weight(1F)) {
                TextButton(
                    enabled = !saving,
                    onClick = { morePreciseSlider = !morePreciseSlider }) {
                    Text(
                        text = stringResource(if (morePreciseSlider) R.string.precise_on else R.string.precise_off),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row {
                Button(
                    enabled = !saving && validation == VolumeSaveResult.SAVED,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        saving = true
                        vm.updateCustomVolume(
                            slot,
                            savedPercent,
                            view.context.maxMediaVolumeIndex
                        ) { result ->
                            saving = false
                            if (result == VolumeSaveResult.SAVED) activity?.finish()
                            else view.showSnackbar(
                                when (result) {
                                    VolumeSaveResult.DUPLICATE -> R.string.duplicate_volume_slot
                                    VolumeSaveResult.FAILED -> R.string.volume_save_failed
                                    else -> R.string.invalid_volume_slot
                                }
                            )
                        }
                    }) { Text(stringResource(R.string.save)) }
            }
        }
    }
}

internal fun customVolumeIndex(percent: Float, maxVolume: Int): Int =
    if (!percent.isFinite() || percent !in 0f..100f || maxVolume <= 0) 0
    else (percent.toDouble() / 100.0 * maxVolume).roundToInt()

internal fun customVolumeLabel(percent: Float, maxVolume: Int): String =
    "${percent.roundToInt()}% -> ${customVolumeIndex(percent, maxVolume)}/$maxVolume"
