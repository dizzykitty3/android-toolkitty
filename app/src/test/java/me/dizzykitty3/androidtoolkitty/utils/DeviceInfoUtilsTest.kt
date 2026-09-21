package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import me.dizzykitty3.androidtoolkitty.BuildConfig
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.versionName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DeviceInfoUtilsTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    fun versionName_matchesTheGeneratedBuildConfigValue() {
        assertEquals(BuildConfig.VERSION_NAME, context.versionName)
    }

    @Test
    fun deviceInfo_valuesAreAvailableForDisplay() {
        assertTrue(StringUtils.manufacturer.isNotBlank())
        assertTrue(StringUtils.model.isNotBlank())
        assertTrue(StringUtils.device.isNotBlank())
        assertTrue(StringUtils.osVersion.startsWith("Android "))
        assertTrue(StringUtils.osVersion.endsWith("(35)"))
    }
}
