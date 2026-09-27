package me.dizzykitty3.androidtoolkitty.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.noBluetoothPermission

fun Context.bluetoothAdapter(): BluetoothAdapter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    getSystemService<BluetoothManager>()?.adapter
} else {
    BluetoothAdapter.getDefaultAdapter()
}

@CheckResult
fun Context.isHeadsetConnected(): Boolean {
    if (noBluetoothPermission()) return false

    return bluetoothAdapter()?.getProfileConnectionState(BluetoothProfile.HEADSET) ==
        BluetoothAdapter.STATE_CONNECTED
}

@CheckResult
fun Context.headsetNotConnected(): Boolean = !isHeadsetConnected()
