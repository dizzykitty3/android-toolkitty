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

private fun Preferences.shownItemStates(): Map<String, Boolean> = asMap()
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

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferenceKeys {
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val AUTO_CLEAR_CLIPBOARD = booleanPreferencesKey("auto_clear_clipboard")
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val VIDEO_SEARCH_ENGINE = stringPreferencesKey("video_search_engine")
        // Keep the legacy key to preserve existing user preferences.
        val DO_NOT_REMEMBER_LAST_SEARCH = booleanPreferencesKey("do_not_remember_recent_searches")
        val LAST_SELECTED_PLATFORM_INDEX = intPreferencesKey("last_selected_platform_index")
        val TYPING_CONTENTS = stringPreferencesKey("typing_contents")
        val LATITUDE = stringPreferencesKey("latitude")
        val LONGITUDE = stringPreferencesKey("longitude")
        val HAVE_TAPPED_ADD_BUTTON = booleanPreferencesKey("have_tapped_add_button")
        val CUSTOM_VOLUME = intPreferencesKey("custom_volume")
        val HAVE_TAPPED_VOLUME_BUTTON = intPreferencesKey("have_tapped_volume_button")
        val WHEEL_OF_FORTUNE_ITEMS = stringPreferencesKey("wheel_of_fortune_items")
        val HOME_CARD_ORDER = stringPreferencesKey("home_card_order")
    }

    val settingsFlow: Flow<UserSettings> = dataStore.data.map { preferences ->
        val defaults = UserSettings.default()
        UserSettings(
            dynamicColor = preferences[PreferenceKeys.DYNAMIC_COLOR] ?: defaults.dynamicColor,
            autoClearClipboard = preferences[PreferenceKeys.AUTO_CLEAR_CLIPBOARD] ?: defaults.autoClearClipboard,
            searchEngine = preferences[PreferenceKeys.SEARCH_ENGINE]
                ?.let(SearchEngine::fromStoredName)
                ?: defaults.searchEngine,
            videoSearchEngine = preferences[PreferenceKeys.VIDEO_SEARCH_ENGINE]
                ?.let(VideoSearchEngine::fromStoredName)
                ?: defaults.videoSearchEngine,
            doNotRememberLastSearch =
                preferences[PreferenceKeys.DO_NOT_REMEMBER_LAST_SEARCH]
                    ?: defaults.doNotRememberLastSearch,
            lastSelectedPlatformIndex = preferences[PreferenceKeys.LAST_SELECTED_PLATFORM_INDEX]
                ?: defaults.lastSelectedPlatformIndex,
            typingContents = preferences[PreferenceKeys.TYPING_CONTENTS] ?: defaults.typingContents,
            latitude = preferences[PreferenceKeys.LATITUDE] ?: defaults.latitude,
            longitude = preferences[PreferenceKeys.LONGITUDE] ?: defaults.longitude,
            haveTappedAddButton = preferences[PreferenceKeys.HAVE_TAPPED_ADD_BUTTON]
                ?: defaults.haveTappedAddButton,
            customVolume = preferences[PreferenceKeys.CUSTOM_VOLUME]
                ?.takeUnless { it == Int.MIN_VALUE }
                ?: defaults.customVolume,
            volumeButtonTapCount = preferences[PreferenceKeys.HAVE_TAPPED_VOLUME_BUTTON]
                ?: defaults.volumeButtonTapCount,
            wheelOfFortuneItems = preferences[PreferenceKeys.WHEEL_OF_FORTUNE_ITEMS]
                ?: defaults.wheelOfFortuneItems,
            shownItemStates = preferences.shownItemStates(),
            homeCardOrder = preferences[PreferenceKeys.HOME_CARD_ORDER]?.split(',') ?: emptyList(),
        )
    }

    private suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { it[key] = value }
    }

    suspend fun saveShownState(itemKey: String, isShown: Boolean) {
        setPreference(booleanPreferencesKey(itemKey), isShown)
    }

    suspend fun moveHomeCard(key: String, offset: Int, defaultOrder: List<String>) {
        if (offset != -1 && offset != 1) return
        dataStore.edit { preferences ->
            val order = normalizedHomeCardOrder(
                preferences[PreferenceKeys.HOME_CARD_ORDER]?.split(',') ?: emptyList(),
                defaultOrder,
            ).toMutableList()
            val visibleOrder = order.filter { preferences[booleanPreferencesKey(it)] != false }
            val index = visibleOrder.indexOf(key)
            val destination = index + offset
            if (index >= 0 && destination in visibleOrder.indices) {
                // Swap visible neighbours without moving hidden cards out of their saved slots.
                val sourceSlot = order.indexOf(key)
                val destinationSlot = order.indexOf(visibleOrder[destination])
                order[sourceSlot] = visibleOrder[destination]
                order[destinationSlot] = key
                preferences[PreferenceKeys.HOME_CARD_ORDER] = order.joinToString(",")
            }
        }
    }

    suspend fun resetHomeCardOrder() {
        dataStore.edit { it.remove(PreferenceKeys.HOME_CARD_ORDER) }
    }

    suspend fun toggleDynamicColor(enabled: Boolean) =
        setPreference(PreferenceKeys.DYNAMIC_COLOR, enabled)

    suspend fun toggleAutoClearClipboard(enabled: Boolean) =
        setPreference(PreferenceKeys.AUTO_CLEAR_CLIPBOARD, enabled)

    suspend fun setSearchEngine(engine: SearchEngine) =
        setPreference(PreferenceKeys.SEARCH_ENGINE, engine.name)

    suspend fun setVideoSearchEngine(engine: VideoSearchEngine) =
        setPreference(PreferenceKeys.VIDEO_SEARCH_ENGINE, engine.name)

    suspend fun setDoNotRememberLastSearch(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.DO_NOT_REMEMBER_LAST_SEARCH] = enabled
            if (enabled) preferences.remove(PreferenceKeys.TYPING_CONTENTS)
        }
    }

    suspend fun updateLastSelectedPlatformIndex(index: Int) =
        setPreference(PreferenceKeys.LAST_SELECTED_PLATFORM_INDEX, index)

    suspend fun updateTypingContents(contents: String) {
        dataStore.edit { preferences ->
            if (preferences[PreferenceKeys.DO_NOT_REMEMBER_LAST_SEARCH] == true) {
                preferences.remove(PreferenceKeys.TYPING_CONTENTS)
            } else {
                preferences[PreferenceKeys.TYPING_CONTENTS] = contents
            }
        }
    }

    suspend fun updateLatitude(latitude: String) =
        setPreference(PreferenceKeys.LATITUDE, latitude)

    suspend fun updateLongitude(longitude: String) =
        setPreference(PreferenceKeys.LONGITUDE, longitude)

    suspend fun toggleHaveTappedAddButton(haveTapped: Boolean) =
        setPreference(PreferenceKeys.HAVE_TAPPED_ADD_BUTTON, haveTapped)

    suspend fun updateCustomVolume(value: Int) =
        setPreference(PreferenceKeys.CUSTOM_VOLUME, value)

    suspend fun increaseVolumeButtonTapCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[PreferenceKeys.HAVE_TAPPED_VOLUME_BUTTON] ?: 0
            preferences[PreferenceKeys.HAVE_TAPPED_VOLUME_BUTTON] = currentCount + 1
        }
    }

    suspend fun updateWheelOfFortuneItems(items: String) =
        setPreference(PreferenceKeys.WHEEL_OF_FORTUNE_ITEMS, items)
}

