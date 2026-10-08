package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.MediaVolume
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseScreen
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.Section
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.effectiveVolumeSlots
import me.dizzykitty3.androidtoolkitty.utils.maxMediaVolumeIndex
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtils.showSnackbar
import me.dizzykitty3.androidtoolkitty.utils.maxVoiceCallVolumeIndex
import me.dizzykitty3.androidtoolkitty.utils.setVoiceCallVolume
import me.dizzykitty3.androidtoolkitty.utils.voiceCallVolume

@AndroidEntryPoint
class VolumeActivity : ComponentActivity() {
    companion object {
        const val EXTRA_UNCONFIGURED_SLOT = "unconfigured_volume_slot"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                BaseScreen(
                    title = R.string.volume,
                    dynamicColor = state.dynamicColor
                ) {
                    val view = LocalView.current
                    val haptic = LocalHapticFeedback.current

                    var unconfiguredSlot by rememberSaveable {
                        mutableIntStateOf(intent.getIntExtra(EXTRA_UNCONFIGURED_SLOT, -1))
                    }
                    val slots = effectiveVolumeSlots(state.customVolumes, view.context.maxMediaVolumeIndex)
                    val showError = unconfiguredSlot in slots.indices && slots[unconfiguredSlot] == null

                    Section(R.string.media_volume) {
                        MediaVolume(onUnconfiguredSlot = { unconfiguredSlot = it })
                    }
                    CardSpacePadding()
                    Section(R.string.edit_custom_volume) {
                        Text(
                            stringResource(R.string.volume_slot_edit_hint),
                            color = if (showError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface,
                        )
                        SpacerPadding()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            slots.forEachIndexed { index, percent ->
                                val description = stringResource(R.string.edit_volume_slot, index + 1)
                                OutlinedButton(
                                    modifier = Modifier.weight(1f).semantics { contentDescription = description },
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        view.context.editVolumeSlot(index)
                                    },
                                ) {
                                    Text(percent?.let { "$it%" } ?: stringResource(R.string.add))
                                }
                            }
                        }
                    }
                    CardSpacePadding()
                    Section(R.string.voice_call_volume) {
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
