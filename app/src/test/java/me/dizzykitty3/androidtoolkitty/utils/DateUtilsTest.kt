package me.dizzykitty3.androidtoolkitty.utils

import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.DateUtils.toProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
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
        assertTrue(
            DateUtils.greeting() in setOf(
                R.string.good_morning,
                R.string.good_afternoon,
                R.string.good_evening,
                R.string.good_night,
            ),
        )
        assertTrue(DateUtils.yearProgress >= 0f)
        assertTrue(DateUtils.yearProgress < 1f)
        assertTrue(DateUtils.unixTimestampInSeconds.toLong() > 0L)
        assertEquals(TimeZone.getDefault().id, DateUtils.sysTimeZone)
    }
}
