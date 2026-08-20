package me.dizzykitty3.androidtoolkitty.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import me.dizzykitty3.androidtoolkitty.BuildConfig
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.Greeting
import me.dizzykitty3.androidtoolkitty.home.Test
import me.dizzykitty3.androidtoolkitty.theme.AppTheme
import me.dizzykitty3.androidtoolkitty.ui.settings.SettingsActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BottomPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.DevBuildTip
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.TopPadding
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen

@Composable
fun HomeScreen(onAutoClearClipboardChanged: (Boolean) -> Unit) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.settingsState.collectAsStateWithLifecycle()

    LaunchedEffect(state.autoClearClipboard) {
        onAutoClearClipboardChanged(state.autoClearClipboard)
    }

    CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
        AppTheme(dynamicColor = state.dynamicColor) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.surfaceContainer,
            ) { innerPadding ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    val largeScreen =
                        currentWindowAdaptiveInfoV2().windowSizeClass.minWidthDp >=
                            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
                    if (largeScreen) TabletLayout(viewModel) else MobileLayout(viewModel)
                }
            }
        }
    }
}

@Composable
private fun MobileLayout(viewModel: SettingsViewModel) {
    val screenPadding = dimensionResource(R.dimen.padding_screen)
    val debug = BuildConfig.DEBUG

    Column {
        TopPadding()
        LazyColumn(Modifier.padding(start = screenPadding, end = screenPadding)) {
            item { TopBar() }
            item { CardSpacePadding() }
            item { CardSpacePadding() }
            item { Greeting() }
            item { CardSpacePadding() }
            item { CardSpacePadding() }
            if (debug) item {
                DevBuildTip()
                CardSpacePadding()
                Test()
            }
            item { HomeCards(viewModel) }
            item { BottomPadding() }
        }
    }
}

@Composable
private fun TabletLayout(viewModel: SettingsViewModel) {
    val largeScreenPadding = dimensionResource(R.dimen.padding_screen_large)
    val debug = BuildConfig.DEBUG

    Column {
        TopPadding()
        Column(Modifier.padding(start = largeScreenPadding, end = largeScreenPadding)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1F)) { Greeting() }
                Box(Modifier.weight(1F)) { TopBar(isTablet = true) }
            }
            SpacerPadding()
            if (debug) DevBuildTip()
            TwoColumnHomeCards(viewModel)
        }
    }
}

@Composable
private fun TopBar(isTablet: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1F)) { HomeStatusBar(isTablet) }
        SettingsButton()
    }
}

@Composable
private fun SettingsButton() {
    val haptic = LocalHapticFeedback.current

    IconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            openScreen(SettingsActivity::class.java)
        }) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
