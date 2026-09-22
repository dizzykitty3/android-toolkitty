package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SearchEnginesTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

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

    @Test
    fun everyEngine_referencesANonBlankDisplayName() {
        SearchEngine.entries.forEach { engine ->
            assertTrue(context.getString(engine.title).isNotBlank())
        }
        VideoSearchEngine.entries.forEach { engine ->
            assertTrue(context.getString(engine.title).isNotBlank())
        }
    }
}
