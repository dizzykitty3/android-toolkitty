package me.dizzykitty3.androidtoolkitty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.core.layout.WindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.Greeting
import me.dizzykitty3.androidtoolkitty.home.Test
import me.dizzykitty3.androidtoolkitty.theme.AppTheme
import me.dizzykitty3.androidtoolkitty.ui.home.HomeCards
import me.dizzykitty3.androidtoolkitty.ui.home.HomeStatusBar
import me.dizzykitty3.androidtoolkitty.ui.home.TwoColumnHomeCards
import me.dizzykitty3.androidtoolkitty.ui.settings.SettingsActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BottomPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.DevBuildTip
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.TopPadding
import me.dizzykitty3.androidtoolkitty.utils.ClipboardUtil
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var continuation: Continuation<Unit>? = null
    private var clipboardClearJob: Job? = null
    private var continuationNotResumed = AtomicBoolean(true)
    private var isAutoClearClipboard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            isAutoClearClipboard = state.autoClearClipboard

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                AppTheme(
                    dynamicColor = state.dynamicColor
                ) {
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
                                currentWindowAdaptiveInfoV2().windowSizeClass.minWidthDp >= WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
                            if (largeScreen) {
                                TabletLayout()
                            } else {
                                MobileLayout()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        continuationNotResumed.set(true)
        clipboardClearJob?.cancel()
        clipboardClearJob = lifecycleScope.launch {
            suspendCancellableCoroutine { cont ->
                continuation = cont
            }
            if (isAutoClearClipboard) {
                if (ClipboardUtil.clear()) {
                    window.decorView.showSnackbar(R.string.clipboard_cleared_automatically)
                    Timber.i("Clipboard cleared automatically")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Timber.d("onWindowFocusChanged")
        if (hasFocus and continuationNotResumed.get()) { // Clipboard operations require window focus
            try {
                Timber.d("continuation resume start")
                continuation?.resume(Unit)
            } catch (e: IllegalStateException) {
                Timber.e(e)
            } finally {
                Timber.i("continuation resumed")
                continuationNotResumed.set(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        clipboardClearJob?.cancel()
        clipboardClearJob = null
        continuation = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }
}

@Composable
private fun MobileLayout() {
    val screenPadding = dimensionResource(R.dimen.padding_screen)
    val debug = BuildConfig.DEBUG
    val viewModel = LocalSettingsViewModel.current

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
private fun TabletLayout() {
    val largeScreenPadding = dimensionResource(R.dimen.padding_screen_large)
    val debug = BuildConfig.DEBUG
    val viewModel = LocalSettingsViewModel.current

    Column {
        TopPadding()
        Column(Modifier.padding(start = largeScreenPadding, end = largeScreenPadding)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1F)) { Greeting() }
                Box(Modifier.weight(1F)) { TopBar(isTablet = true) }
            }
            SpacerPadding()
            if (debug) {
                DevBuildTip()
            }
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
