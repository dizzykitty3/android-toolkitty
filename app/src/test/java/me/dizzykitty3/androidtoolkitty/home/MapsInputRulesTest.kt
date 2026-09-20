package me.dizzykitty3.androidtoolkitty.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapsInputRulesTest {

    @Test
    fun sanitizeCoordinateInput_keepsOneLeadingMinusAndDecimalPoint() {
        assertEquals("-12.34", sanitizeCoordinateInput("-12..3north4"))
        assertEquals("12", sanitizeCoordinateInput("+12"))
        assertEquals("", sanitizeCoordinateInput("latitude"))
    }

    @Test
    fun coordinateSuffixes_andValidationRespectGeographicBounds() {
        assertEquals("N", "90".getLatitudeSuffix())
        assertEquals("S", "-90".getLatitudeSuffix())
        assertEquals("", "90.1".getLatitudeSuffix())
        assertEquals("E", "180".getLongitudeSuffix())
        assertEquals("W", "-180".getLongitudeSuffix())
        assertTrue("181".hasInvalidLongitude())
        assertTrue("invalid".hasInvalidLatitude())
        assertFalse("".hasInvalidLatitude())
        assertTrue("0".hasInvalidLatitude())
    }
}
