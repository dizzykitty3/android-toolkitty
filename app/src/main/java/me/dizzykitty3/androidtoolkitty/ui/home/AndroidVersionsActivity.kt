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
    val v = Build.VERSION.SDK_INT

    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(0.4F)) {
            if (v == 37) ScrollableBoldText("Android 17") else ScrollableText("Android 17")
        }

        Column(Modifier.weight(0.6F)) {
            ScrollableItalicText("API 37, CinnamonBun")
        }
    }
}

@Composable
private fun OlderReleases() {
    val v = Build.VERSION.SDK_INT

    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(0.4f)) {
            if (v == 36) ScrollableBoldText("Android 16") else ScrollableText("Android 16")
            if (v == 35) ScrollableBoldText("Android 15") else ScrollableText("Android 15")
            if (v == 34) ScrollableBoldText("Android 14") else ScrollableText("Android 14")
            if (v == 33) ScrollableBoldText("Android 13") else ScrollableText("Android 13")
            if (v == 32) ScrollableBoldText("Android 12L") else ScrollableText("Android 12L")
            if (v == 31) ScrollableBoldText("Android 12") else ScrollableText("Android 12")
            if (v == 30) ScrollableBoldText("Android 11") else ScrollableText("Android 11")
            if (v == 29) ScrollableBoldText("Android 10") else ScrollableText("Android 10")
            if (v == 28) ScrollableBoldText("Android 9") else ScrollableText("Android 9")
            if (v == 27) ScrollableBoldText("Android 8.1") else ScrollableText("Android 8.1")
            if (v == 26) ScrollableBoldText("Android 8") else ScrollableText("Android 8")
            if (v == 25) ScrollableBoldText("Android 7.1.1") else ScrollableText("Android 7.1.1")
            if (v == 24) ScrollableBoldText("Android 7") else ScrollableText("Android 7")
            if (v == 23) ScrollableBoldText("Android 6") else ScrollableText("Android 6")
            if (v == 22) ScrollableBoldText("Android 5.1") else ScrollableText("Android 5.1")
            if (v == 21) ScrollableBoldText("Android 5") else ScrollableText("Android 5")
            if (v == 20) ScrollableBoldText("Android 4.4W") else ScrollableText("Android 4.4W")
            if (v == 19) ScrollableBoldText("Android 4.4") else ScrollableText("Android 4.4")
            if (v == 18) ScrollableBoldText("Android 4.3") else ScrollableText("Android 4.3")
            if (v == 17) ScrollableBoldText("Android 4.2") else ScrollableText("Android 4.2")
            if (v == 16) ScrollableBoldText("Android 4.1") else ScrollableText("Android 4.1")
            if (v == 15) ScrollableBoldText("Android 4.0.3") else ScrollableText("Android 4.0.3")
            if (v == 14) ScrollableBoldText("Android 4.0") else ScrollableText("Android 4.0")
            if (v == 13) ScrollableBoldText("Android 3.2") else ScrollableText("Android 3.2")
            if (v == 12) ScrollableBoldText("Android 3.1") else ScrollableText("Android 3.1")
            if (v == 11) ScrollableBoldText("Android 3.0") else ScrollableText("Android 3.0")
            if (v == 10) ScrollableBoldText("Android 2.3.3") else ScrollableText("Android 2.3.3")
            if (v == 9) ScrollableBoldText("Android 2.3") else ScrollableText("Android 2.3")
            if (v == 8) ScrollableBoldText("Android 2.2") else ScrollableText("Android 2.2")
            if (v == 7) ScrollableBoldText("Android 2.1") else ScrollableText("Android 2.1")
            ScrollableText("...")
        }

        Column(Modifier.weight(0.6f)) {
            ScrollableItalicText("API 36, Baklava")
            ScrollableItalicText("API 35, VanillaIceCream")
            ScrollableItalicText("API 34, UpsideDownCake")
            ScrollableItalicText("API 33, Tiramisu")
            ScrollableItalicText("API 32, Sv2")
            ScrollableItalicText("API 31, S")
            ScrollableItalicText("API 30, R")
            ScrollableItalicText("API 29, Q")
            ScrollableItalicText("API 28, Pie")
            ScrollableItalicText("API 27, Oreo")
            ScrollableItalicText("API 26, Oreo")
            ScrollableItalicText("API 25, Nougat")
            ScrollableItalicText("API 24, Nougat")
            ScrollableItalicText("API 23, Marshmallow")
            ScrollableItalicText("API 22, Lollipop")
            ScrollableItalicText("API 21, Lollipop")
            ScrollableItalicText("API 20, KitKat Wear")
            ScrollableItalicText("API 19, KitKat")
            ScrollableItalicText("API 18, Jelly Bean")
            ScrollableItalicText("API 17, Jelly Bean")
            ScrollableItalicText("API 16, Jelly Bean")
            ScrollableItalicText("API 15, IceCreamSandwich")
            ScrollableItalicText("API 14, IceCreamSandwich")
            ScrollableItalicText("API 13, Honeycomb")
            ScrollableItalicText("API 12, Honeycomb")
            ScrollableItalicText("API 11, Honeycomb")
            ScrollableItalicText("API 10, Gingerbread")
            ScrollableItalicText("API 9, Gingerbread")
            ScrollableItalicText("API 8, Froyo")
            ScrollableItalicText("API 7, Eclair")
        }
    }
}
