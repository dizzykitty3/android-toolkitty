package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.dizzykitty3.androidtoolkitty.utils.VolumeSaveResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VolumeSlotsRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun slots_saveIndependentlyAndRejectDuplicateStepsWithoutChangingSettings() = runTest {
        val store = PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(
                folder.root,
                "slots.preferences_pb"
            )
        }
        val repository = SettingsRepository(store)
        assertEquals(listOf(null, null, null), repository.settingsFlow.first().customVolumes)
        repository.toggleDynamicColor(false)
        assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(0, 40, 15))
        assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(1, 60, 15))
        assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(2, 80, 15))
        assertEquals(listOf(40, 60, 80), repository.settingsFlow.first().customVolumes)
        val before = repository.settingsFlow.first()
        assertEquals(VolumeSaveResult.DUPLICATE, repository.updateCustomVolume(1, 41, 15))
        for ((slot, value, max) in listOf(
            Triple(-1, 50, 15), Triple(3, 50, 15), Triple(1, 0, 15),
            Triple(1, 101, 15), Triple(1, 1, 15), Triple(1, 50, 0), Triple(1, 50, -1),
        )) {
            assertEquals(VolumeSaveResult.INVALID, repository.updateCustomVolume(slot, value, max))
        }
        assertEquals(before, repository.settingsFlow.first())
        assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(0, 41, 15))
        assertEquals(listOf(41, 60, 80), repository.settingsFlow.first().customVolumes)
    }

    @Test
    fun deviceWithOnlyTwoNonZeroSteps_cannotFillThreeSlots() = runTest {
        val store = PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(
                folder.root,
                "limited.preferences_pb"
            )
        }
        val repository = SettingsRepository(store)
        assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(0, 50, 2))
        assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(1, 100, 2))
        for (percent in 1..100) {
            val result = repository.updateCustomVolume(2, percent, 2)
            assertEquals(
                if (percent < 25) VolumeSaveResult.INVALID else VolumeSaveResult.DUPLICATE,
                result
            )
        }
        assertEquals(listOf(50, 100, null), repository.settingsFlow.first().customVolumes)
    }

    @Test
    fun concurrentSaves_cannotClaimTheSameStep() = runTest {
        val store = PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(
                folder.root,
                "race.preferences_pb"
            )
        }
        val repository = SettingsRepository(store)
        val results = listOf(
            async { repository.updateCustomVolume(0, 40, 15) },
            async { repository.updateCustomVolume(1, 41, 15) },
        ).awaitAll()
        assertEquals(1, results.count { it == VolumeSaveResult.SAVED })
        assertEquals(1, results.count { it == VolumeSaveResult.DUPLICATE })
        assertEquals(1, repository.settingsFlow.first().customVolumes.count { it != null })
    }

    @Test
    fun migration_removesIntroFlagPreservesLegacyFourthSlotAndPersistsNewSlots() = runTest {
        val file = File(folder.root, "legacy.preferences_pb")
        val legacyFlag = booleanPreferencesKey("have_tapped_add_button")
        val writer = Job()
        val legacyStore =
            PreferenceDataStoreFactory.create(scope = CoroutineScope(coroutineContext + writer)) { file }
        try {
            legacyStore.edit {
                it[legacyFlag] = true
                it[intPreferencesKey("custom_volume")] = 80
                it[booleanPreferencesKey("dynamic_color")] = false
            }
        } finally {
            writer.cancelAndJoin()
        }

        val migratedJob = Job()
        val migrated = PreferenceDataStoreFactory.create(
            migrations = listOf(RemoveVolumeIntroMigration),
            scope = CoroutineScope(coroutineContext + migratedJob),
        ) { file }
        try {
            val repository = SettingsRepository(migrated)
            assertEquals(listOf(null, null, 80), repository.settingsFlow.first().customVolumes)
            assertNull(migrated.data.first()[legacyFlag])
            assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(0, 20, 15))
            assertEquals(VolumeSaveResult.SAVED, repository.updateCustomVolume(1, 40, 15))
        } finally {
            migratedJob.cancelAndJoin()
        }

        val reopenedStore = PreferenceDataStoreFactory.create(
            migrations = listOf(RemoveVolumeIntroMigration), scope = backgroundScope,
        ) { file }
        val restored = SettingsRepository(reopenedStore).settingsFlow.first()
        assertEquals(listOf(20, 40, 80), restored.customVolumes)
        assertEquals(false, restored.dynamicColor)
        assertNull(reopenedStore.data.first()[legacyFlag])
    }

    @Test
    fun corruptPercentages_areReadAsEmptySlots() = runTest {
        val store = PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(
                folder.root,
                "invalid.preferences_pb"
            )
        }
        store.edit {
            it[intPreferencesKey("custom_volume_1")] = -1
            it[intPreferencesKey("custom_volume_2")] = 101
            it[intPreferencesKey("custom_volume")] = Int.MIN_VALUE
        }
        assertEquals(
            listOf(null, null, null),
            SettingsRepository(store).settingsFlow.first().customVolumes
        )
    }
}
