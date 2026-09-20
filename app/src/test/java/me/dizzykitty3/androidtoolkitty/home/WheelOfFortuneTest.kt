package me.dizzykitty3.androidtoolkitty.home

import org.junit.Assert.assertEquals
import org.junit.Test

class WheelOfFortuneTest {

    @Test
    fun selectedWheelItemIndex_mapsRotationsToTheExpectedSegment() {
        assertEquals(3, selectedWheelItemIndex(rotationDegrees = 0f, itemCount = 4))
        assertEquals(2, selectedWheelItemIndex(rotationDegrees = 90f, itemCount = 4))
        assertEquals(0, selectedWheelItemIndex(rotationDegrees = 270f, itemCount = 4))
        assertEquals(0, selectedWheelItemIndex(rotationDegrees = 630f, itemCount = 4))
        assertEquals(0, selectedWheelItemIndex(rotationDegrees = 0f, itemCount = 0))
    }

    @Test
    fun decodeWheelItems_usesSavedItemsOrGeneratesDefaults() {
        assertEquals(
            listOf("Tea", "Coffee"),
            decodeWheelItems("{\"items\":[\"Tea\",\"Coffee\"]}", "Item"),
        )
        assertEquals(
            listOf("Item 1", "Item 2", "Item 3", "Item 4"),
            decodeWheelItems("{\"items\":[]}", "Item"),
        )
        assertEquals(
            listOf("Item 1", "Item 2", "Item 3", "Item 4"),
            decodeWheelItems(null, "Item"),
        )
    }
}
