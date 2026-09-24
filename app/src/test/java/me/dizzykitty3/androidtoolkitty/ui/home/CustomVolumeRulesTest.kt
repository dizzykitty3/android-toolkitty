package me.dizzykitty3.androidtoolkitty.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class CustomVolumeRulesTest {

    @Test
    fun label_includesPercentageCurrentIndexAndMaximum() {
        assertEquals("40% -> 6/15", customVolumeLabel(40f, 15))
        assertEquals("0% -> 0/15", customVolumeLabel(0f, 15))
        assertEquals("100% -> 15/15", customVolumeLabel(100f, 15))
        assertEquals("33% -> 2/7", customVolumeLabel(33.3f, 7))
    }

    @Test
    fun index_roundsAtTheFirstAudibleStepBoundary() {
        assertEquals(0, customVolumeIndex(4.9f, 10))
        assertEquals(1, customVolumeIndex(5f, 10))
        assertEquals(1, customVolumeIndex(5.1f, 10))
    }

    @Test
    fun index_usesTheSelectedPercentageBeforeRoundingTheDisplay() {
        assertEquals("5% -> 0/10", customVolumeLabel(4.9f, 10))
        assertEquals(0, customVolumeIndex(4.9f, 10))
    }

    @Test
    fun unavailableMaximum_keepsTheComputedVolumeAtZero() {
        assertEquals(0, customVolumeIndex(100f, 0))
        assertEquals("100% -> 0/0", customVolumeLabel(100f, 0))
    }
}
