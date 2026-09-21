package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import me.dizzykitty3.androidtoolkitty.utils.URLUtils.Platform
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class URLPlatformTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    fun everyPlatform_hasAUsablePrefixAndDisplayName() {
        Platform.entries.forEach { platform ->
            assertTrue(platform.prefix.isNotBlank())
            assertTrue(context.getString(platform.platform).isNotBlank())
        }
    }
}
