package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.key
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.homeCardDefinitions
import me.dizzykitty3.androidtoolkitty.ui.home.orderedHomeCards
import me.dizzykitty3.androidtoolkitty.ui.home.visibleHomeCards
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomHideCardSettingSwitch
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen

@AndroidEntryPoint
class CustomizeHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                ToolkitScreen(
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

    BaseCard(R.string.customize_home) {
        val haptic = LocalHapticFeedback.current

        TextButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            orderMode = !orderMode
        }) {
            Text(stringResource(if (orderMode) R.string.order_mode_on else R.string.order_mode_off))
        }
        val cards = if (orderMode) state.visibleHomeCards() else state.orderedHomeCards()
        if (orderMode) {
            Text(stringResource(
                if (cards.isEmpty()) R.string.home_card_order_empty
                else R.string.home_card_order_hint,
            ))
        }
        val defaultOrder = homeCardDefinitions.map { it.id.preferenceKey }
        cards.forEachIndexed { index, card ->
            key(card.id) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (orderMode) {
                            Text(stringResource(card.id.title))
                        } else {
                            CustomHideCardSettingSwitch(
                                text = card.id.title,
                                isChecked = state.isShown(card.id.preferenceKey)
                            ) { newState ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                vm.saveShownState(card.id.preferenceKey, newState)
                            }
                        }
                    }
                    if (orderMode) {
                        IconButton(
                            enabled = index > 0,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                vm.moveHomeCard(card.id.preferenceKey, -1, defaultOrder)
                            },
                        ) {
                            Icon(
                                Icons.Outlined.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.move_card_up, stringResource(card.id.title)),
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
                                contentDescription = stringResource(R.string.move_card_down, stringResource(card.id.title)),
                            )
                        }
                    }
                }
            }
        }

        SpacerPadding()

        if (orderMode) {
            Button(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                vm.resetHomeCardOrder()
            }) { Text(stringResource(R.string.reset_home_card_order)) }
        } else {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    homeCardDefinitions.forEach { card -> vm.saveShownState(card.id.preferenceKey, false) }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = stringResource(R.string.hide_all_cards),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                SpacerPadding()
                Text(stringResource(R.string.hide_all_cards))
            }

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    homeCardDefinitions.forEach { card -> vm.saveShownState(card.id.preferenceKey, true) }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = stringResource(R.string.show_all_cards),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                SpacerPadding()
                Text(stringResource(R.string.show_all_cards))
            }
        }
    }
}
