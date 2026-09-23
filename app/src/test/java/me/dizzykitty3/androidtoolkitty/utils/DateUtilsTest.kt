package me.dizzykitty3.androidtoolkitty.utils

import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.DateUtils.toProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.TimeZone

class DateUtilsTest {

    @Test
    fun toProgress_formatsNewYearAndPercentageValues() {
        assertEquals("Happy New Year", 0f.toProgress("Happy New Year"))
        assertEquals("12.3%", 0.1234f.toProgress("Happy New Year"))
        assertEquals("99.9%", 0.999f.toProgress("Happy New Year"))
    }

    @Test
    fun runtimeDateValues_stayWithinTheirExpectedContracts() {
        assertTrue(DateUtils.unixTimestampInSeconds.toLong() > 0L)
        assertEquals(TimeZone.getDefault().id, DateUtils.sysTimeZone)
    }

    @Test
    fun greeting_changesAtEachTimeBoundary() {
        val cases = listOf(
            LocalTime.MIDNIGHT to R.string.good_night,
            LocalTime.of(5, 59, 59) to R.string.good_night,
            LocalTime.of(6, 0) to R.string.good_morning,
            LocalTime.of(11, 59, 59) to R.string.good_morning,
            LocalTime.NOON to R.string.good_afternoon,
            LocalTime.of(18, 59, 59) to R.string.good_afternoon,
            LocalTime.of(19, 0) to R.string.good_evening,
            LocalTime.of(22, 59, 59) to R.string.good_evening,
            LocalTime.of(23, 0) to R.string.good_night,
            LocalTime.MAX to R.string.good_night,
        )
        cases.forEach { (time, expected) ->
            assertEquals("Greeting at $time", expected, DateUtils.greeting(time))
        }
    }

    @Test
    fun yearProgress_countsCompletedDaysAndResetsAtNewYear() {
        assertEquals(0f, DateUtils.yearProgress(LocalDate.of(2023, 1, 1)), 0f)
        assertEquals(1f / 365f, DateUtils.yearProgress(LocalDate.of(2023, 1, 2)), 0.000001f)
        assertEquals(364f / 365f, DateUtils.yearProgress(LocalDate.of(2023, 12, 31)), 0.000001f)
        assertEquals(0f, DateUtils.yearProgress(LocalDate.of(2024, 1, 1)), 0f)
    }

    @Test
    fun yearProgress_accountsForLeapDaysAndCenturyExceptions() {
        assertEquals(59f / 365f, DateUtils.yearProgress(LocalDate.of(2023, 3, 1)), 0.000001f)
        assertEquals(59f / 366f, DateUtils.yearProgress(LocalDate.of(2024, 2, 29)), 0.000001f)
        assertEquals(60f / 366f, DateUtils.yearProgress(LocalDate.of(2024, 3, 1)), 0.000001f)
        assertEquals(365f / 366f, DateUtils.yearProgress(LocalDate.of(2024, 12, 31)), 0.000001f)
        assertEquals(60f / 366f, DateUtils.yearProgress(LocalDate.of(2000, 3, 1)), 0.000001f)
        assertEquals(59f / 365f, DateUtils.yearProgress(LocalDate.of(2100, 3, 1)), 0.000001f)
    }
}
