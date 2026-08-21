package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen

@AndroidEntryPoint
class HapticTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            ToolkitScreen(
                title = R.string.haptic_test,
                dynamicColor = state.dynamicColor
            ) {
                HapticTestComposable()
            }
        }
    }
}

@Composable
private fun HapticTestComposable() {
    BaseCard(R.string.haptic_test) {
        val haptic = LocalHapticFeedback.current
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.Confirm) }) { Text("Confirm") }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.ContextClick) }) {
            Text(
                "ContextClick"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd) }) {
            Text(
                "GestureEnd"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate) }) {
            Text(
                "GestureThresholdActivate"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }) {
            Text(
                "LongPress"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.Reject) }) { Text("Reject") }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick) }) {
            Text(
                "SegmentFrequentTick"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.SegmentTick) }) {
            Text(
                "SegmentTick"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }) {
            Text(
                "TextHandleMove"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.ToggleOff) }) {
            Text(
                "ToggleOff"
            )
        }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.ToggleOn) }) { Text("ToggleOn") }
        TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.VirtualKey) }) {
            Text(
                "VirtualKey"
            )
        }
    }
}
