package me.dizzykitty3.androidtoolkitty.home

import me.dizzykitty3.androidtoolkitty.utils.effectiveVolumeSlots
import me.dizzykitty3.androidtoolkitty.utils.volumeSlotStep
import me.dizzykitty3.androidtoolkitty.utils.validateVolumeSlot
import me.dizzykitty3.androidtoolkitty.utils.VolumeSaveResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VolumeRulesTest {
    @Test
    fun selection_matchesActualStepsAndKeepsOffIndependent() {
        val slots = listOf(20, 40, 80)
        assertEquals(0, selectedVolumeIndex(0, 15, slots))
        assertEquals(1, selectedVolumeIndex(3, 15, slots))
        assertEquals(2, selectedVolumeIndex(6, 15, slots))
        assertEquals(3, selectedVolumeIndex(12, 15, slots))
        assertEquals(-1, selectedVolumeIndex(9, 15, slots))
        assertEquals(-1, selectedVolumeIndex(-1, 15, slots))
        assertEquals(-1, selectedVolumeIndex(16, 15, slots))
        assertEquals(-1, selectedVolumeIndex(1, 0, slots))
    }

    @Test
    fun validation_rejectsDifferentPercentagesWithTheSameStepButAllowsSelfEdit() {
        assertEquals(VolumeSaveResult.DUPLICATE, validateVolumeSlot(1, 41, 15, listOf(40, null, null)))
        assertEquals(VolumeSaveResult.SAVED, validateVolumeSlot(0, 41, 15, listOf(40, null, null)))
        assertEquals(VolumeSaveResult.SAVED, validateVolumeSlot(1, 50, 15, listOf(40, null, null)))
        assertEquals(VolumeSaveResult.INVALID, validateVolumeSlot(3, 40, 15, emptyList()))
        assertEquals(VolumeSaveResult.INVALID, validateVolumeSlot(-1, 40, 15, emptyList()))
    }

    @Test
    fun invalidRangesAndRoundedZeroAreNotUsableSlots() {
        for (percent in listOf(null, -1, 0, 101, Int.MAX_VALUE)) {
            assertNull(volumeSlotStep(percent, 15))
        }
        assertNull(volumeSlotStep(100, 0))
        assertNull(volumeSlotStep(100, -1))
        assertNull(volumeSlotStep(4, 10))
        assertEquals(1, volumeSlotStep(5, 10))
        assertEquals(15, volumeSlotStep(100, 15))
    }

    @Test
    fun changedRange_disablesCollisionsWithoutReplacingStoredPercentages() {
        val slots = listOf(40, 41, 80)
        assertEquals(slots, effectiveVolumeSlots(slots, 100))
        assertEquals(listOf(40, null, 80), effectiveVolumeSlots(slots, 15))
        assertEquals(1, selectedVolumeIndex(6, 15, slots))
        assertEquals(listOf(null, null, null), effectiveVolumeSlots(slots, 0))
        assertEquals(listOf(null, null, null), effectiveVolumeSlots(emptyList(), 15))
        assertEquals(listOf(null, 60, null), effectiveVolumeSlots(listOf(40, 60, 80), 1))
    }
}
