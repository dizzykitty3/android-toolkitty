package me.dizzykitty3.androidtoolkitty.preferences

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LoggingPreferencesTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Before
    fun resetPreferences() {
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        LoggingPreferences.initialize(context)
    }

    @Test
    fun isEnabled_defaultsToFalseAfterInitialization() {
        assertFalse(LoggingPreferences.isEnabled)
    }

    @Test
    fun isEnabled_persistsChangesAcrossInitialization() {
        LoggingPreferences.isEnabled = true
        LoggingPreferences.initialize(context)

        assertTrue(LoggingPreferences.isEnabled)
    }

    @Test
    fun initialize_readsTheLegacyLoggingPreferenceWithoutOverwritingIt() {
        val legacyPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        assertTrue(legacyPreferences.edit().putBoolean("is_logging_enabled", true).commit())

        LoggingPreferences.initialize(context)

        assertTrue(LoggingPreferences.isEnabled)
        assertEquals(mapOf("is_logging_enabled" to true), legacyPreferences.all)
    }

    @Test
    fun disablingLogging_updatesTheLegacyKeyAndPreservesOtherPreferences() {
        val legacyPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        assertTrue(
            legacyPreferences.edit()
                .putBoolean("is_logging_enabled", true)
                .putString("unrelated_setting", "preserve me")
                .commit(),
        )
        LoggingPreferences.initialize(context)

        LoggingPreferences.isEnabled = false
        LoggingPreferences.initialize(context)

        assertFalse(LoggingPreferences.isEnabled)
        assertEquals(
            mapOf("is_logging_enabled" to false, "unrelated_setting" to "preserve me"),
            legacyPreferences.all,
        )
    }
}
