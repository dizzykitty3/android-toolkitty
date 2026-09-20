package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import me.dizzykitty3.androidtoolkitty.GOOGLE_MAPS
import me.dizzykitty3.androidtoolkitty.GOOGLE_PLAY
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.checkOnGoogleMaps
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.checkOnMarket
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openSearch
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openURL
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.searchOnVideoPlatform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class IntentUtilsTest {

    @Test
    fun openSearch_usesWebSearchIntentForGoogle() {
        val activity = activity()

        activity.openSearch("tool kitty", SearchEngine.GOOGLE)

        val intent = shadowOf(activity).nextStartedActivity
        assertEquals(Intent.ACTION_WEB_SEARCH, intent.action)
        assertEquals("tool kitty", intent.getStringExtra(SearchManager.QUERY))
    }

    @Test
    fun openSearch_usesSelectedSearchEngineUrl() {
        val activity = activity()

        activity.openSearch("tool kitty", SearchEngine.DUCKDUCKGO)

        val intent = shadowOf(activity).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(Uri.parse("https://duckduckgo.com/?q=tool kitty"), intent.data)
    }

    @Test
    fun videoSearch_marketAndMapsUseTheirExpectedUrisAndPackages() {
        val activity = activity()

        activity.searchOnVideoPlatform("tool kitty", VideoSearchEngine.BILIBILI)
        val videoIntent = shadowOf(activity).nextStartedActivity
        assertEquals(Uri.parse("bilibili://search?keyword=tool kitty"), videoIntent.data)

        activity.checkOnMarket("me.dizzykitty3.androidtoolkitty")
        val marketIntent = shadowOf(activity).nextStartedActivity
        assertEquals(Uri.parse("market://details?id=me.dizzykitty3.androidtoolkitty"), marketIntent.data)
        assertEquals(GOOGLE_PLAY, marketIntent.`package`)

        activity.checkOnGoogleMaps("25.03", "121.56")
        val mapsIntent = shadowOf(activity).nextStartedActivity
        assertEquals(Uri.parse("geo:25.03,121.56?q=25.03,121.56"), mapsIntent.data)
        assertEquals(GOOGLE_MAPS, mapsIntent.`package`)
    }

    @Test
    fun openUrl_addsHttpsSchemeBeforeLaunchingViewIntent() {
        val activity = activity()

        activity.openURL("android-toolkitty.dev")

        val intent = shadowOf(activity).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(Uri.parse("https://android-toolkitty.dev"), intent.data)
    }

    @Test
    fun searchAndUrlActions_ignoreBlankInput() {
        val activity = activity()

        activity.openSearch("   ", SearchEngine.BING)
        activity.searchOnVideoPlatform("", VideoSearchEngine.BILIBILI)
        activity.openURL("\t")

        assertNull(shadowOf(activity).nextStartedActivity)
    }

    private fun activity(): Activity = Robolectric.buildActivity(Activity::class.java).setup().get()
}
