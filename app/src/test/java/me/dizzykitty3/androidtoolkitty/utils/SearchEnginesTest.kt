package me.dizzykitty3.androidtoolkitty.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SearchEnginesTest {

    @Test
    fun searchEngine_deserializesKnownStoredNamesOnly() {
        assertEquals(SearchEngine.DUCKDUCKGO, SearchEngine.fromStoredName("DUCKDUCKGO"))
        assertNull(SearchEngine.fromStoredName("duckduckgo"))
        assertNull(SearchEngine.fromStoredName(null))
    }

    @Test
    fun videoSearchEngine_deserializesKnownStoredNamesOnly() {
        assertEquals(VideoSearchEngine.BILIBILI, VideoSearchEngine.fromStoredName("BILIBILI"))
        assertNull(VideoSearchEngine.fromStoredName("VIMEO"))
    }

    @Test
    fun everyEngine_roundTripsThroughItsStoredName() {
        SearchEngine.entries.forEach { engine ->
            assertEquals(engine, SearchEngine.fromStoredName(engine.name))
        }
        VideoSearchEngine.entries.forEach { engine ->
            assertEquals(engine, VideoSearchEngine.fromStoredName(engine.name))
        }
    }
}
