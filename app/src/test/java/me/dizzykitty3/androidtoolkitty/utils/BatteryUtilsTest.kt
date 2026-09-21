package me.dizzykitty3.androidtoolkitty.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BatteryUtilsTest {

    @Test
    fun batteryLevel_calculatesPercentageFromBatteryBroadcast() {
        val context = batteryContext(level = 45, scale = 60)

        assertEquals(75, context.batteryLevel())
    }

    @Test
    fun batteryLevel_returnsUnavailableWhenBroadcastDataIsMissingOrInvalid() {
        assertEquals(-1, batteryContext(level = null, scale = 100).batteryLevel())
        assertEquals(-1, batteryContext(level = 50, scale = 0).batteryLevel())
        assertEquals(-1, batteryContext(level = 50, scale = -1).batteryLevel())
    }

    @Test
    fun batteryLevel_returnsUnavailableWhenTheSystemHasNoBatteryBroadcast() {
        val context = object : ContextWrapper(null) {
            override fun registerReceiver(
                receiver: BroadcastReceiver?,
                filter: IntentFilter?,
            ): Intent? = null
        }

        assertEquals(-1, context.batteryLevel())
    }

    @Test
    fun batteryLevel_keepsZeroAndTruncatesFractionalPercentages() {
        assertEquals(0, batteryContext(level = 0, scale = 100).batteryLevel())
        assertEquals(33, batteryContext(level = 1, scale = 3).batteryLevel())
    }

    private fun batteryContext(level: Int?, scale: Int?): Context = object : ContextWrapper(null) {
        override fun registerReceiver(
            receiver: BroadcastReceiver?,
            filter: IntentFilter?,
        ): Intent? = Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            level?.let { putExtra(BatteryManager.EXTRA_LEVEL, it) }
            scale?.let { putExtra(BatteryManager.EXTRA_SCALE, it) }
        }
    }
}
