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

    private fun newPreferencesFile(): File =
        File.createTempFile("toolkitty-view-model-", ".preferences_pb").apply {
            delete()
            deleteOnExit()
        }
}
