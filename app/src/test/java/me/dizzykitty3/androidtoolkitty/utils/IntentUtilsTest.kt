package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import me.dizzykitty3.androidtoolkitty.appcomponents.ClearClipboardActivity
import me.dizzykitty3.androidtoolkitty.GOOGLE_MAPS
import me.dizzykitty3.androidtoolkitty.GOOGLE_PLAY
import me.dizzykitty3.androidtoolkitty.S_DISPLAY
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.checkOnGoogleMaps
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.checkOnMarket
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openAppDetailSettings
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openAppLanguageSetting
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openSearch
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openSystemSettings
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
    fun searchActions_useTheSelectedEngineAndVideoPlatformUris() {
        val activity = activity()

        activity.openSearch("tool kitty", SearchEngine.BING)
        val bingIntent = shadowOf(activity).nextStartedActivity
        assertEquals(Uri.parse("https://bing.com/search?q=tool kitty"), bingIntent.data)

        activity.openSearch("tool kitty", SearchEngine.ECOSIA)
        val ecosiaIntent = shadowOf(activity).nextStartedActivity
        assertEquals(Uri.parse("https://www.ecosia.org/search?q=tool kitty"), ecosiaIntent.data)

        activity.searchOnVideoPlatform("tool kitty", VideoSearchEngine.YOUTUBE)
        val youtubeIntent = shadowOf(activity).nextStartedActivity
        assertEquals(Uri.parse("https://youtube.com/results?search_query=tool kitty"), youtubeIntent.data)
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
    fun openUrl_preservesExistingSchemeAndMarketDetailsDropWhitespace() {
        val urlActivity = activity()
        urlActivity.openURL("custom://toolkitty")
        assertEquals(
            Uri.parse("custom://toolkitty"),
            shadowOf(urlActivity).nextStartedActivity.data,
        )

        val marketActivity = activity()
        marketActivity.checkOnMarket(" me. dizzy kitty ")
        assertEquals(
            Uri.parse("market://details?id=me.dizzykitty"),
            shadowOf(marketActivity).nextStartedActivity.data,
        )
    }

    @Test
    fun searchAndUrlActions_ignoreBlankInput() {
        val activity = activity()

        activity.openSearch("   ", SearchEngine.BING)
        activity.searchOnVideoPlatform("", VideoSearchEngine.BILIBILI)
        activity.openURL("\t")

        assertNull(shadowOf(activity).nextStartedActivity)
    }

    @Test
    fun appSettingsActions_useTheCurrentApplicationPackage() {
        val detailActivity = activity()
        detailActivity.openAppDetailSettings()
        val detailIntent = shadowOf(detailActivity).nextStartedActivity
        assertEquals(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, detailIntent.action)
        assertEquals(Uri.parse("package:${detailActivity.packageName}"), detailIntent.data)

        val languageActivity = activity()
        languageActivity.openAppLanguageSetting()
        val languageIntent = shadowOf(languageActivity).nextStartedActivity
        assertEquals(android.provider.Settings.ACTION_APP_LOCALE_SETTINGS, languageIntent.action)
        assertEquals(Uri.parse("package:${languageActivity.packageName}"), languageIntent.data)
    }

    @Test
    fun openSystemSettings_delegatesToTheResolvedSettingsIntent() {
        val activity = activity()

        activity.openSystemSettings(S_DISPLAY)

        assertEquals(
            android.provider.Settings.ACTION_DISPLAY_SETTINGS,
            shadowOf(activity).nextStartedActivity.action,
        )
    }

    @Test
    fun openScreen_startsTheRequestedActivityComponent() {
        val activity = activity()

        activity.openScreen(ClearClipboardActivity::class.java)

        assertEquals(
            ComponentName(activity, ClearClipboardActivity::class.java),
            shadowOf(activity).nextStartedActivity.component,
        )
    }

    @Test
    fun marketAndMapsActions_handleSearchAndBlankInput() {
        val marketActivity = activity()
        marketActivity.checkOnMarket("tool kitty", isGooglePlay = false)
        val marketIntent = shadowOf(marketActivity).nextStartedActivity
        assertEquals(Uri.parse("market://search?q=tool kitty"), marketIntent.data)
        assertNull(marketIntent.`package`)

        val blankActivity = activity()
        blankActivity.checkOnMarket("", isGooglePlay = false)
        blankActivity.checkOnGoogleMaps("", "121.5654")
        assertNull(shadowOf(blankActivity).nextStartedActivity)
    }

    private fun activity(): Activity = Robolectric.buildActivity(Activity::class.java).setup().get()
}
