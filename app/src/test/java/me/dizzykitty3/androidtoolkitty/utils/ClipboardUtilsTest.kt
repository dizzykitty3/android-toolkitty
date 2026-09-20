package me.dizzykitty3.androidtoolkitty.utils

import android.content.ClipboardManager
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}
