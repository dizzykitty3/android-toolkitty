package me.dizzykitty3.androidtoolkitty.home

import android.bluetooth.BluetoothDevice
import org.junit.Assert.assertEquals
import org.junit.Test

class BluetoothDeviceTypeTest {

    @Test
    fun toTypeName_describesEveryKnownBluetoothDeviceType() {
        assertEquals("BT", BluetoothDevice.DEVICE_TYPE_CLASSIC.toTypeName())
        assertEquals("BLE", BluetoothDevice.DEVICE_TYPE_LE.toTypeName())
        assertEquals("Dual", BluetoothDevice.DEVICE_TYPE_DUAL.toTypeName())
    }

    @Test
    fun toTypeName_usesUnknownForUnsupportedDeviceTypes() {
        assertEquals("Unknown", BluetoothDevice.DEVICE_TYPE_UNKNOWN.toTypeName())
        assertEquals("Unknown", 99.toTypeName())
    }
}
