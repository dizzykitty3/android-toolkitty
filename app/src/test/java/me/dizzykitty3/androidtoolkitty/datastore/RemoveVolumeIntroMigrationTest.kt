package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveVolumeIntroMigrationTest {
    private val legacyKey = booleanPreferencesKey("have_tapped_add_button")

    @Test
    fun absentLegacyFlag_doesNotRequireMigration() = runTest {
        val preferences = preferencesOf(intPreferencesKey("custom_volume") to 42)
        assertFalse(RemoveVolumeIntroMigration.shouldMigrate(emptyPreferences()))
        assertFalse(RemoveVolumeIntroMigration.shouldMigrate(preferences))
        assertEquals(preferences, RemoveVolumeIntroMigration.migrate(preferences))
    }

    @Test
    fun eitherLegacyFlagValue_isRemovedWithoutMutatingInputOrOtherSettings() = runTest {
        val retained = preferencesOf(
            intPreferencesKey("custom_volume") to 80,
            intPreferencesKey("custom_volume_1") to 20,
            booleanPreferencesKey("dynamic_color") to false,
            booleanPreferencesKey("card_volume") to false,
        )
        for (flag in listOf(false, true)) {
            val original = retained.toMutablePreferences().apply { this[legacyKey] = flag }
            assertTrue(RemoveVolumeIntroMigration.shouldMigrate(original))

            val migrated = RemoveVolumeIntroMigration.migrate(original)

            assertNull(migrated[legacyKey])
            assertEquals(retained, migrated)
            assertEquals(flag, original[legacyKey])
            assertFalse(RemoveVolumeIntroMigration.shouldMigrate(migrated))
            assertEquals(migrated, RemoveVolumeIntroMigration.migrate(migrated))
        }
    }
}
