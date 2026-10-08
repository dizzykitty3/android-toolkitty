package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseScreen
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomSwitchRow
import me.dizzykitty3.androidtoolkitty.uicomponents.SettingsRow
import me.dizzykitty3.androidtoolkitty.utils.SearchEngine
import me.dizzykitty3.androidtoolkitty.utils.VideoSearchEngine

@AndroidEntryPoint
class SearchSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                BaseScreen(
                    largeTitle = true,
                    title = R.string.search_settings,
                    dynamicColor = state.dynamicColor,
                ) {
                    SearchSettings()
                }
            }
        }
    }
}

@Composable
private fun SearchSettings() {
    val viewModel = LocalSettingsViewModel.current
    val haptic = LocalHapticFeedback.current
    val state by viewModel.settingsState.collectAsStateWithLifecycle()

    SettingsSection(R.string.search_engine, selectable = true) {
        SearchEngine.entries.forEach { engine ->
            SettingsRadioRow(
                title = engine.title,
                icon = Icons.Outlined.Search,
                isSelected = state.searchEngine == engine,
                onClick = { viewModel.setSearchEngine(engine) },
            )
        }
    }

    CardSpacePadding()
    SettingsSection(R.string.video_search_engine, selectable = true) {
        VideoSearchEngine.entries.forEach { engine ->
            SettingsRadioRow(
                title = engine.title,
                icon = Icons.Outlined.PlayCircleOutline,
                isSelected = state.videoSearchEngine == engine,
                onClick = { viewModel.setVideoSearchEngine(engine) },
            )
        }
    }

    CardSpacePadding()
    SettingsSection(R.string.search_preferences) {
        CustomSwitchRow(
            title = R.string.do_not_remember_last_search,
            icon = Icons.Outlined.History,
            checked = state.doNotRememberLastSearch,
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.setDoNotRememberLastSearch(it)
        }
    }
}

@Composable
private fun SettingsRadioRow(
    @StringRes title: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    SettingsRow(
        interaction = Modifier.selectable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = MaterialTheme.colorScheme.onSurfaceVariant),
            selected = isSelected,
            role = Role.RadioButton,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        ),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(20.dp))
        Text(stringResource(title), modifier = Modifier.weight(1f))
        Spacer(Modifier.width(16.dp))
        RadioButton(selected = isSelected, onClick = null)
    }
}
