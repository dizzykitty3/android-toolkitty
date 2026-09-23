package me.dizzykitty3.androidtoolkitty.utils

import android.content.ClipboardManager
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ClipboardUtilsTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    fun copyToClipboard_writesPlainTextToTheSystemClipboard() {
        context.copyToClipboard("ToolKitty")

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        assertTrue(clipboard.hasPrimaryClip())
        assertEquals("ToolKitty", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun clearClipboard_clearsExistingContentAndReportsWhetherAnythingWasCleared() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.clearPrimaryClip()
        assertFalse(context.clearClipboard())

        context.copyToClipboard("temporary")
        assertTrue(context.clearClipboard())
        assertFalse(clipboard.hasPrimaryClip())
    }

    @Test
    @Config(sdk = [27])
    fun clearClipboard_onAndroid81_replacesAllItemsWithEmptyText() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("private", "first secret")
        clip.addItem(android.content.ClipData.Item("second secret"))
        clipboard.setPrimaryClip(clip)

        assertTrue(context.clearClipboard())

        val cleared = requireNotNull(clipboard.primaryClip)
        assertEquals(1, cleared.itemCount)
        assertEquals("", cleared.getItemAt(0).text.toString())
        assertEquals("", cleared.description.label.toString())
    }

    @Test
    @Config(sdk = [27])
    fun clearClipboard_onAndroid81_leavesAnEmptyClipboardUntouched() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        assertNull(clipboard.primaryClip)

        assertFalse(context.clearClipboard())

        assertNull(clipboard.primaryClip)
    }
}
