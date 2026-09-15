package me.dizzykitty3.androidtoolkitty.utils

import androidx.annotation.StringRes
import me.dizzykitty3.androidtoolkitty.R

enum class SearchEngine(@param:StringRes val title: Int) {
    GOOGLE(R.string.google),
    BING(R.string.bing),
    DUCKDUCKGO(R.string.duckduckgo),
    ECOSIA(R.string.ecosia),
    ;

    companion object {
        fun fromStoredName(name: String?): SearchEngine? =
            entries.firstOrNull { it.name == name }
    }
}

enum class VideoSearchEngine(@param:StringRes val title: Int) {
    YOUTUBE(R.string.youtube),
    BILIBILI(R.string.bilibili),
    ;

    companion object {
        fun fromStoredName(name: String?): VideoSearchEngine? =
            entries.firstOrNull { it.name == name }
    }
}
