package me.dizzykitty3.androidtoolkitty.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import me.dizzykitty3.androidtoolkitty.BT
import me.dizzykitty3.androidtoolkitty.BT_ADMIN
import me.dizzykitty3.androidtoolkitty.BT_CONNECT
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.S_ENABLE_BLUETOOTH
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomIconPopup
import me.dizzykitty3.androidtoolkitty.uicomponents.RowDivider
import me.dizzykitty3.androidtoolkitty.uicomponents.ScrollableText
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.bluetoothAdapter
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openAppDetailSettings
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openSystemSettings
import me.dizzykitty3.androidtoolkitty.utils.OSVersion
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothDevice() {
    BaseCard(title = R.string.bluetooth_devices, icon = Icons.Outlined.Bluetooth) {
        val view = LocalView.current
        val haptic = LocalHapticFeedback.current
        var showResult by remember { mutableStateOf(false) }
        var bluetoothAdapter by remember { mutableStateOf<BluetoothAdapter?>(null) }
        var pairedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
        var size by remember { mutableIntStateOf(0) }
        val btPermissionState = rememberPermissionState(BT_CONNECT)
        val legacyBTPermissionState = rememberMultiplePermissionsState(listOf(BT, BT_ADMIN))
        var clickCount by remember { mutableIntStateOf(0) }

        if ((OSVersion.android12() && btPermissionState.status.isGranted) || (!OSVersion.android12() && legacyBTPermissionState.allPermissionsGranted)) {
            OutlinedButton(onClick = {
                // Get system service
                bluetoothAdapter = view.context.bluetoothAdapter()
                if (bluetoothAdapter == null) {
                    view.showSnackbar(R.string.no_bluetooth_adapter_available)
                    return@OutlinedButton
                }

                // Show current device name, paired devices' name and MAC address
                if (bluetoothAdapter!!.isEnabled) {
                    pairedDevices = bluetoothAdapter!!.bondedDevices.sortedBy { it.name }
                    size = pairedDevices.size
                    showResult = true
                    return@OutlinedButton
                }

                // When Bluetooth is OFF
                view.context.openSystemSettings(S_ENABLE_BLUETOOTH)
            }) {
                Icon(
                    imageVector = Icons.Outlined.BluetoothConnected,
                    contentDescription = stringResource(R.string.show_paired_devices),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                SpacerPadding()
                Text(stringResource(if (showResult) R.string.refresh else R.string.show_paired_devices))
            }
        } else {
            Column {
                Text(stringResource(R.string.bluetooth_connect))
                Button(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    clickCount++
                    if (OSVersion.android12()) {
                        btPermissionState.launchPermissionRequest()
                    } else {
                        legacyBTPermissionState.launchMultiplePermissionRequest()
                    }
                }) {
                    Text(stringResource(R.string.request_permission))
                }

                if (clickCount >= 2) {
                    RowDivider()
                    ManuallyGrant()
                }
            }
        }

        if (showResult) {
            Text(stringResource(R.string.current_device))
            ScrollableText("${bluetoothAdapter?.name}\n")

            if (size == 0) {
                Text(stringResource(R.string.no_paired_devices))
            } else {
                Text(stringResource(R.string.paired_devices))

                pairedDevices.forEach { device ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScrollableText(device.name ?: stringResource(R.string.unknown_device))
                        SpacerPadding()
                        CustomIconPopup(device.type.toTypeName(), device.address)
                    }
                }
            }
        }
    }
}

@Composable
private fun ManuallyGrant() {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    Text(stringResource(R.string.missed_sys_popup))
    TextButton({
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        view.context.openAppDetailSettings()
    }) {
        Text(
            stringResource(R.string.go_to_settings),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * @see android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC
 */
private fun Int.toTypeName(): String = when (this) {
    1 -> "BT"
    2 -> "BLE"
    3 -> "Dual"
    else -> "Unknown"
}
