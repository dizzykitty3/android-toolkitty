package me.dizzykitty3.androidtoolkitty.ui.home

import androidx.core.text.isDigitsOnly
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.dropSpaces
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.isInvalidUsername
import me.dizzykitty3.androidtoolkitty.utils.URLUtils

/**
 * URL formatting and validation rules for the social profile search card.
 */
internal fun toProfileFullURL(platform: URLUtils.Platform, username: String): String =
    when (platform) {
        URLUtils.Platform.BLUESKY -> if (username.contains(".")) "${platform.prefix}$username"
        else if (username.isNotBlank()) "${platform.prefix}${username.dropSpaces()}.bsky.social"
        else platform.prefix

        URLUtils.Platform.FANBOX,
        URLUtils.Platform.BOOTH,
        URLUtils.Platform.TUMBLR,
        URLUtils.Platform.CARRD -> "${username.dropSpaces()}${platform.prefix}"

        URLUtils.Platform.BILIBILI_AV -> if (username.lowercase().startsWith("av")) {
            "${platform.prefix}${username.dropSpaces()}"
        } else {
            "${platform.prefix}av${username.dropSpaces()}"
        }

        URLUtils.Platform.BILIBILI_BV -> if (username.lowercase().startsWith("bv")) {
            "${platform.prefix}${username.dropSpaces()}"
        } else {
            "${platform.prefix}BV${username.dropSpaces()}"
        }

        URLUtils.Platform.YOUTUBE_SEARCH,
        URLUtils.Platform.STEAM_SEARCH_STORE -> "${platform.prefix}${username.trim()}"

        else -> "${platform.prefix}${username.dropSpaces()}"
    }

private fun numbersOnlyPlatform(platform: URLUtils.Platform): Boolean =
    platform == URLUtils.Platform.BILIBILI_UUID ||
        platform == URLUtils.Platform.BILIBILI_AV ||
        platform == URLUtils.Platform.PIXIV_ARTWORK ||
        platform == URLUtils.Platform.PIXIV_UUID ||
        platform == URLUtils.Platform.STEAM_UUID ||
        platform == URLUtils.Platform.WEIBO_UUID ||
        platform == URLUtils.Platform.GOOGLE_ISSUE_TRACKER

internal fun isInvalidNotNumbersOnly(platform: URLUtils.Platform, username: String): Boolean =
    numbersOnlyPlatform(platform) && username.isNotBlank() && !username.dropSpaces().isDigitsOnly()

private fun usesCommonRule(platform: URLUtils.Platform): Boolean =
    platform == URLUtils.Platform.X

internal fun isInvalidCommonRule(platform: URLUtils.Platform, username: String): Boolean =
    usesCommonRule(platform) && username.isNotBlank() && username.dropSpaces().isInvalidUsername()

internal fun isValid(platform: URLUtils.Platform, username: String): Boolean =
    !isInvalidCommonRule(platform, username) && !isInvalidNotNumbersOnly(platform, username)

internal fun isCaseSensitive(platform: URLUtils.Platform): Boolean =
    platform == URLUtils.Platform.LIT_LINK
