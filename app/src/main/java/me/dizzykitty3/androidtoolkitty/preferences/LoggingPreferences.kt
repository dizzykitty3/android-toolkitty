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
    private const val PREF_NAME = "Settings"
    private const val IS_ENABLED = "is_logging_enabled"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.applicationContext
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var isEnabled: Boolean
        get() = sharedPreferences.getBoolean(IS_ENABLED, false)
        set(value) {
            Timber.d("set logging enabled: $value")
            sharedPreferences.edit().putBoolean(IS_ENABLED, value).apply()
        }
}
