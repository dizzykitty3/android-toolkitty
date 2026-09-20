package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.VolumeActivity
import me.dizzykitty3.androidtoolkitty.ui.home.VolumeCustomizeActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.GradientSmall
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.maxMediaVolumeIndex
import me.dizzykitty3.androidtoolkitty.utils.PERCENT_TO_VOLUME_RATIO
import me.dizzykitty3.androidtoolkitty.utils.mediaVolume
import me.dizzykitty3.androidtoolkitty.utils.setVolume
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import timber.log.Timber
import kotlin.math.roundToInt

private const val OFF_VOLUME_INDEX = 0
private const val FORTY_PERCENT_VOLUME_INDEX = 1
private const val SIXTY_PERCENT_VOLUME_INDEX = 2
private const val CUSTOM_VOLUME_INDEX = 3
private const val FORTY_PERCENT = 0.4
private const val SIXTY_PERCENT = 0.6
private const val NO_SELECTED_VOLUME_INDEX = -1

@Composable
fun Volume() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(
        R.string.volume, Icons.AutoMirrored.Outlined.VolumeUp, true, {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(VolumeActivity::class.java)
        }) {
        MediaVolume(isHome = true)
    }
}

@Composable
fun MediaVolume(isHome: Boolean) {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val maxVolume = view.context.maxMediaVolumeIndex
    val offAllCap = stringResource(R.string.off_all_cap)
    val addLabel = stringResource(R.string.add)
    val fortyPercentLabel = stringResource(R.string.volume_forty_percent)
    val sixtyPercentLabel = stringResource(R.string.volume_sixty_percent)
    val options = remember(
        offAllCap, fortyPercentLabel, sixtyPercentLabel, addLabel, state.customVolume
    ) {
        listOf(
            offAllCap,
            fortyPercentLabel,
            sixtyPercentLabel,
            state.customVolume?.takeIf { it > 0 }?.let { it.toString() + "%" } ?: addLabel,
        )
    }

    var selectedIndex by remember { mutableIntStateOf(NO_SELECTED_VOLUME_INDEX) }

    LaunchedEffect(state.customVolume, state.volumeButtonTapCount) {
        Timber.i("launched effect")

        val customVolume = state.customVolume ?: 0

        selectedIndex = selectedVolumeIndex(
            volume = view.context.mediaVolume,
            maxVolume = maxVolume,
            customVolume = customVolume,
        )
    }

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(), space = SegmentedButtonDefaults.BorderWidth
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedIndex = index
                    when (index) {
                        OFF_VOLUME_INDEX, FORTY_PERCENT_VOLUME_INDEX, SIXTY_PERCENT_VOLUME_INDEX -> {
                            presetVolume(index, maxVolume)?.let { volume ->
                                view.setVolume(volume)
                                vm.increaseVolumeButtonTapCount()
                            }
                        }

                        CUSTOM_VOLUME_INDEX -> {
                            vm.toggleHaveTappedAddButton(true)
                            val customVolume = state.customVolume
                            if (customVolume != null && customVolume > 0) {
                                view.setVolume(customVolume * PERCENT_TO_VOLUME_RATIO * maxVolume)
                                vm.increaseVolumeButtonTapCount()
                            } else {
                                view.context.openScreen(VolumeCustomizeActivity::class.java)
                            }
                        }
                    }
                },
                selected = index == selectedIndex,
                shape = SegmentedButtonDefaults.itemShape(
                    index = index, count = options.size
                ),
                colors = SegmentedButtonDefaults.colors()
                    .copy(inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                if (index != CUSTOM_VOLUME_INDEX) {
                    Text(label)
                } else if (state.haveTappedAddButton) {
                    Text(label)
                } else {
                    GradientSmall(
                        textToDisplay = label, colors = listOf(
                            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            }
        }
    }

    val customVolume = state.customVolume
    if (customVolume != null && customVolume > 0 && !isHome) {
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            TextButton({
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                view.context.openScreen(VolumeCustomizeActivity::class.java)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                SpacerPadding()
                Text(stringResource(R.string.edit))
            }
        }
    }
}

internal fun presetVolume(index: Int, maxVolume: Int): Double? = when (index) {
    OFF_VOLUME_INDEX -> 0.0
    FORTY_PERCENT_VOLUME_INDEX -> FORTY_PERCENT * maxVolume
    SIXTY_PERCENT_VOLUME_INDEX -> SIXTY_PERCENT * maxVolume
    else -> null
}

internal fun selectedVolumeIndex(
    volume: Int,
    maxVolume: Int,
    customVolume: Int,
): Int = when (volume) {
    0 -> OFF_VOLUME_INDEX
    (FORTY_PERCENT * maxVolume).roundToInt() -> FORTY_PERCENT_VOLUME_INDEX
    (SIXTY_PERCENT * maxVolume).roundToInt() -> SIXTY_PERCENT_VOLUME_INDEX
    (customVolume * PERCENT_TO_VOLUME_RATIO * maxVolume).roundToInt() -> CUSTOM_VOLUME_INDEX
    else -> NO_SELECTED_VOLUME_INDEX
}
