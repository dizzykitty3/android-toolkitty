package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Bundle
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.ContentPasteSearch
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.HomeCardId
import me.dizzykitty3.androidtoolkitty.ui.home.homeCardDefinitions
import me.dizzykitty3.androidtoolkitty.ui.home.orderedHomeCards
import me.dizzykitty3.androidtoolkitty.ui.home.visibleHomeCards
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseScreen
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomHideCardSettingSwitch
import me.dizzykitty3.androidtoolkitty.uicomponents.SettingsRow
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding

@AndroidEntryPoint
class CustomizeHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                BaseScreen(
                    largeTitle = true,
                    title = R.string.customize_home,
                    dynamicColor = state.dynamicColor
                ) {
                    CustomizeHomeComposable()
                }
            }
        }
    }
}

@Composable
private fun CustomizeHomeComposable() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    var orderMode by rememberSaveable { mutableStateOf(false) }

    Column {
        val haptic = LocalHapticFeedback.current

        SpacerPadding()
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
            space = SegmentedButtonDefaults.BorderWidth,
        ) {
            listOf(
                R.string.home_card_visibility,
                R.string.home_card_order
            ).forEachIndexed { index, label ->
                SegmentedButton(
                    selected = orderMode == (index == 1),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        orderMode = index == 1
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                    colors = SegmentedButtonDefaults.colors()
                        .copy(inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                ) {
                    Text(stringResource(label))
                }
            }
        }
        SpacerPadding()
        val cards = if (orderMode) state.visibleHomeCards() else state.orderedHomeCards()
        if (orderMode) {
            Text(
                stringResource(
                    if (cards.isEmpty()) R.string.home_card_order_empty
                    else R.string.home_card_order_hint,
                )
            )
        }
        val defaultOrder = homeCardDefinitions.map { it.id.preferenceKey }
        SettingsSection {
            cards.forEachIndexed { index, card ->
                key(card.id) {
                    if (!orderMode) {
                        CustomHideCardSettingSwitch(
                            text = card.id.title,
                            icon = card.id.settingsIcon,
                            isChecked = state.isShown(card.id.preferenceKey),
                        ) { newState ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            vm.saveShownState(card.id.preferenceKey, newState)
                        }
                    } else {
                        SettingsRow {
                            Icon(card.id.settingsIcon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(20.dp))
                            Text(stringResource(card.id.title), Modifier.weight(1f))
                            IconButton(
                                enabled = index > 0,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    vm.moveHomeCard(card.id.preferenceKey, -1, defaultOrder)
                                },
                            ) {
                                Icon(
                                    Icons.Outlined.KeyboardArrowUp,
                                    contentDescription = stringResource(
                                        R.string.move_card_up,
                                        stringResource(card.id.title)
                                    ),
                                )
                            }
                            IconButton(
                                enabled = index < cards.lastIndex,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    vm.moveHomeCard(card.id.preferenceKey, 1, defaultOrder)
                                },
                            ) {
                                Icon(
                                    Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = stringResource(
                                        R.string.move_card_down,
                                        stringResource(card.id.title)
                                    ),
                                )
                            }
                        }
                    }
                }
            }

            if (orderMode && cards.isNotEmpty()) {
                SettingsRow {
                    Button(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        vm.resetHomeCardOrder()
                    }) { Text(stringResource(R.string.reset_home_card_order)) }
                }
            }
        }
        SpacerPadding()

        if (!orderMode) {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    homeCardDefinitions.forEach { card ->
                        vm.saveShownState(
                            card.id.preferenceKey,
                            false
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.hide_all_cards))
            }

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    homeCardDefinitions.forEach { card ->
                        vm.saveShownState(
                            card.id.preferenceKey,
                            true
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.show_all_cards))
            }
        }
        SpacerPadding()
    }
}

private val HomeCardId.settingsIcon: ImageVector
    get() = when (this) {
        HomeCardId.YEAR_PROGRESS -> Icons.Outlined.HourglassTop
        HomeCardId.VOLUME -> Icons.AutoMirrored.Outlined.VolumeUp
        HomeCardId.CLIPBOARD -> Icons.Outlined.ContentPasteSearch
        HomeCardId.SEARCH -> Icons.Outlined.Search
        HomeCardId.SYSTEM_SHORTCUTS -> Icons.Outlined.Settings
        HomeCardId.WHEEL_OF_FORTUNE -> Icons.Outlined.Casino
        HomeCardId.BLUETOOTH_DEVICE -> Icons.Outlined.Bluetooth
        HomeCardId.CHARACTER_CODES -> Icons.AutoMirrored.Outlined.Notes
        HomeCardId.MAPS -> Icons.Outlined.Map
        HomeCardId.FONT_WEIGHT -> Icons.Outlined.FontDownload
        HomeCardId.COMPOSE_CATALOG -> Icons.Outlined.DashboardCustomize
        HomeCardId.HAPTIC_FEEDBACK -> Icons.Outlined.Vibration
    }
