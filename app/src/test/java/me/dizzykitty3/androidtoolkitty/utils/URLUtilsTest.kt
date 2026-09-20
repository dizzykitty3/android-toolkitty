package me.dizzykitty3.androidtoolkitty.utils

import me.dizzykitty3.androidtoolkitty.utils.URLUtils.addSuffix
import me.dizzykitty3.androidtoolkitty.utils.URLUtils.addURLScheme
import me.dizzykitty3.androidtoolkitty.utils.URLUtils.getSuffix
import org.junit.Assert.assertEquals
import org.junit.Test

class URLUtilsTest {

    @Test
    fun addURLScheme_preservesExistingSchemeAndAddsHttpsOtherwise() {
        assertEquals("https://example.com", "example.com".addURLScheme())
        assertEquals("http://example.com", "http://example.com".addURLScheme())
        assertEquals("custom://entry", "custom://entry".addURLScheme())
    }

    @Test
    fun suffixResolution_usesKnownAndDefaultDomains() {
        assertEquals(".co.uk", "bbc".getSuffix())
        assertEquals(".com", "unknown-service".getSuffix())
        assertEquals("", "already.has.domain".getSuffix())
        assertEquals("https://bbc.co.uk", "bbc".addSuffix())
    }

    @Test
    fun suffixResolution_handlesRepresentativeSpecialDomainsCaseInsensitively() {
        assertEquals(".co.jp", "RAKUTEN".getSuffix())
        assertEquals(".net", "pixiv".getSuffix())
        assertEquals(".org", "telegram".getSuffix())
        assertEquals(".us", "zoom".getSuffix())
        assertEquals("https://pixiv.net", "pixiv".addSuffix())
    }
}
