package me.dizzykitty3.androidtoolkitty

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import me.dizzykitty3.androidtoolkitty.preferences.LoggingPreferences
import timber.log.Timber

@HiltAndroidApp
class ToolKitty : Application() {
    override fun onCreate() {
        super.onCreate()
        LoggingPreferences.initialize(this)
        if (BuildConfig.DEBUG || LoggingPreferences.isEnabled) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("onCreate")
    }
}
