package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.app.Application
import me.dizzykitty3.androidtoolkitty.BT_CONNECT
import me.dizzykitty3.androidtoolkitty.POST_NOTIFICATIONS
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.noBluetoothPermission
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.noNotificationPermission
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.requestBluetoothPermission
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.requestNotificationPermission
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PermissionUtilsTest {

    @Test
    fun notificationPermission_reflectsGrantedAndDeniedStates() {
        val application = application()

        shadowOf(application).denyPermissions(POST_NOTIFICATIONS)
        assertTrue(application.noNotificationPermission())

        shadowOf(application).grantPermissions(POST_NOTIFICATIONS)
        assertFalse(application.noNotificationPermission())
    }

    @Test
    fun bluetoothPermission_reflectsGrantedAndDeniedStatesOnAndroid12AndLater() {
        val application = application()

        shadowOf(application).denyPermissions(BT_CONNECT)
        assertTrue(application.noBluetoothPermission())

        shadowOf(application).grantPermissions(BT_CONNECT)
        assertFalse(application.noBluetoothPermission())
    }

    @Test
    fun requestNotificationPermission_requestsOnlyNotificationPermission() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

        activity.requestNotificationPermission()

        val request = requireNotNull(shadowOf(activity).lastRequestedPermission)
        assertEquals(1, request.requestCode)
        assertArrayEquals(arrayOf(POST_NOTIFICATIONS), request.requestedPermissions)
    }

    @Test
    fun requestBluetoothPermission_requestsBluetoothConnectOnModernAndroid() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

        activity.requestBluetoothPermission()

        val request = requireNotNull(shadowOf(activity).lastRequestedPermission)
        assertEquals(1, request.requestCode)
        assertArrayEquals(arrayOf(BT_CONNECT), request.requestedPermissions)
    }

    private fun application(): Application = RuntimeEnvironment.getApplication()
}
