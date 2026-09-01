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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import me.dizzykitty3.androidtoolkitty.utils.mediaVolume
import me.dizzykitty3.androidtoolkitty.utils.setVolume
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import timber.log.Timber
import kotlin.math.roundToInt

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
    var mCustomVolume by remember { mutableStateOf<Int?>(null) }
    val offAllCap = stringResource(R.string.off_all_cap)
    val addLabel = stringResource(R.string.add)
    val options = remember(offAllCap, addLabel) {
        mutableStateListOf(
            offAllCap,
            "40%",
            "60%",
            addLabel
        )
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(state.customVolume, state.volumeButtonTapCount) {
        Timber.i("launched effect")

        mCustomVolume = state.customVolume
        val customVolume = mCustomVolume ?: 0

        if (customVolume > 0) {
            options[3] = "${customVolume}%"
        } else {
            options[3] = addLabel
        }

        selectedIndex = when (view.context.mediaVolume) {
            0 -> 0
            (0.4 * maxVolume).roundToInt() -> 1
            (0.6 * maxVolume).roundToInt() -> 2
            (customVolume * 0.01 * maxVolume).roundToInt() -> 3
            else -> -1
        }
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
                        0 -> {
                            view.setVolume(0)
                            vm.increaseVolumeButtonTapCount()
                        }

                        1 -> {
                            view.setVolume(0.4 * maxVolume)
                            vm.increaseVolumeButtonTapCount()
                        }

                        2 -> {
                            view.setVolume(0.6 * maxVolume)
                            vm.increaseVolumeButtonTapCount()
                        }

                        3 -> {
                            vm.toggleHaveTappedAddButton(true)
                            val customVolume = mCustomVolume
                            if (customVolume != null && customVolume > 0) {
                                view.setVolume(customVolume * 0.01 * maxVolume)
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
                if (label != stringResource(R.string.add)) {
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

    val customVolume = mCustomVolume
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
