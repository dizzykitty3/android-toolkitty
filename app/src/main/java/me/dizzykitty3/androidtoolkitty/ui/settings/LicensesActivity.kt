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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.util.withContext
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.theme.AppTheme
import me.dizzykitty3.androidtoolkitty.uicomponents.LicenseScreen

@AndroidEntryPoint
class LicensesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

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
                        val context = LocalContext.current
                        LicenseScreen(screenTitle = R.string.licenses) {
                            Surface(shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape))) {
                                LibrariesContainer(
                                    libraries = Libs.Builder().withContext(context).build(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
