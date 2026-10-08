package me.dizzykitty3.androidtoolkitty.home

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.VolumeActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import me.dizzykitty3.androidtoolkitty.utils.effectiveVolumeSlots
import me.dizzykitty3.androidtoolkitty.utils.maxMediaVolumeIndex
import me.dizzykitty3.androidtoolkitty.utils.mediaVolume
import me.dizzykitty3.androidtoolkitty.utils.setVolume
import me.dizzykitty3.androidtoolkitty.utils.volumeSlotStep

@Composable
fun Volume() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(R.string.volume, Icons.AutoMirrored.Outlined.VolumeUp, true, {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        context.openScreen(VolumeActivity::class.java)
    }) {
        MediaVolume(onUnconfiguredSlot = { slot ->
            context.startActivity(Intent(context, VolumeActivity::class.java).apply {
                putExtra(VolumeActivity.EXTRA_UNCONFIGURED_SLOT, slot)
            })
        })
    }
}

@Composable
fun MediaVolume(onUnconfiguredSlot: (Int) -> Unit) {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val maxVolume = view.context.maxMediaVolumeIndex
    val slots = effectiveVolumeSlots(state.customVolumes, maxVolume)
    val options =
        listOf(stringResource(R.string.off_all_cap)) + slots.map { it?.let { "$it%" } ?: "+" }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(state.customVolumes, state.volumeButtonTapCount, maxVolume) {
        selectedIndex =
            selectedVolumeIndex(view.context.mediaVolume, maxVolume, state.customVolumes)
    }

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(), space = SegmentedButtonDefaults.BorderWidth,
    ) {
        options.forEachIndexed { index, label ->
            val description =
                if (index == 0) label else stringResource(R.string.volume_slot_label, index, label)
            SegmentedButton(
                modifier = Modifier.semantics { contentDescription = description },
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    // Re-read the range at tap time in case the audio route changed.
                    val currentMax = view.context.maxMediaVolumeIndex
                    val currentSlots = effectiveVolumeSlots(state.customVolumes, currentMax)
                    val step =
                        if (index == 0) 0 else volumeSlotStep(currentSlots[index - 1], currentMax)
                    if (step == null) {
                        onUnconfiguredSlot(index - 1)
                    } else {
                        view.setVolume(step)
                        selectedIndex = selectedVolumeIndex(
                            view.context.mediaVolume,
                            currentMax,
                            state.customVolumes
                        )
                        vm.increaseVolumeButtonTapCount()
                    }
                },
                selected = index == selectedIndex,
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                ),
                colors = SegmentedButtonDefaults.colors()
                    .copy(inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) { Text(label) }
        }
    }

}

internal fun selectedVolumeIndex(volume: Int, maxVolume: Int, slots: List<Int?>): Int {
    if (volume == 0) return 0
    if (volume < 0 || maxVolume <= 0 || volume > maxVolume) return -1
    val slot = effectiveVolumeSlots(slots, maxVolume).indexOfFirst {
        volumeSlotStep(
            it,
            maxVolume
        ) == volume
    }
    return if (slot < 0) -1 else slot + 1
}
