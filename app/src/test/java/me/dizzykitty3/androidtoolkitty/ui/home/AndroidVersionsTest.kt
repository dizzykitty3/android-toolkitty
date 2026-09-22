package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidVersionsTest {

    @Test
    fun androidVersions_areListedFromNewestToOldestWithoutDuplicateApis() {
        val apis = androidVersions.map { it.api }

        assertEquals(apis.sortedDescending(), apis)
        assertEquals(apis.size, apis.distinct().size)
        assertEquals(Build.VERSION_CODES.CINNAMON_BUN, androidVersions.first().api)
    }

    @Test
    fun androidVersions_haveDisplayNamesAndCodenames() {
        assertTrue(androidVersions.all { it.name.isNotBlank() && it.codename.isNotBlank() })
    }
}
