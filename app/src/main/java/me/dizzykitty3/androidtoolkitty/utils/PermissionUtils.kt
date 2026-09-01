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
    /**
     * Remember to use Activity Context to check/request permissions.
     * DO NOT use AppContext.check(_) which will cause a ClassCastException.
     */
    private fun Context.check(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(this, permission) != GRANTED

    private fun Activity.request(permission: Array<String>) =
        ActivityCompat.requestPermissions(this, permission, 1)

    /**
     * @return true if the app does NOT have Bluetooth permissions, false otherwise.
     */
    @CheckResult
    fun Context.noBluetoothPermission(): Boolean =
        if (OSVersion.android12()) {
            this.check(BT_CONNECT)
        } else {
            this.check(BT) || this.check(BT_ADMIN)
        }

    fun Activity.requestBluetoothPermission() =
        this.request(
            if (OSVersion.android12()) {
                arrayOf(BT_CONNECT)
            } else {
                arrayOf(BT, BT_ADMIN)
            }
        )

    /**
     * @return true if the app does NOT have Notification permissions, false otherwise.
     */
    @CheckResult
    fun Context.noNotificationPermission(): Boolean =
        this.check(POST_NOTIFICATIONS)

    fun Activity.requestNotificationPermission() =
        this.request(arrayOf(POST_NOTIFICATIONS))
}