data class UserSettings(
    val dynamicColor: Boolean,
    val autoClearClipboard: Boolean,
    val searchEngine: SearchEngine,
    val videoSearchEngine: VideoSearchEngine,
    val doNotRememberLastSearch: Boolean,
    val lastSelectedPlatformIndex: Int,
    val typingContents: String,
    val latitude: String,
    val longitude: String,
    val haveTappedAddButton: Boolean,
    val customVolume: Int?,
    val volumeButtonTapCount: Int,
    val wheelOfFortuneItems: String?,
    val shownItemStates: Map<String, Boolean>,
    val homeCardOrder: List<String> = emptyList(),
) {
    fun isShown(itemKey: String): Boolean = shownItemStates[itemKey] ?: true

    companion object {
        fun default(): UserSettings = UserSettings(
            dynamicColor = true,
            autoClearClipboard = false,
            searchEngine = SearchEngine.GOOGLE,
            videoSearchEngine = VideoSearchEngine.YOUTUBE,
            doNotRememberLastSearch = false,
            lastSelectedPlatformIndex = 0,
            typingContents = "",
            latitude = "",
            longitude = "",
            haveTappedAddButton = false,
            customVolume = null,
            volumeButtonTapCount = 0,
            wheelOfFortuneItems = null,
            shownItemStates = emptyMap(),
        )
    }
}
