package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

internal object RemoveVolumeIntroMigration : DataMigration<Preferences> {
    private val legacyKey = booleanPreferencesKey("have_tapped_add_button")
    override suspend fun shouldMigrate(currentData: Preferences) = legacyKey in currentData
    override suspend fun migrate(currentData: Preferences): Preferences =
        currentData.toMutablePreferences().apply { remove(legacyKey) }

    override suspend fun cleanUp() = Unit
}
