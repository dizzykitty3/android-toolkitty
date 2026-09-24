package me.dizzykitty3.androidtoolkitty.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import me.dizzykitty3.androidtoolkitty.BT_CONNECT
import org.junit.Assert.assertEquals
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
    fun headsetStatus_tracksConnectionTransitionsWithoutCachingOldState() {
        val context: Context = RuntimeEnvironment.getApplication()
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(BT_CONNECT)
        val adapter = shadowOf(requireNotNull(context.bluetoothAdapter()))
        val states = listOf(
            BluetoothProfile.STATE_DISCONNECTED,
            BluetoothProfile.STATE_CONNECTING,
            BluetoothProfile.STATE_CONNECTED,
            BluetoothProfile.STATE_DISCONNECTING,
            BluetoothProfile.STATE_DISCONNECTED,
        )

        states.forEach { state ->
            adapter.setProfileConnectionState(BluetoothProfile.HEADSET, state)
            assertEquals(
                "Headset state $state",
                state == BluetoothProfile.STATE_CONNECTED,
                context.isHeadsetConnected(),
            )
            assertEquals(
                "Inverse headset state $state",
                state != BluetoothProfile.STATE_CONNECTED,
                context.headsetNotConnected(),
            )
        }
    }

    @Test
    fun headsetStatus_doesNotTreatAnA2dpOnlyConnectionAsAHeadsetConnection() {
        val context: Context = RuntimeEnvironment.getApplication()
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(BT_CONNECT)
        val adapter = shadowOf(requireNotNull(context.bluetoothAdapter()))
        adapter.setProfileConnectionState(BluetoothProfile.HEADSET, BluetoothProfile.STATE_DISCONNECTED)
        adapter.setProfileConnectionState(BluetoothProfile.A2DP, BluetoothProfile.STATE_CONNECTED)

        assertFalse(context.isHeadsetConnected())
        assertTrue(context.headsetNotConnected())
    }

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

    @Test
    fun headsetStatus_reportsConnectedWhenTheHeadsetProfileIsConnected() {
        val context: Context = RuntimeEnvironment.getApplication()
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(BT_CONNECT)
        val adapter = requireNotNull(context.bluetoothAdapter())
        shadowOf(adapter).setProfileConnectionState(
            BluetoothProfile.HEADSET,
            BluetoothAdapter.STATE_CONNECTED,
        )

        assertTrue(context.isHeadsetConnected())
        assertFalse(context.headsetNotConnected())
    }
}
