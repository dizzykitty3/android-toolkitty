package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class VolumeSlotNavigationTest {
    @Test
    fun editVolumeSlot_passesEachSlotToTheEditorAndRejectsInvalidIndexes() {
        val context = RuntimeEnvironment.getApplication()
        for (slot in 0..2) {
            context.editVolumeSlot(slot)
            val intent = shadowOf(context).nextStartedActivity
            assertEquals(VolumeCustomizeActivity::class.java.name, intent.component?.className)
            assertEquals(slot, intent.getIntExtra(EXTRA_VOLUME_SLOT, -1))
            assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
        }
        context.editVolumeSlot(-1)
        context.editVolumeSlot(3)
        assertNull(shadowOf(context).nextStartedActivity)
    }
}
