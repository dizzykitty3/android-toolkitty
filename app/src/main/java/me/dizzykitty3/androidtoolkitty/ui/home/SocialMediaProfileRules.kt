package me.dizzykitty3.androidtoolkitty.ui.home

import androidx.core.text.isDigitsOnly
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.dropSpaces
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.isInvalidUsername
import me.dizzykitty3.androidtoolkitty.utils.URLUtil

/**
 * URL formatting and validation rules for the social profile search card.
 */
internal fun toProfileFullURL(platform: URLUtil.Platform, username: String): String =
    when (platform) {
        URLUtil.Platform.BLUESKY -> if (username.contains(".")) "${platform.prefix}$username"
        else if (username.isNotBlank()) "${platform.prefix}${username.dropSpaces()}.bsky.social"
        else platform.prefix

        URLUtil.Platform.FANBOX,
        URLUtil.Platform.BOOTH,
        URLUtil.Platform.TUMBLR,
        URLUtil.Platform.CARRD -> "${username.dropSpaces()}${platform.prefix}"

        URLUtil.Platform.BILIBILI_AV -> if (username.lowercase().startsWith("av")) {
            "${platform.prefix}${username.dropSpaces()}"
        } else {
            "${platform.prefix}av${username.dropSpaces()}"
        }

        URLUtil.Platform.BILIBILI_BV -> if (username.lowercase().startsWith("bv")) {
            "${platform.prefix}${username.dropSpaces()}"
        } else {
            "${platform.prefix}BV${username.dropSpaces()}"
        }

        URLUtil.Platform.YOUTUBE_SEARCH,
        URLUtil.Platform.STEAM_SEARCH_STORE -> "${platform.prefix}${username.trim()}"

        else -> "${platform.prefix}${username.dropSpaces()}"
    }

private fun numbersOnlyPlatform(platform: URLUtil.Platform): Boolean =
    platform == URLUtil.Platform.BILIBILI_UUID ||
        platform == URLUtil.Platform.BILIBILI_AV ||
        platform == URLUtil.Platform.PIXIV_ARTWORK ||
        platform == URLUtil.Platform.PIXIV_UUID ||
        platform == URLUtil.Platform.STEAM_UUID ||
        platform == URLUtil.Platform.WEIBO_UUID ||
        platform == URLUtil.Platform.GOOGLE_ISSUE_TRACKER

internal fun isInvalidNotNumbersOnly(platform: URLUtil.Platform, username: String): Boolean =
    numbersOnlyPlatform(platform) && username.isNotBlank() && !username.dropSpaces().isDigitsOnly()

private fun usesCommonRule(platform: URLUtil.Platform): Boolean =
    platform == URLUtil.Platform.X

internal fun isInvalidCommonRule(platform: URLUtil.Platform, username: String): Boolean =
    usesCommonRule(platform) && username.isNotBlank() && username.dropSpaces().isInvalidUsername()

internal fun isValid(platform: URLUtil.Platform, username: String): Boolean =
    !isInvalidCommonRule(platform, username) && !isInvalidNotNumbersOnly(platform, username)

internal fun isCaseSensitive(platform: URLUtil.Platform): Boolean =
    platform == URLUtil.Platform.LIT_LINK
