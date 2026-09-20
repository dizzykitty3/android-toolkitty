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
    fun caseSensitivity_isEnabledOnlyForLitLink() {
        assertTrue(isCaseSensitive(Platform.LIT_LINK))
        assertFalse(isCaseSensitive(Platform.GITHUB))
    }
}
