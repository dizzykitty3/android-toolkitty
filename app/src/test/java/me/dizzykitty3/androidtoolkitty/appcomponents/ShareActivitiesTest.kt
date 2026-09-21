package me.dizzykitty3.androidtoolkitty.appcomponents

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.app.SearchManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ShareActivitiesTest {

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
}
