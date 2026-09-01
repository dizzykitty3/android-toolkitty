package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.MediaBluetoothOn
import androidx.compose.material.icons.outlined.NetworkCell
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import kotlinx.coroutines.awaitCancellation
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.S_BATTERY
import me.dizzykitty3.androidtoolkitty.S_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.S_WIFI
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openSystemSettings
import me.dizzykitty3.androidtoolkitty.utils.NetworkUtil
import me.dizzykitty3.androidtoolkitty.utils.OSVersion
import me.dizzykitty3.androidtoolkitty.utils.batteryLevel
import me.dizzykitty3.androidtoolkitty.utils.headsetNotConnected
import me.dizzykitty3.androidtoolkitty.utils.isHeadsetConnected
import me.dizzykitty3.androidtoolkitty.utils.networkState
import timber.log.Timber

@Composable
fun HomeStatusBar(isTablet: Boolean = false) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(context.batteryLevel()) }

    LaunchedEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                batteryLevel = context.batteryLevel()
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
            StatusChip(
                imageVector = Icons.Outlined.BatteryStd,
                contentDescription = stringResource(R.string.battery),
                text = "$batteryLevel%",
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    view.context.openSystemSettings(S_BATTERY)
                }
            )
            SpacerPadding()
            SpacerPadding()
            NetworkState()
        }

        if (view.context.isHeadsetConnected()) {
            if (isTablet) {
                SpacerPadding()
                SpacerPadding()
            }
            StatusChip(
                imageVector = Icons.Outlined.MediaBluetoothOn,
                contentDescription = stringResource(R.string.audio_devices_connected),
                text = stringResource(R.string.audio_devices_connected),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    view.context.openSystemSettings(S_BLUETOOTH)
                }
            )
        }
    }
    CardSpacePadding()
}

@Composable
private fun StatusChip(
    imageVector: ImageVector,
    contentDescription: String?,
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(Modifier.clickable(onClick = onClick)) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
            )
            SpacerPadding()
            Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F))
        }
    }
}

@Composable
private fun NetworkState() {
    val context = LocalContext.current
    var networkState by remember { mutableIntStateOf(context.networkState()) }

    LaunchedEffect(Unit) {
        fun refreshNetworkState() {
            networkState = context.networkState()
        }

        if (OSVersion.android7()) {
            val connectivityManager = context.getSystemService<ConnectivityManager>()
                ?: return@LaunchedEffect
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Timber.d("Network onAvailable: $network")
                    refreshNetworkState()
                }

                override fun onLost(network: Network) {
                    Timber.d("Network onLost: $network")
                    networkState = NetworkUtil.STATE_CODE_OFFLINE
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    Timber.d("Network onCapabilitiesChanged: $network, capabilities: $networkCapabilities")
                    refreshNetworkState()
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
                    refreshNetworkState()
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
        NetworkUtil.STATE_CODE_WIFI -> NetworkStateIcon(Icons.Outlined.Wifi, R.string.wifi)
        NetworkUtil.STATE_CODE_MOBILE -> NetworkStateIcon(Icons.Outlined.NetworkCell, R.string.cellular)
        NetworkUtil.STATE_CODE_OFFLINE -> NetworkStateIcon(Icons.Outlined.WifiOff, R.string.offline)
        else -> NetworkStateIcon(Icons.Outlined.QuestionMark, R.string.unknown)
    }
}

@Composable
private fun NetworkStateIcon(
    imageVector: ImageVector,
    @StringRes text: Int,
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    StatusChip(
        imageVector = imageVector,
        contentDescription = stringResource(text),
        text = stringResource(text),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.context.openSystemSettings(S_WIFI)
        }
    )
}
