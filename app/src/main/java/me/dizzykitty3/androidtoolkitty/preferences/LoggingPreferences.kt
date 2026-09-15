package me.dizzykitty3.androidtoolkitty.preferences

import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

/**
 * Synchronous preferences needed while the Application is starting.
 * Runtime settings belong in DataStore; this object is only for logging.
 */
object LoggingPreferences {
    // Keep the existing storage name and key so upgrades preserve the user's setting.
    private const val PREFERENCES_NAME = "Settings"
    private const val LOGGING_ENABLED_KEY = "is_logging_enabled"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.applicationContext
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var isEnabled: Boolean
        get() = sharedPreferences.getBoolean(LOGGING_ENABLED_KEY, false)
        set(value) {
            Timber.d("set logging enabled: $value")
            sharedPreferences.edit().putBoolean(LOGGING_ENABLED_KEY, value).apply()
        }
}
