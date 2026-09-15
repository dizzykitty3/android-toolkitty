package me.dizzykitty3.androidtoolkitty.datastore

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.dizzykitty3.androidtoolkitty.utils.SearchEngine
import me.dizzykitty3.androidtoolkitty.utils.VideoSearchEngine
import javax.inject.Inject

val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> {
    error("No SettingsViewModel provided")
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val settingsState = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings.default()
    )

    private fun updateSettings(update: suspend SettingsRepository.() -> Unit) {
        viewModelScope.launch { repository.update() }
    }

    fun getShownState(card: String): Boolean {
        return settingsState.value.isShown(card)
    }

    fun saveShownState(card: String, isShown: Boolean) {
        updateSettings { saveShownState(card, isShown) }
    }

    fun toggleDynamicColor(enabled: Boolean) {
        updateSettings { toggleDynamicColor(enabled) }
    }

    fun toggleAutoClearClipboard(enabled: Boolean) {
        updateSettings { toggleAutoClearClipboard(enabled) }
    }

    fun setSearchEngine(engine: SearchEngine) {
        updateSettings { setSearchEngine(engine) }
    }

    fun setVideoSearchEngine(engine: VideoSearchEngine) {
        updateSettings { setVideoSearchEngine(engine) }
    }

    fun setDoNotRememberLastSearch(enabled: Boolean) {
        updateSettings { setDoNotRememberLastSearch(enabled) }
    }

    fun updateLastSelectedPlatformIndex(index: Int) {
        updateSettings { updateLastSelectedPlatformIndex(index) }
    }

    fun updateTypingContents(contents: String) {
        updateSettings { updateTypingContents(contents) }
    }

    fun updateLatitude(latitude: String) {
        updateSettings { updateLatitude(latitude) }
    }

    fun updateLongitude(longitude: String) {
        updateSettings { updateLongitude(longitude) }
    }

    fun toggleHaveTappedAddButton(haveTapped: Boolean) {
        updateSettings { toggleHaveTappedAddButton(haveTapped) }
    }

    fun updateCustomVolume(value: Int) {
        updateSettings { updateCustomVolume(value) }
    }

    fun increaseVolumeButtonTapCount() {
        updateSettings { increaseVolumeButtonTapCount() }
    }

    fun updateWheelOfFortuneItems(items: String) {
        updateSettings { updateWheelOfFortuneItems(items) }
    }
}
