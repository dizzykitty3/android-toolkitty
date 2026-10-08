package me.dizzykitty3.androidtoolkitty.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.util.withContext
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseScaffold
import me.dizzykitty3.androidtoolkitty.uicomponents.BottomPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.LargeScreenTitle
import me.dizzykitty3.androidtoolkitty.uicomponents.TopBar
import me.dizzykitty3.androidtoolkitty.uicomponents.TopPadding

@AndroidEntryPoint
class LicensesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()
            BaseScaffold(dynamicColor = state.dynamicColor) {
                val context = LocalContext.current
                val libraries = remember(context) { Libs.Builder().withContext(context).build() }
                val title = stringResource(R.string.licenses)
                Column(Modifier.fillMaxSize().padding(horizontal = dimensionResource(R.dimen.padding_screen))) {
                    TopPadding()
                    TopBar()
                    LargeScreenTitle(title)
                    LibrariesContainer(
                        libraries = libraries,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape))),
                        colors = LibraryDefaults.libraryColors(
                            libraryBackgroundColor = MaterialTheme.colorScheme.surfaceBright,
                        ),
                    )
                    BottomPadding()
                }
            }
        }
    }
}
