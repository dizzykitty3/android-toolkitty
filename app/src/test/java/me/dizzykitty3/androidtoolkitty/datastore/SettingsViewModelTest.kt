package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import java.io.File
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.dizzykitty3.androidtoolkitty.utils.SearchEngine
import me.dizzykitty3.androidtoolkitty.utils.VideoSearchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @Test
    fun updateMethods_persistChangesAndPublishThemThroughSettingsState() {
        val dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                val viewModel = SettingsViewModel(repository)
                val updatedState = async(start = CoroutineStart.UNDISPATCHED) {
                    viewModel.settingsState.first {
                        !it.dynamicColor &&
                            it.searchEngine == SearchEngine.ECOSIA &&
                            it.videoSearchEngine == VideoSearchEngine.BILIBILI &&
                            !it.isShown("card_search")
                    }
                }

                viewModel.toggleDynamicColor(false)
                viewModel.setSearchEngine(SearchEngine.ECOSIA)
                viewModel.setVideoSearchEngine(VideoSearchEngine.BILIBILI)
                viewModel.saveShownState("card_search", false)

                val state = updatedState.await()
                assertFalse(state.dynamicColor)
                assertEquals(SearchEngine.ECOSIA, state.searchEngine)
                assertEquals(VideoSearchEngine.BILIBILI, state.videoSearchEngine)
                assertFalse(state.isShown("card_search"))
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun remainingUpdateMethods_publishSettingsStateAndHonorPrivacyPreference() {
        val dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                val viewModel = SettingsViewModel(repository)
                val wheelItems = "{\"items\":[\"Tea\",\"Coffee\"]}"
                val updatedState = async(start = CoroutineStart.UNDISPATCHED) {
                    viewModel.settingsState.first {
                        it.doNotRememberLastSearch &&
                            it.autoClearClipboard &&
                            it.lastSelectedPlatformIndex == 4 &&
                            it.customVolume == 35 &&
                            it.latitude == "25.0330" &&
                            it.longitude == "121.5654" &&
                            it.haveTappedAddButton &&
                            it.volumeButtonTapCount == 1 &&
                            it.wheelOfFortuneItems == wheelItems
                    }
                }

                viewModel.setDoNotRememberLastSearch(true)
                viewModel.updateTypingContents("must not persist")
                viewModel.toggleAutoClearClipboard(true)
                viewModel.updateLastSelectedPlatformIndex(4)
                viewModel.updateCustomVolume(35)
                viewModel.updateLatitude("25.0330")
                viewModel.updateLongitude("121.5654")
                viewModel.toggleHaveTappedAddButton(true)
                viewModel.increaseVolumeButtonTapCount()
                viewModel.updateWheelOfFortuneItems(wheelItems)

                val state = updatedState.await()
                assertEquals("", state.typingContents)
                assertEquals(true, state.autoClearClipboard)
                assertEquals(4, state.lastSelectedPlatformIndex)
                assertEquals(35, state.customVolume)
                assertEquals("25.0330", state.latitude)
                assertEquals("121.5654", state.longitude)
                assertEquals(true, state.haveTappedAddButton)
                assertEquals(1, state.volumeButtonTapCount)
                assertEquals(wheelItems, state.wheelOfFortuneItems)
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun settingsState_resubscribingAfterTimeoutLoadsLatestPersistedSettings() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                repository.updateCustomVolume(25)
                val viewModel = SettingsViewModel(repository)
                try {
                    assertEquals(25, viewModel.settingsState.first { it.customVolume == 25 }.customVolume)

                    // The first collector has finished; let WhileSubscribed stop its upstream.
                    runCurrent()
                    advanceTimeBy(5_001)
                    runCurrent()

                    repository.updateCustomVolume(70)
                    assertEquals(70, repository.settingsFlow.first().customVolume)
                    runCurrent()
                    assertEquals(25, viewModel.settingsState.value.customVolume)

                    assertEquals(70, viewModel.settingsState.first { it.customVolume == 70 }.customVolume)
                } finally {
                    viewModel.viewModelScope.cancel()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun updateWithoutSubscribers_persistsAndIsAvailableToANewViewModel() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val dataStore = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
                val repository = SettingsRepository(dataStore)
                val viewModel = SettingsViewModel(repository)
                try {
                    // Never collect settingsState on the writer ViewModel.
                    viewModel.updateCustomVolume(65)
                    viewModel.toggleDynamicColor(false)
                    val persisted = repository.settingsFlow.first {
                        it.customVolume == 65 && !it.dynamicColor
                    }
                    assertEquals(65, persisted.customVolume)
                    assertFalse(persisted.dynamicColor)
                } finally {
                    viewModel.viewModelScope.cancel()
                }

                val restoredViewModel = SettingsViewModel(SettingsRepository(dataStore))
                try {
                    val restored = restoredViewModel.settingsState.first { it.customVolume == 65 }
                    assertEquals(65, restored.customVolume)
                    assertFalse(restored.dynamicColor)
                } finally {
                    restoredViewModel.viewModelScope.cancel()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun settingsState_keepsReceivingUpdatesDuringSubscriptionGracePeriod() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                repository.updateCustomVolume(25)
                val viewModel = SettingsViewModel(repository)
                try {
                    viewModel.settingsState.first { it.customVolume == 25 }
                    runCurrent()
                    advanceTimeBy(4_999)
                    runCurrent()

                    // No collectors remain, but the five-second grace period is still active.
                    repository.updateCustomVolume(60)
                    runCurrent()
                    assertEquals(60, viewModel.settingsState.value.customVolume)
                } finally {
                    viewModel.viewModelScope.cancel()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun settingsState_keepsUpdatingWhileAnotherSubscriberRemains() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                repository.updateCustomVolume(25)
                val viewModel = SettingsViewModel(repository)
                try {
                    val firstSubscriber = backgroundScope.launch {
                        viewModel.settingsState.collect()
                    }
                    val observedVolumes = mutableListOf<Int?>()
                    val remainingSubscriber = backgroundScope.launch {
                        viewModel.settingsState.collect { observedVolumes.add(it.customVolume) }
                    }
                    viewModel.settingsState.first { it.customVolume == 25 }
                    runCurrent()
                    assertEquals(25, observedVolumes.last())

                    firstSubscriber.cancelAndJoin()
                    advanceTimeBy(5_001)
                    runCurrent()
                    repository.updateCustomVolume(80)
                    runCurrent()

                    assertEquals(80, observedVolumes.last())
                    assertEquals(80, viewModel.settingsState.value.customVolume)
                    remainingSubscriber.cancelAndJoin()
                } finally {
                    viewModel.viewModelScope.cancel()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun clearingViewModelStore_stopsSettingsUpdatesEvenWithAnActiveCollector() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                repository.updateCustomVolume(25)
                val viewModel = SettingsViewModel(repository)
                val store = ViewModelStore().apply { put("settings", viewModel) }
                try {
                    val observedVolumes = mutableListOf<Int?>()
                    backgroundScope.launch {
                        viewModel.settingsState.collect { observedVolumes.add(it.customVolume) }
                    }
                    viewModel.settingsState.first { it.customVolume == 25 }
                    runCurrent()
                    val observationsBeforeClear = observedVolumes.toList()

                    store.clear()
                    repository.updateCustomVolume(80)
                    runCurrent()

                    assertEquals(80, repository.settingsFlow.first().customVolume)
                    assertEquals(25, viewModel.settingsState.value.customVolume)
                    assertEquals(observationsBeforeClear, observedVolumes)
                } finally {
                    store.clear()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun clearingViewModelStore_cancelsQueuedWritesAndIgnoresLaterUpdates() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                repository.updateCustomVolume(25)
                val viewModel = SettingsViewModel(repository)
                val store = ViewModelStore().apply { put("settings", viewModel) }
                try {
                    // StandardTestDispatcher keeps this write queued until the scheduler runs.
                    viewModel.updateCustomVolume(60)
                    store.clear()
                    runCurrent()
                    assertEquals(25, repository.settingsFlow.first().customVolume)

                    viewModel.updateCustomVolume(90)
                    runCurrent()
                    assertEquals(25, repository.settingsFlow.first().customVolume)
                } finally {
                    store.clear()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun homeCardOrdering_publishesMovesAndResetWithoutChangingOtherSettings() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        try {
            runTest(dispatcher) {
                val file = newPreferencesFile()
                val repository = SettingsRepository(
                    PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
                )
                val defaults = listOf("card_a", "card_b", "card_c")
                repository.saveShownState("card_b", false)
                repository.updateCustomVolume(45)
                val initial = repository.settingsFlow.first()
                val viewModel = SettingsViewModel(repository)
                try {
                    viewModel.moveHomeCard("card_c", -1, defaults)
                    val moved = viewModel.settingsState.first {
                        it.homeCardOrder == listOf("card_c", "card_b", "card_a")
                    }
                    assertEquals(initial.copy(homeCardOrder = listOf("card_c", "card_b", "card_a")), moved)

                    // Keep collecting before reset so the initial empty order cannot satisfy the assertion.
                    val reset = async(start = CoroutineStart.UNDISPATCHED) {
                        viewModel.settingsState.first { it.homeCardOrder.isEmpty() }
                    }
                    viewModel.resetHomeCardOrder()
                    assertEquals(initial, reset.await())
                    assertEquals(initial, repository.settingsFlow.first())
                } finally {
                    viewModel.viewModelScope.cancel()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun newPreferencesFile(): File =
        File.createTempFile("toolkitty-view-model-", ".preferences_pb").apply {
            delete()
            deleteOnExit()
        }
}
