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
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.dropSpaces
import me.dizzykitty3.androidtoolkitty.utils.ToastUtil.showToast
import me.dizzykitty3.androidtoolkitty.utils.URLUtil.addURLScheme
import timber.log.Timber

object IntentUtil {
    // Didn't use StartActivity as the name because a custom extension function is needed.
    private fun Context.launch(intent: Intent) {
        var msg: String

        try {
            Timber.d("startActivity")
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            this.startActivity(intent)
            return
        } catch (e: ActivityNotFoundException) {
            if (intent.data.toString().startsWith("bilibili://search?keyword=")) {
                // Handle bilibili search
                this.openURL(
                    "m.bilibili.com/search?keyword=${
                        intent.data.toString().removePrefix("bilibili://search?keyword=")
                    }"
                )
                return
            } else {
                Timber.e("brand = ${StringUtil.manufacturer}\nintent = ${intent}\n$e")
                msg = this.getString(R.string.oem_removed, StringUtil.manufacturer)
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

    fun Context.openScreen(screen: Class<*>) {
        launch(Intent(this, screen))
    }

    fun Context.openSearch(query: String, bingSearch: Boolean = false) {
        if (query.isBlank()) return

        Timber.d("openSearch, bingSearch = $bingSearch")
        if (bingSearch) {
            this.openURL("https://bing.com/search?q=$query")
        } else {
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY, query)
            this.launch(intent)
        }
    }

    fun Context.searchOnYouTube(query: String, bingSearch: Boolean = false) {
        if (query.isBlank()) return

        Timber.d("searchOnYouTube, bingSearch = $bingSearch")
        val intent = Intent(
            Intent.ACTION_VIEW,
            if (bingSearch) "bilibili://search?keyword=$query".toUri() else "https://youtube.com/results?search_query=$query".toUri()
        )
        this.launch(intent)
    }

    fun Context.openURL(url: String) {
        if (url.isBlank()) return

        Timber.d("openURL")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = url.addURLScheme().toUri()
        this.launch(intent)
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
        val intent = Intent(Intent.ACTION_VIEW, marketUri)
        if (isGooglePlay) intent.setPackage(GOOGLE_PLAY)
        this.launch(intent)
    }

    fun Context.checkOnGoogleMaps(latitude: String, longitude: String) {
        if (latitude.isBlank() || longitude.isBlank()) return

        Timber.d("checkOnGoogleMaps")
        val coordinates = "$latitude,$longitude"
        val googleMapsIntentUri = "geo:$coordinates?q=$coordinates".toUri()
        val intent = Intent(Intent.ACTION_VIEW, googleMapsIntentUri)
        intent.setPackage(GOOGLE_MAPS)
        this.launch(intent)
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
