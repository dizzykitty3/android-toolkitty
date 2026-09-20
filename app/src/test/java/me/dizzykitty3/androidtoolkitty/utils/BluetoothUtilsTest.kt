package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
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
}
