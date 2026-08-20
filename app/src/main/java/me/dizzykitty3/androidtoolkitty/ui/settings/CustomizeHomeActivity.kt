package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.homeCardDefinitions
import me.dizzykitty3.androidtoolkitty.theme.AppTheme
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomHideCardSettingSwitch
import me.dizzykitty3.androidtoolkitty.uicomponents.Screen
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
                            Screen(screenTitle = R.string.customize_home) {
                                CustomizeHomeComposable()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomizeHomeComposable() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()

    BaseCard(R.string.customize_home) {
        val haptic = LocalHapticFeedback.current

        homeCardDefinitions.forEach { card ->
            CustomHideCardSettingSwitch(
                text = card.id.title,
                isChecked = state.cardShownStates[card.id.key] ?: true
            ) { newState ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                vm.saveShownState(card.id.key, newState)
            }
        }

        SpacerPadding()

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                homeCardDefinitions.forEach { card -> vm.saveShownState(card.id.key, false) }
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
                homeCardDefinitions.forEach { card -> vm.saveShownState(card.id.key, true) }
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
