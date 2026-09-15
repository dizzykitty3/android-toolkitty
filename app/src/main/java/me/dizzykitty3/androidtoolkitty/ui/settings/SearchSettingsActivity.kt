package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomSwitchRow
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
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
                ToolkitScreen(
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

    BaseCard(R.string.search_engine) {
        SearchEngine.entries.forEach { engine ->
            SettingsRadioRow(
                title = engine.title,
                selected = state.searchEngine == engine,
                onClick = { viewModel.setSearchEngine(engine) },
            )
        }
    }

    BaseCard(R.string.video_search_engine) {
        VideoSearchEngine.entries.forEach { engine ->
            SettingsRadioRow(
                title = engine.title,
                selected = state.videoSearchEngine == engine,
                onClick = { viewModel.setVideoSearchEngine(engine) },
            )
        }
    }

    BaseCard(R.string.search_preferences) {
        CustomSwitchRow(
            title = R.string.do_not_remember_last_search,
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
    selected: Boolean,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeightIn(min = dimensionResource(R.dimen.height_setting_row))
                .selectable(
                    selected = selected,
                    role = Role.RadioButton,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                )
                .padding(horizontal = dimensionResource(R.dimen.padding_card_content)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(title))
            Spacer(Modifier.weight(1F))
            RadioButton(selected = selected, onClick = null)
        }
    }
}
