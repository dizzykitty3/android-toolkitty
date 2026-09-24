package me.dizzykitty3.androidtoolkitty.appcomponents

import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.app.SearchManager
import me.dizzykitty3.androidtoolkitty.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ShareActivitiesTest {

    @Test
    @Config(sdk = [32])
    fun shareToClipboard_missingTextPreservesExistingClipWithoutConfirmation() {
        val context = RuntimeEnvironment.getApplication()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("existing label", "keep this text"))
        ShadowToast.reset()
        val controller = Robolectric.buildActivity(
            ShareToClipboardActivity::class.java,
            Intent(Intent.ACTION_SEND),
        )
        try {
            val activity = controller.setup().get()

            assertEquals("keep this text", clipboard.primaryClip?.getItemAt(0)?.text)
            assertEquals("existing label", clipboard.primaryClip?.description?.label)
            assertEquals(0, ShadowToast.shownToastCount())
            assertTrue(activity.isFinishing)
        } finally {
            controller.pause().stop().destroy()
            ShadowToast.reset()
        }
    }

    @Test
    fun shareActivities_preserveUnicodeMultilineTextAndSurroundingWhitespace() {
        val text = "  中文 🐱 café\n第二行 & + % #\t "
        val clipboardController = Robolectric.buildActivity(
            ShareToClipboardActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, text),
        )
        val searchController = Robolectric.buildActivity(
            ShareToSearchActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, text),
        )
        try {
            val clipboardActivity = clipboardController.setup().get()
            val searchActivity = searchController.setup().get()
            val clipboard = clipboardActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            assertEquals(text, clipboard.primaryClip?.getItemAt(0)?.text)

            val searchIntent = shadowOf(searchActivity).nextStartedActivity
            assertEquals(Intent.ACTION_WEB_SEARCH, searchIntent.action)
            assertEquals(text, searchIntent.getStringExtra(SearchManager.QUERY))
            assertNull(shadowOf(searchActivity).nextStartedActivity)
            assertTrue(clipboardActivity.isFinishing)
            assertTrue(searchActivity.isFinishing)
        } finally {
            clipboardController.pause().stop().destroy()
            searchController.pause().stop().destroy()
        }
    }

    @Test
    @Config(sdk = [32])
    fun shareToClipboard_onAndroid12L_showsCopyConfirmation() {
        ShadowToast.reset()
        val controller = Robolectric.buildActivity(
            ShareToClipboardActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, "shared text"),
        )
        try {
            val activity = controller.setup().get()
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            assertEquals("shared text", clipboard.primaryClip?.getItemAt(0)?.text)
            assertEquals(1, ShadowToast.shownToastCount())
            assertEquals(activity.getString(R.string.copied), ShadowToast.getTextOfLatestToast())
            assertTrue(activity.isFinishing)
        } finally {
            controller.pause().stop().destroy()
            ShadowToast.reset()
        }
    }

    @Test
    @Config(sdk = [33])
    fun shareToClipboard_onAndroid13_doesNotShowDuplicateCopyToast() {
        ShadowToast.reset()
        val controller = Robolectric.buildActivity(
            ShareToClipboardActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, "shared text"),
        )
        try {
            val activity = controller.setup().get()
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            assertEquals("shared text", clipboard.primaryClip?.getItemAt(0)?.text)
            assertEquals(0, ShadowToast.shownToastCount())
            assertTrue(activity.isFinishing)
        } finally {
            controller.pause().stop().destroy()
            ShadowToast.reset()
        }
    }

    @Test
    fun shareToClipboard_copiesSharedTextAndFinishes() {
        val activity = Robolectric.buildActivity(
            ShareToClipboardActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, "ToolKitty"),
        )
            .setup()
            .get()

        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        assertEquals("ToolKitty", clipboard.primaryClip?.getItemAt(0)?.text)
        assertTrue(activity.isFinishing)
    }

    @Test
    fun shareToSearch_startsGoogleWebSearchAndFinishes() {
        val activity = Robolectric.buildActivity(
            ShareToSearchActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, "compose testing"),
        )
            .setup()
            .get()

        val intent = shadowOf(activity).nextStartedActivity
        assertEquals(Intent.ACTION_WEB_SEARCH, intent.action)
        assertEquals("compose testing", intent.getStringExtra(SearchManager.QUERY))
        assertTrue(activity.isFinishing)
    }

    @Test
    fun shareActivities_ignoreMissingSharedText() {
        val clipboardActivity = Robolectric.buildActivity(
            ShareToClipboardActivity::class.java,
            Intent(Intent.ACTION_SEND),
        )
            .setup()
            .get()
        val searchActivity = Robolectric.buildActivity(
            ShareToSearchActivity::class.java,
            Intent(Intent.ACTION_SEND),
        )
            .setup()
            .get()

        assertTrue(clipboardActivity.isFinishing)
        assertTrue(searchActivity.isFinishing)
        assertNull(shadowOf(searchActivity).nextStartedActivity)
    }

    @Test
    fun shareActivities_handleEmptySharedTextWithoutLaunchingSearch() {
        val clipboardActivity = Robolectric.buildActivity(
            ShareToClipboardActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, ""),
        )
            .setup()
            .get()
        val searchActivity = Robolectric.buildActivity(
            ShareToSearchActivity::class.java,
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, ""),
        )
            .setup()
            .get()

        val clipboard = clipboardActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        assertEquals("", clipboard.primaryClip?.getItemAt(0)?.text)
        assertTrue(clipboardActivity.isFinishing)
        assertTrue(searchActivity.isFinishing)
        assertNull(shadowOf(searchActivity).nextStartedActivity)
    }
}
