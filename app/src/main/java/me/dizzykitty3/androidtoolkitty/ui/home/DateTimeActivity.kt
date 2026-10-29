package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseScreen
import me.dizzykitty3.androidtoolkitty.uicomponents.SectionSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.Section
import me.dizzykitty3.androidtoolkitty.uicomponents.YearProgressIndicator
import me.dizzykitty3.androidtoolkitty.utils.DateUtils
import me.dizzykitty3.androidtoolkitty.utils.DateUtils.toProgress

@AndroidEntryPoint
class DateTimeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()
            BaseScreen(title = R.string.date_time_tools, dynamicColor = state.dynamicColor) {
                Section(R.string.year_progress) {
                    YearProgressIndicator()
                    Text(DateUtils.yearProgress.toProgress(stringResource(R.string.happy_new_year)))
                }
                SectionSpacePadding()
                Section(R.string.unix_timestamp) {
                    Text(stringResource(R.string.current_unix_timestamp_in_seconds))
                    Text(DateUtils.unixTimestampInSeconds)
                }
            }
        }
    }
}
