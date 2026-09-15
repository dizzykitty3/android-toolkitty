package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.content.Context
import androidx.annotation.CheckResult
import androidx.core.app.ActivityCompat
import me.dizzykitty3.androidtoolkitty.BT
import me.dizzykitty3.androidtoolkitty.BT_ADMIN
import me.dizzykitty3.androidtoolkitty.BT_CONNECT
import me.dizzykitty3.androidtoolkitty.GRANTED
import me.dizzykitty3.androidtoolkitty.POST_NOTIFICATIONS

object PermissionUtils {
    private const val PERMISSION_REQUEST_CODE = 1

    /**
     * Remember to use Activity Context to check/request permissions.
     * DO NOT use AppContext.isPermissionDenied(_) which will cause a ClassCastException.
     */
    private fun Context.isPermissionDenied(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(this, permission) != GRANTED

    private fun Activity.request(permission: Array<String>) =
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE)

    /**
     * @return true if the app does NOT have Bluetooth permissions, false otherwise.
     */
    @CheckResult
    fun Context.noBluetoothPermission(): Boolean =
        bluetoothPermissions().any { isPermissionDenied(it) }

    fun Activity.requestBluetoothPermission() =
        request(bluetoothPermissions())

    private fun bluetoothPermissions(): Array<String> =
        if (OSVersion.android12()) arrayOf(BT_CONNECT) else arrayOf(BT, BT_ADMIN)

    /**
     * @return true if the app does NOT have Notification permissions, false otherwise.
     */
    @CheckResult
    fun Context.noNotificationPermission(): Boolean =
        isPermissionDenied(POST_NOTIFICATIONS)

    fun Activity.requestNotificationPermission() =
        this.request(arrayOf(POST_NOTIFICATIONS))
}
