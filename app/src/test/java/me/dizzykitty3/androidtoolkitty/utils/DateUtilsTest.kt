package me.dizzykitty3.androidtoolkitty.utils

import me.dizzykitty3.androidtoolkitty.utils.DateUtils.toProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun toProgress_formatsNewYearAndPercentageValues() {
        assertEquals("Happy New Year", 0f.toProgress("Happy New Year"))
        assertEquals("12.3%", 0.1234f.toProgress("Happy New Year"))
        assertEquals("99.9%", 0.999f.toProgress("Happy New Year"))
    }
}
