package me.dizzykitty3.androidtoolkitty.ui.home

import me.dizzykitty3.androidtoolkitty.utils.URLUtils.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SocialMediaProfileRulesTest {

    @Test
    fun profileUrl_normalizesPlatformSpecificUsernames() {
        assertEquals(
            "bsky.app/profile/theo.bsky.social",
            toProfileFullURL(Platform.BLUESKY, " theo "),
        )
        assertEquals(
            "bilibili.com/video/BV1abc",
            toProfileFullURL(Platform.BILIBILI_BV, "1abc"),
        )
        assertEquals(
            "theo.tumblr.com",
            toProfileFullURL(Platform.TUMBLR, " theo "),
        )
        assertEquals(
            "bsky.app/profile/theo.example",
            toProfileFullURL(Platform.BLUESKY, "theo.example"),
        )
        assertEquals(
            "bilibili.com/video/AV123",
            toProfileFullURL(Platform.BILIBILI_AV, "AV123"),
        )
        assertEquals(
            "youtube.com/results?search_query=kotlin compose",
            toProfileFullURL(Platform.YOUTUBE_SEARCH, " kotlin compose "),
        )
    }

    @Test
    fun profileUrl_keepsPlatformBaseUrlForBlankUsernames() {
        assertEquals("bsky.app/profile/", toProfileFullURL(Platform.BLUESKY, ""))
        assertEquals(".fanbox.cc", toProfileFullURL(Platform.FANBOX, ""))
        assertEquals("bilibili.com/video/av", toProfileFullURL(Platform.BILIBILI_AV, ""))
    }

    @Test
    fun validation_appliesNumericAndCommonRulesOnlyToRelevantPlatforms() {
        assertFalse(isValid(Platform.BILIBILI_UUID, "12ab"))
        assertTrue(isValid(Platform.BILIBILI_UUID, " 123 "))
        assertFalse(isValid(Platform.X, "theo-kitty"))
        assertTrue(isValid(Platform.X, "theo_kitty"))
        assertTrue(isValid(Platform.GITHUB, "theo-kitty"))
    }

    @Test
    fun everyNumbersOnlyPlatform_rejectsNonNumericUsernames() {
        val numericPlatforms = listOf(
            Platform.BILIBILI_UUID,
            Platform.BILIBILI_AV,
            Platform.PIXIV_ARTWORK,
            Platform.PIXIV_UUID,
            Platform.STEAM_UUID,
            Platform.WEIBO_UUID,
            Platform.GOOGLE_ISSUE_TRACKER,
        )

        numericPlatforms.forEach { platform ->
            assertFalse(isValid(platform, "12a"))
            assertTrue(isValid(platform, " 123 "))
        }
    }

    @Test
    fun caseSensitivity_isEnabledOnlyForLitLink() {
        assertTrue(isCaseSensitive(Platform.LIT_LINK))
        assertFalse(isCaseSensitive(Platform.GITHUB))
    }

    @Test
    fun normalizedPlatformIndex_clampsInvalidPersistedIndexes() {
        assertEquals(0, normalizedPlatformIndex(-1))
        assertEquals(2, normalizedPlatformIndex(2))
        assertEquals(Platform.entries.lastIndex, normalizedPlatformIndex(Int.MAX_VALUE))
    }
}
