package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

private const val BATTERY_VALUE_UNAVAILABLE = -1

fun Context.batteryLevel(): Int {
    val batteryIntent = registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    )
    val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, BATTERY_VALUE_UNAVAILABLE)
        ?: BATTERY_VALUE_UNAVAILABLE
    val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, BATTERY_VALUE_UNAVAILABLE)
        ?: BATTERY_VALUE_UNAVAILABLE

    if (level == BATTERY_VALUE_UNAVAILABLE || scale <= 0) return BATTERY_VALUE_UNAVAILABLE

    return (level.toFloat() / scale.toFloat() * 100).toInt()
}
