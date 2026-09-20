package me.dizzykitty3.androidtoolkitty.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VolumeRulesTest {

    @Test
    fun presetVolume_returnsExpectedValuesForPresetIndexes() {
        assertEquals(0.0, requireNotNull(presetVolume(index = 0, maxVolume = 15)), 0.0)
        assertEquals(6.0, requireNotNull(presetVolume(index = 1, maxVolume = 15)), 0.0)
        assertEquals(9.0, requireNotNull(presetVolume(index = 2, maxVolume = 15)), 0.0)
        assertNull(presetVolume(index = 3, maxVolume = 15))
    }

    @Test
    fun selectedVolumeIndex_prioritizesPresetsThenCustomVolume() {
        assertEquals(0, selectedVolumeIndex(volume = 0, maxVolume = 15, customVolume = 33))
        assertEquals(1, selectedVolumeIndex(volume = 6, maxVolume = 15, customVolume = 33))
        assertEquals(2, selectedVolumeIndex(volume = 9, maxVolume = 15, customVolume = 33))
        assertEquals(3, selectedVolumeIndex(volume = 5, maxVolume = 15, customVolume = 33))
        assertEquals(1, selectedVolumeIndex(volume = 6, maxVolume = 15, customVolume = 40))
        assertEquals(-1, selectedVolumeIndex(volume = 7, maxVolume = 15, customVolume = 33))
    }
}
