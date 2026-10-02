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
    fun selectedWheelItemIndex_supportsNonFourItemWheels() {
        assertEquals(2, selectedWheelItemIndex(rotationDegrees = 0f, itemCount = 3))
        assertEquals(1, selectedWheelItemIndex(rotationDegrees = 120f, itemCount = 3))
        assertEquals(0, selectedWheelItemIndex(rotationDegrees = 240f, itemCount = 3))
    }

    @Test
    fun selectedWheelItemIndex_treatsNegativeAndFullCircleRotationsAsEquivalent() {
        assertEquals(
            selectedWheelItemIndex(rotationDegrees = 270f, itemCount = 4),
            selectedWheelItemIndex(rotationDegrees = -90f, itemCount = 4),
        )
        assertEquals(
            selectedWheelItemIndex(rotationDegrees = 120f, itemCount = 3),
            selectedWheelItemIndex(rotationDegrees = 840f, itemCount = 3),
        )
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

    @Test
    fun decodeWheelItems_fallsBackToDefaultsForMalformedJson() {
        assertEquals(
            listOf("Item 1", "Item 2", "Item 3", "Item 4"),
            decodeWheelItems("not valid json", "Item"),
        )
    }

    @Test
    fun decodeWheelItems_fallsBackForMissingNullAndWronglyTypedItems() {
        val expected = listOf("选项 1", "选项 2", "选项 3", "选项 4")
        for (json in listOf(
            "", "null", "{}", "[]", "{\"items\":null}",
            "{\"items\":\"Tea\"}", "{\"items\":[null]}"
        )) {
            assertEquals("Stored JSON: $json", expected, decodeWheelItems(json, "选项"))
        }
    }

    @Test
    fun decodeWheelItems_preservesDuplicatesUnicodeAndEscapedText() {
        assertEquals(
            listOf("茶🐱", "Tea", "Tea", "line\nbreak", "\"quoted\""),
            decodeWheelItems(
                """{"items":["茶🐱","Tea","Tea","line\nbreak","\"quoted\""]}""",
                "Item",
            ),
        )
    }

    @Test
    fun selectedWheelItemIndex_handlesSingleItemAndBothSidesOfSegmentBoundary() {
        for (rotation in listOf(-720f, -1f, 0f, 89.99f, 360f, 720f)) {
            assertEquals(0, selectedWheelItemIndex(rotation, 1))
        }
        assertEquals(0, selectedWheelItemIndex(90f, -1))
        assertEquals(3, selectedWheelItemIndex(-0.01f, 4))
        assertEquals(3, selectedWheelItemIndex(0f, 4))
        assertEquals(2, selectedWheelItemIndex(0.01f, 4))
        assertEquals(2, selectedWheelItemIndex(89.99f, 4))
        assertEquals(2, selectedWheelItemIndex(90f, 4))
        assertEquals(1, selectedWheelItemIndex(90.01f, 4))
    }
}
