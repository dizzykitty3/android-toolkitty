package me.dizzykitty3.androidtoolkitty.appcomponents

import android.content.ClipboardManager
import android.content.Context
import me.dizzykitty3.androidtoolkitty.utils.copyToClipboard
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ClearClipboardActivityTest {

    @Test
    fun gainingFocus_clearsExistingClipboardContentAndFinishes() {
        val activity = Robolectric.buildActivity(ClearClipboardActivity::class.java).setup().get()
        activity.copyToClipboard("temporary")

        activity.onWindowFocusChanged(true)

        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        assertFalse(clipboard.hasPrimaryClip())
        assertTrue(activity.isFinishing)
    }

    @Test
    fun gainingFocus_withAnEmptyClipboardStillFinishes() {
        val activity = Robolectric.buildActivity(ClearClipboardActivity::class.java).setup().get()
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.clearPrimaryClip()

        activity.onWindowFocusChanged(true)

        assertFalse(clipboard.hasPrimaryClip())
        assertTrue(activity.isFinishing)
    }
}
