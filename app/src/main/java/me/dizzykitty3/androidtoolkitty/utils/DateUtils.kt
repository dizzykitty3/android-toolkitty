package me.dizzykitty3.androidtoolkitty.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.TimeZone

object DateUtils {
    fun greeting(): String = when (LocalTime.now().hour) {
        in 6..11 -> "Good morning"
        in 12..18 -> "Good afternoon"
        in 19..22 -> "Good evening"
        else -> "Good night"
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
            (this * 100).toString().substring(0, 4).plus("%")
        }

    private val sysTimeMillis: Long
        get() = System.currentTimeMillis()

    val unixTimestampInSeconds: String
        get() = (sysTimeMillis / 1000).toString()

    val sysTimeZone: String
        get() = TimeZone.getDefault().id
}
