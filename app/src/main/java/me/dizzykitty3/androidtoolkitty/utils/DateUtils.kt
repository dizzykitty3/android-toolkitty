package me.dizzykitty3.androidtoolkitty.utils

import androidx.annotation.StringRes
import me.dizzykitty3.androidtoolkitty.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.TimeZone

object DateUtils {

    @StringRes
    fun greeting(): Int = when (LocalTime.now().hour) {
        in 6..11 -> R.string.good_morning
        in 12..18 -> R.string.good_afternoon
        in 19..22 -> R.string.good_evening
        else -> R.string.good_night
    }

    private val daysPassed: Long
        get() = daysFromStartOfYear(LocalDate.now())

    private val totalDaysInYear: Long
        get() = daysFromStartOfYear(LocalDate.of(LocalDate.now().year, 12, 31)) + 1

    private fun daysFromStartOfYear(endDate: LocalDate): Long =
        LocalDate.of(endDate.year, 1, 1).until(endDate, ChronoUnit.DAYS)

    val yearProgress: Float
        get() = daysPassed.toFloat() / totalDaysInYear.toFloat()

    fun Float.toProgress(happyNewYear: String): String =
        if (this == 0f) {
            happyNewYear
        } else {
            (this * 100).toString().take(4).plus("%")
        }

    private val sysTimeMillis: Long
        get() = System.currentTimeMillis()

    val unixTimestampInSeconds: String
        get() = (sysTimeMillis / 1000).toString()

    val sysTimeZone: String
        get() = TimeZone.getDefault().id
}
