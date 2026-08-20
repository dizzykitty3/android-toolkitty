package me.dizzykitty3.androidtoolkitty

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.MediaBluetoothOn
import androidx.compose.material.icons.outlined.NetworkCell
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.core.layout.WindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.Greeting
import me.dizzykitty3.androidtoolkitty.home.Test
import me.dizzykitty3.androidtoolkitty.home.visibleHomeCards
import me.dizzykitty3.androidtoolkitty.sharedpreferences.SettingsSharedPref
import me.dizzykitty3.androidtoolkitty.theme.AppTheme
import me.dizzykitty3.androidtoolkitty.ui.settings.SettingsActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BottomPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.DevBuildTip
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.TopPadding
import me.dizzykitty3.androidtoolkitty.utils.BatteryUtil
import me.dizzykitty3.androidtoolkitty.utils.BluetoothUtil.headsetNotConnected
import me.dizzykitty3.androidtoolkitty.utils.BluetoothUtil.isHeadsetConnected
import me.dizzykitty3.androidtoolkitty.utils.ClipboardUtil
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openSystemSettings
import me.dizzykitty3.androidtoolkitty.utils.NetworkUtil
import me.dizzykitty3.androidtoolkitty.utils.OSVersion
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
                        SettingsSharedPref.topPaddingDp = innerPadding.calculateTopPadding().value
                        SettingsSharedPref.bottomPaddingDp =
                            innerPadding.calculateBottomPadding().value
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
        Box(Modifier.weight(1F)) { Status(isTablet) }
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

@Composable
private fun Status(isTablet: Boolean = false) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(BatteryUtil.batteryLevel()) }

    LaunchedEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                batteryLevel = BatteryUtil.batteryLevel()
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        try {
            awaitCancellation()
        } finally {
            context.unregisterReceiver(receiver)
        }
    }

    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    Row(Modifier.horizontalScroll(rememberScrollState())) {
        if (isTablet || view.context.headsetNotConnected()) {
            Surface(
                shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(Modifier.clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    view.context.openSystemSettings(S_BATTERY)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.BatteryStd,
                        contentDescription = stringResource(R.string.battery),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                    )
                    SpacerPadding()
                    Text(
                        "$batteryLevel%",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                    )
                }
            }
            SpacerPadding()
            SpacerPadding()
            NetworkState()
        }

        if (view.context.isHeadsetConnected()) {
            if (isTablet) {
                SpacerPadding()
                SpacerPadding()
            }
            Surface(
                shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(Modifier.clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    view.context.openSystemSettings(S_BLUETOOTH)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.MediaBluetoothOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                    )
                    SpacerPadding()
                    Text(
                        stringResource(R.string.audio_devices_connected),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                    )
                }
            }
        }
    }
    CardSpacePadding()
}

@Composable
private fun NetworkState() {
    val context = LocalContext.current
    var networkState by remember { mutableIntStateOf(NetworkUtil.networkState()) }

    LaunchedEffect(Unit) {
        if (OSVersion.android7()) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Timber.d("Network onAvailable: $network")
                    networkState = NetworkUtil.networkState()
                }

                override fun onLost(network: Network) {
                    Timber.d("Network onLost: $network")
                    // When the default network is lost, we set it to OFFLINE immediately
                    // to avoid potential race conditions with NetworkUtil.networkState()
                    networkState = NetworkUtil.STATE_CODE_OFFLINE
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    Timber.d("Network onCapabilitiesChanged: $network")
                    networkState = NetworkUtil.networkState()
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            try {
                awaitCancellation()
            } finally {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        } else {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    networkState = NetworkUtil.networkState()
                }
            }
            context.registerReceiver(
                receiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            try {
                awaitCancellation()
            } finally {
                context.unregisterReceiver(receiver)
            }
        }
    }

    when (networkState) {
        NetworkUtil.STATE_CODE_WIFI -> {
            NetworkStateIcon(Icons.Outlined.Wifi, R.string.wifi)
        }

        NetworkUtil.STATE_CODE_MOBILE -> {
            NetworkStateIcon(Icons.Outlined.NetworkCell, R.string.cellular)
        }

        NetworkUtil.STATE_CODE_OFFLINE -> {
            NetworkStateIcon(Icons.Outlined.WifiOff, R.string.offline)
        }

        else -> {
            NetworkStateIcon(Icons.Outlined.QuestionMark, R.string.unknown)
        }
    }
}

@Composable
private fun NetworkStateIcon(imageVector: ImageVector, @StringRes text: Int) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(Modifier.clickable {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openSystemSettings(S_WIFI)
        }) {
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(text),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
            )
            SpacerPadding()
            Text(
                stringResource(text), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
            )
        }
    }
}

@Composable
private fun HomeCards(viewModel: SettingsViewModel) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    state.visibleHomeCards().forEach { card ->
        card.content()
    }
}

@Composable
private fun TwoColumnHomeCards(viewModel: SettingsViewModel) {
    val cardPadding = dimensionResource(R.dimen.padding_card_space)
    val largeCardPadding = dimensionResource(R.dimen.padding_card_space_large)
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val cards = state.visibleHomeCards()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.padding(bottom = SettingsSharedPref.bottomPaddingDp.dp),
        verticalItemSpacing = cardPadding,
        horizontalArrangement = Arrangement.spacedBy(largeCardPadding),
    ) {
        items(cards, key = { it.id }) { card ->
            card.content()
        }
    }
}
