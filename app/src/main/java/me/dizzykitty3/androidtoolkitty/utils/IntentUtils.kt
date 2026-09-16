package me.dizzykitty3.androidtoolkitty.utils

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri
import me.dizzykitty3.androidtoolkitty.GOOGLE_MAPS
import me.dizzykitty3.androidtoolkitty.GOOGLE_PLAY
import me.dizzykitty3.androidtoolkitty.PACKAGE
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.dropSpaces
import me.dizzykitty3.androidtoolkitty.utils.ToastUtils.showToast
import me.dizzykitty3.androidtoolkitty.utils.URLUtils.addURLScheme
import timber.log.Timber

object IntentUtils {
    private const val BILIBILI_SEARCH_URI_PREFIX = "bilibili://search?keyword="
    private const val BILIBILI_SEARCH_WEB_PREFIX = "https://m.bilibili.com/search?keyword="
    private const val BING_SEARCH_PREFIX = "https://bing.com/search?q="
    private const val DUCKDUCKGO_SEARCH_PREFIX = "https://duckduckgo.com/?q="
    private const val ECOSIA_SEARCH_PREFIX = "https://www.ecosia.org/search?q="
    private const val YOUTUBE_SEARCH_PREFIX = "https://youtube.com/results?search_query="

    private fun SearchEngine.buildSearchUrl(searchQuery: String): String? = when (this) {
        SearchEngine.GOOGLE -> null
        SearchEngine.BING -> BING_SEARCH_PREFIX + searchQuery
        SearchEngine.DUCKDUCKGO -> DUCKDUCKGO_SEARCH_PREFIX + searchQuery
        SearchEngine.ECOSIA -> ECOSIA_SEARCH_PREFIX + searchQuery
    }

    private fun VideoSearchEngine.buildSearchUri(searchQuery: String): Uri = when (this) {
        VideoSearchEngine.YOUTUBE -> (YOUTUBE_SEARCH_PREFIX + searchQuery).toUri()
        VideoSearchEngine.BILIBILI -> (BILIBILI_SEARCH_URI_PREFIX + searchQuery).toUri()
    }
    // Didn't use StartActivity as the name because a custom extension function is needed.
    private fun Context.launch(intent: Intent) {
        var msg: String

        try {
            Timber.d("startActivity")
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            this.startActivity(intent)
            return
        } catch (e: ActivityNotFoundException) {
            val data = intent.dataString
            if (data?.startsWith(BILIBILI_SEARCH_URI_PREFIX) == true) {
                // Handle bilibili search
                this.openURL(
                    BILIBILI_SEARCH_WEB_PREFIX + data.removePrefix(BILIBILI_SEARCH_URI_PREFIX)
                )
                return
            } else {
                Timber.e("brand = ${StringUtils.manufacturer}\nintent = ${intent}\n$e")
                msg = this.getString(R.string.oem_removed, StringUtils.manufacturer)
            }
        }

        when (intent.`package`) {
            GOOGLE_PLAY -> {
                Timber.i("Google Play not installed")
                msg = this.getString(R.string.google_play_not_installed)
            }

            GOOGLE_MAPS -> {
                Timber.i("Google Maps not installed")
                msg = this.getString(R.string.google_maps_not_installed)
                checkOnMarket(GOOGLE_MAPS)
            }
        }

        this.showToast(msg)
    }

    private fun Context.launchView(uri: Uri, targetPackage: String? = null) {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            if (targetPackage != null) setPackage(targetPackage)
        }
        launch(intent)
    }

    fun Context.openScreen(screen: Class<*>) {
        launch(Intent(this, screen))
    }

    fun Context.openSearch(
        searchQuery: String,
        searchEngine: SearchEngine = SearchEngine.GOOGLE,
    ) {
        if (searchQuery.isBlank()) return

        Timber.d("openSearch, searchEngine = $searchEngine")
        val searchUrl = searchEngine.buildSearchUrl(searchQuery)
        if (searchUrl != null) {
            this.openURL(searchUrl)
        } else {
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY, searchQuery)
            this.launch(intent)
        }
    }

    fun Context.searchOnVideoPlatform(
        searchQuery: String,
        videoSearchEngine: VideoSearchEngine = VideoSearchEngine.YOUTUBE,
    ) {
        if (searchQuery.isBlank()) return

        Timber.d("searchOnVideoPlatform, videoSearchEngine = $videoSearchEngine")
        this.launchView(videoSearchEngine.buildSearchUri(searchQuery))
    }

    fun Context.openURL(url: String) {
        if (url.isBlank()) return

        Timber.d("openURL")
        this.launchView(url.addURLScheme().toUri())
    }

    fun Context.checkOnMarket(packageName: String, isGooglePlay: Boolean = true) {
        val marketUri: Uri = when {
            packageName.isBlank() -> {
                return
            }

            packageName.contains(".") -> {
                "market://details?id=${packageName.dropSpaces()}"
            }

            else -> {
                "market://search?q=${packageName.trim()}"
            }
        }.toUri()
        Timber.d("checkOnMarket")
        this.launchView(marketUri, GOOGLE_PLAY.takeIf { isGooglePlay })
    }

    fun Context.checkOnGoogleMaps(latitude: String, longitude: String) {
        if (latitude.isBlank() || longitude.isBlank()) return

        Timber.d("checkOnGoogleMaps")
        val coordinates = "$latitude,$longitude"
        val googleMapsIntentUri = "geo:$coordinates?q=$coordinates".toUri()
        this.launchView(googleMapsIntentUri, GOOGLE_MAPS)
    }

    @JvmStatic
    fun Context.openSystemSettings(settingType: String) {
        val intent = systemSettingsIntent(settingType) ?: return
        Timber.d("openSystemSettings: $settingType")
        this.launch(intent)
    }

    fun Context.openAppDetailSettings() {
        Timber.d("openAppDetailSettings")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:$packageName".toUri()
        }
        this.launch(intent)
    }

    fun Context.openAppLanguageSetting() {
        if (!OSVersion.android13()) return

        Timber.d("openAppLanguageSetting")
        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
        intent.data = Uri.fromParts(PACKAGE, this.packageName, null)
        this.launch(intent)
    }
}
