package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ScrollableBoldText
import me.dizzykitty3.androidtoolkitty.uicomponents.ScrollableItalicText
import me.dizzykitty3.androidtoolkitty.uicomponents.ScrollableText
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen

@AndroidEntryPoint
class AndroidVersionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            ToolkitScreen(
                title = R.string.android_versions,
                dynamicColor = state.dynamicColor
            ) {
                BaseCard(title = R.string.latest_version) {
                    LatestVersion()
                }
                BaseCard(title = R.string.older_releases) {
                    OlderReleases()
                }
            }
        }
    }
}

@Composable
private fun LatestVersion() {
    VersionRow(androidVersions.first())
}

@Composable
private fun OlderReleases() {
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(0.4f)) {
            androidVersions.drop(1).forEach { version -> VersionName(version) }
            ScrollableText("...")
        }

        Column(Modifier.weight(0.6f)) {
            androidVersions.drop(1).forEach { version ->
                ScrollableItalicText("API ${version.api}, ${version.codename}")
            }
        }
    }
}

@Composable
private fun VersionRow(version: AndroidVersion) {
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(0.4F)) {
            VersionName(version)
        }
        Column(Modifier.weight(0.6F)) {
            ScrollableItalicText("API ${version.api}, ${version.codename}")
        }
    }
}

@Composable
private fun VersionName(version: AndroidVersion) {
    val text = "Android ${version.name}"
    if (Build.VERSION.SDK_INT == version.api) {
        ScrollableBoldText(text)
    } else {
        ScrollableText(text)
    }
}

private data class AndroidVersion(
    val api: Int,
    val name: String,
    val codename: String,
)

private val androidVersions = listOf(
    AndroidVersion(37, "17", "CinnamonBun"),
    AndroidVersion(36, "16", "Baklava"),
    AndroidVersion(35, "15", "VanillaIceCream"),
    AndroidVersion(34, "14", "UpsideDownCake"),
    AndroidVersion(33, "13", "Tiramisu"),
    AndroidVersion(32, "12L", "Sv2"),
    AndroidVersion(31, "12", "S"),
    AndroidVersion(30, "11", "R"),
    AndroidVersion(29, "10", "Q"),
    AndroidVersion(28, "9", "Pie"),
    AndroidVersion(27, "8.1", "Oreo"),
    AndroidVersion(26, "8", "Oreo"),
    AndroidVersion(25, "7.1.1", "Nougat"),
    AndroidVersion(24, "7", "Nougat"),
    AndroidVersion(23, "6", "Marshmallow"),
    AndroidVersion(22, "5.1", "Lollipop"),
    AndroidVersion(21, "5", "Lollipop"),
    AndroidVersion(20, "4.4W", "KitKat Wear"),
    AndroidVersion(19, "4.4", "KitKat"),
    AndroidVersion(18, "4.3", "Jelly Bean"),
    AndroidVersion(17, "4.2", "Jelly Bean"),
    AndroidVersion(16, "4.1", "Jelly Bean"),
    AndroidVersion(15, "4.0.3", "IceCreamSandwich"),
    AndroidVersion(14, "4.0", "IceCreamSandwich"),
    AndroidVersion(13, "3.2", "Honeycomb"),
    AndroidVersion(12, "3.1", "Honeycomb"),
    AndroidVersion(11, "3.0", "Honeycomb"),
    AndroidVersion(10, "2.3.3", "Gingerbread"),
    AndroidVersion(9, "2.3", "Gingerbread"),
    AndroidVersion(8, "2.2", "Froyo"),
    AndroidVersion(7, "2.1", "Eclair"),
)
