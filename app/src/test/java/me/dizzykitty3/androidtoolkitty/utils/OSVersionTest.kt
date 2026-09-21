package me.dizzykitty3.androidtoolkitty.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class OSVersionTest {

    @Test
    fun versionChecks_acceptEveryReleaseThroughTheConfiguredSdk() {
        assertTrue(OSVersion.android5())
        assertTrue(OSVersion.android5Point1())
        assertTrue(OSVersion.android6())
        assertTrue(OSVersion.android7())
        assertTrue(OSVersion.android8())
        assertTrue(OSVersion.android9())
        assertTrue(OSVersion.android10())
        assertTrue(OSVersion.android11())
        assertTrue(OSVersion.android12())
        assertTrue(OSVersion.android12L())
        assertTrue(OSVersion.android13())
        assertTrue(OSVersion.android14())
        assertTrue(OSVersion.android15())
    }

    @Test
    fun versionChecks_rejectReleasesNewerThanTheConfiguredSdk() {
        assertFalse(OSVersion.android16())
        assertFalse(OSVersion.android17())
    }
}
