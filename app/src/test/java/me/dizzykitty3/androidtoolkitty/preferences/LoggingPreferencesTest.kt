package me.dizzykitty3.androidtoolkitty.preferences

import android.content.Context
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
}
