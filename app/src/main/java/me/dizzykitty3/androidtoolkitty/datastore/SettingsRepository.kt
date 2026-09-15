package me.dizzykitty3.androidtoolkitty.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import me.dizzykitty3.androidtoolkitty.utils.SearchEngine
import me.dizzykitty3.androidtoolkitty.utils.VideoSearchEngine
import javax.inject.Inject

@Serializable
data class WheelOfFortuneItems(val items: List<String>)

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val AUTO_CLEAR_CLIPBOARD = booleanPreferencesKey("auto_clear_clipboard")
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val VIDEO_SEARCH_ENGINE = stringPreferencesKey("video_search_engine")
        val DO_NOT_REMEMBER_RECENT_SEARCHES = booleanPreferencesKey("do_not_remember_recent_searches")
        val LAST_SELECTED_PLATFORM_INDEX = intPreferencesKey("last_selected_platform_index")
        val TYPING_CONTENTS = stringPreferencesKey("typing_contents")
        val LATITUDE = stringPreferencesKey("latitude")
        val LONGITUDE = stringPreferencesKey("longitude")
        val HAVE_TAPPED_ADD_BUTTON = booleanPreferencesKey("have_tapped_add_button")
        val CUSTOM_VOLUME = intPreferencesKey("custom_volume")
        val HAVE_TAPPED_VOLUME_BUTTON = intPreferencesKey("have_tapped_volume_button")
        val WHEEL_OF_FORTUNE_ITEMS = stringPreferencesKey("wheel_of_fortune_items")
    }

    val settingsFlow: Flow<UserSettings> = dataStore.data.map { preferences ->
        val defaults = UserSettings.default()
        val cardShownStates = preferences.asMap()
            .mapNotNull { (key, value) ->
                if ((key.name.startsWith("card_") || key.name.startsWith("setting_")) &&
                    value is Boolean
                ) {
                    key.name to value
                } else {
                    null
                }
            }
            .toMap()

        UserSettings(
            dynamicColor = preferences[Keys.DYNAMIC_COLOR] ?: defaults.dynamicColor,
            autoClearClipboard = preferences[Keys.AUTO_CLEAR_CLIPBOARD] ?: defaults.autoClearClipboard,
            searchEngine = preferences[Keys.SEARCH_ENGINE]
                ?.let(SearchEngine::fromStoredName)
                ?: defaults.searchEngine,
            videoSearchEngine = preferences[Keys.VIDEO_SEARCH_ENGINE]
                ?.let(VideoSearchEngine::fromStoredName)
                ?: defaults.videoSearchEngine,
            doNotRememberRecentSearches = preferences[Keys.DO_NOT_REMEMBER_RECENT_SEARCHES] ?: defaults.doNotRememberRecentSearches,
            lastSelectedPlatformIndex = preferences[Keys.LAST_SELECTED_PLATFORM_INDEX]
                ?: defaults.lastSelectedPlatformIndex,
            typingContents = preferences[Keys.TYPING_CONTENTS] ?: defaults.typingContents,
            latitude = preferences[Keys.LATITUDE] ?: defaults.latitude,
            longitude = preferences[Keys.LONGITUDE] ?: defaults.longitude,
            haveTappedAddButton = preferences[Keys.HAVE_TAPPED_ADD_BUTTON]
                ?: defaults.haveTappedAddButton,
            customVolume = preferences[Keys.CUSTOM_VOLUME]
                ?.takeUnless { it == Int.MIN_VALUE }
                ?: defaults.customVolume,
            volumeButtonTapCount = preferences[Keys.HAVE_TAPPED_VOLUME_BUTTON]
                ?: defaults.volumeButtonTapCount,
            wheelOfFortuneItems = preferences[Keys.WHEEL_OF_FORTUNE_ITEMS]
                ?: defaults.wheelOfFortuneItems,
            cardShownStates = cardShownStates,
        )
    }

    suspend fun saveShownState(card: String, isShown: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(card)] = isShown }
    }

    suspend fun toggleDynamicColor(enabled: Boolean) {
        dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun toggleAutoClearClipboard(enabled: Boolean) {
        dataStore.edit { it[Keys.AUTO_CLEAR_CLIPBOARD] = enabled }
    }

    suspend fun setSearchEngine(engine: SearchEngine) {
        dataStore.edit { it[Keys.SEARCH_ENGINE] = engine.name }
    }

    suspend fun setVideoSearchEngine(engine: VideoSearchEngine) {
        dataStore.edit { it[Keys.VIDEO_SEARCH_ENGINE] = engine.name }
    }

    suspend fun setDoNotRememberRecentSearches(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DO_NOT_REMEMBER_RECENT_SEARCHES] = enabled
            if (enabled) preferences.remove(Keys.TYPING_CONTENTS)
        }
    }

    suspend fun updateLastSelectedPlatformIndex(index: Int) {
        dataStore.edit { it[Keys.LAST_SELECTED_PLATFORM_INDEX] = index }
    }

    suspend fun updateTypingContents(contents: String) {
        dataStore.edit { preferences ->
            if (preferences[Keys.DO_NOT_REMEMBER_RECENT_SEARCHES] == true) {
                preferences.remove(Keys.TYPING_CONTENTS)
            } else {
                preferences[Keys.TYPING_CONTENTS] = contents
            }
        }
    }

    suspend fun updateLatitude(latitude: String) {
        dataStore.edit { it[Keys.LATITUDE] = latitude }
    }

    suspend fun updateLongitude(longitude: String) {
        dataStore.edit { it[Keys.LONGITUDE] = longitude }
    }

    suspend fun toggleHaveTappedAddButton(haveTapped: Boolean) {
        dataStore.edit { it[Keys.HAVE_TAPPED_ADD_BUTTON] = haveTapped }
    }

    suspend fun updateCustomVolume(value: Int) {
        dataStore.edit { it[Keys.CUSTOM_VOLUME] = value }
    }

    suspend fun increaseVolumeButtonTapCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[Keys.HAVE_TAPPED_VOLUME_BUTTON] ?: 0
            preferences[Keys.HAVE_TAPPED_VOLUME_BUTTON] = currentCount + 1
        }
    }

    suspend fun updateWheelOfFortuneItems(items: String) {
        dataStore.edit { it[Keys.WHEEL_OF_FORTUNE_ITEMS] = items }
    }
}

data class UserSettings(
    val dynamicColor: Boolean,
    val autoClearClipboard: Boolean,
    val searchEngine: SearchEngine,
    val videoSearchEngine: VideoSearchEngine,
    val doNotRememberRecentSearches: Boolean,
    val lastSelectedPlatformIndex: Int,
    val typingContents: String,
    val latitude: String,
    val longitude: String,
    val haveTappedAddButton: Boolean,
    val customVolume: Int?,
    val volumeButtonTapCount: Int,
    val wheelOfFortuneItems: String?,
    val cardShownStates: Map<String, Boolean>,
) {
    companion object {
        fun default(): UserSettings = UserSettings(
            dynamicColor = true,
            autoClearClipboard = false,
            searchEngine = SearchEngine.GOOGLE,
            videoSearchEngine = VideoSearchEngine.YOUTUBE,
            doNotRememberRecentSearches = false,
            lastSelectedPlatformIndex = 0,
            typingContents = "",
            latitude = "",
            longitude = "",
            haveTappedAddButton = false,
            customVolume = null,
            volumeButtonTapCount = 0,
            wheelOfFortuneItems = null,
            cardShownStates = emptyMap(),
        )
    }
}
