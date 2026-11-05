package me.dizzykitty3.androidtoolkitty.ui.home

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import me.dizzykitty3.androidtoolkitty.utils.bluetoothAdapter
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.noBluetoothPermission
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.BluetoothConnected
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import kotlinx.coroutines.awaitCancellation
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.S_BATTERY
import me.dizzykitty3.androidtoolkitty.S_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.S_WIFI
import me.dizzykitty3.androidtoolkitty.uicomponents.SectionSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openSystemShortcut
import me.dizzykitty3.androidtoolkitty.utils.NetworkUtil
import me.dizzykitty3.androidtoolkitty.utils.batteryLevel
import me.dizzykitty3.androidtoolkitty.utils.networkState
import timber.log.Timber

@Composable
fun HomeStatusBar(isTablet: Boolean = false) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(context.batteryLevel()) }

    LaunchedEffect(context) {
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
        StatusChip(
            imageVector = Icons.Outlined.BatteryStd,
            contentDescription = stringResource(R.string.battery),
            text = "$batteryLevel%",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                view.context.openSystemShortcut(S_BATTERY)
            }
        )
        SpacerPadding()
        SpacerPadding()
        NetworkState()
        BluetoothState()

    }
    SectionSpacePadding()
}

@Composable
private fun StatusChip(
    imageVector: ImageVector,
    contentDescription: String?,
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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

    LaunchedEffect(context) {
        fun refreshNetworkState() {
            networkState = context.networkState()
        }

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
    }

    when (networkState) {
        NetworkUtil.STATE_CODE_WIFI -> NetworkStateIcon(Icons.Outlined.Wifi, R.string.wifi)
        NetworkUtil.STATE_CODE_MOBILE -> NetworkStateIcon(
            Icons.Outlined.NetworkCell,
            R.string.cellular
        )

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
            view.context.openSystemShortcut(S_WIFI)
        }
    )
}


private enum class BluetoothStatus { OFF, ON, CONNECTED, UNKNOWN }

private fun Context.bluetoothStatus(): BluetoothStatus {
    if (noBluetoothPermission()) return BluetoothStatus.UNKNOWN
    return try {
        val adapter = bluetoothAdapter()
        if (adapter == null || !adapter.isEnabled) BluetoothStatus.OFF
        else {
            // Include input devices and PAN connections as well as audio profiles.
            val profiles = mutableListOf(BluetoothProfile.HEADSET, BluetoothProfile.A2DP, 4, 5)
            if (Build.VERSION.SDK_INT >= 28) profiles += BluetoothProfile.HEARING_AID
            if (Build.VERSION.SDK_INT >= 33) profiles += BluetoothProfile.LE_AUDIO
            val gattConnected = contextGattConnected()
            if (gattConnected || profiles.any { adapter.getProfileConnectionState(it) == BluetoothProfile.STATE_CONNECTED })
                BluetoothStatus.CONNECTED else BluetoothStatus.ON
        }
    } catch (_: SecurityException) {
        BluetoothStatus.UNKNOWN
    }
}

private fun Context.contextGattConnected(): Boolean {
    val manager = getSystemService<BluetoothManager>() ?: return false
    return manager.getConnectedDevices(BluetoothProfile.GATT).isNotEmpty() ||
        manager.getConnectedDevices(BluetoothProfile.GATT_SERVER).isNotEmpty()
}

@Composable
private fun BluetoothState() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current
    var status by remember(context) { mutableStateOf(context.bluetoothStatus()) }

    DisposableEffect(context, lifecycleOwner) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                status = context.bluetoothStatus()
                if (intent.action == BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED &&
                    intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1) ==
                    BluetoothAdapter.STATE_CONNECTED) {
                    status = BluetoothStatus.CONNECTED
                }
            }
        }
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED).apply {
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) status = context.bluetoothStatus()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            context.unregisterReceiver(receiver)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    if (status == BluetoothStatus.UNKNOWN) return

    SpacerPadding()
    SpacerPadding()
    val (icon, label) = when (status) {
        BluetoothStatus.OFF -> Icons.Outlined.BluetoothDisabled to R.string.bluetooth_status_off
        BluetoothStatus.ON -> Icons.Outlined.Bluetooth to R.string.bluetooth_status_on
        BluetoothStatus.CONNECTED -> Icons.Outlined.BluetoothConnected to R.string.bluetooth_status_connected
        BluetoothStatus.UNKNOWN -> return
    }
    StatusChip(icon, stringResource(label), stringResource(label)) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        context.openSystemShortcut(S_BLUETOOTH)
    }
}
