package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import me.dizzykitty3.androidtoolkitty.BT_CONNECT
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BluetoothUtilsTest {

    @Test
    fun headsetStatus_treatsMissingBluetoothPermissionAsNotConnected() {
        val context: Context = RuntimeEnvironment.getApplication()

        assertFalse(context.isHeadsetConnected())
        assertTrue(context.headsetNotConnected())
    }

    @Test
    fun bluetoothAdapter_isAvailableWhenBluetoothPermissionIsGranted() {
        val context: Context = RuntimeEnvironment.getApplication()
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(BT_CONNECT)

        assertNotNull(context.bluetoothAdapter())
    }
}
