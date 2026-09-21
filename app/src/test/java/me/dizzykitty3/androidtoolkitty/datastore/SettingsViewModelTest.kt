package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
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

    private fun newPreferencesFile(): File =
        File.createTempFile("toolkitty-view-model-", ".preferences_pb").apply {
            delete()
            deleteOnExit()
        }
}
