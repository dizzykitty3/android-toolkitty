package me.dizzykitty3.androidtoolkitty.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchUrlRulesTest {

    @Test
    fun normalizeUrlInput_convertsPeriodsAndWhitespaceToPeriods() {
        assertEquals("example.com.profile", normalizeUrlInput("example。com profile"))
        assertEquals("example.com.profile", normalizeUrlInput("example.com　profile"))
        assertEquals("a..b", normalizeUrlInput("a 。b"))
    }

    @Test
    fun displayedUrlSuffix_showsInferredSuffixAndHandlesTrailingPeriods() {
        assertEquals("", displayedUrlSuffix(""))
        assertEquals(".co.uk", displayedUrlSuffix("bbc"))
        assertEquals("co.uk", displayedUrlSuffix("bbc."))
        assertEquals("co.uk", displayedUrlSuffix("bbc..."))
        assertEquals("", displayedUrlSuffix("example.com..."))
    }
}
