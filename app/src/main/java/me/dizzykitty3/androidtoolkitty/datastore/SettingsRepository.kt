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
import javax.inject.Inject

@Serializable
data class WheelOfFortuneItems(val items: List<String>)

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val AUTO_CLEAR_CLIPBOARD = booleanPreferencesKey("auto_clear_clipboard")
        val SWITCH_TO_BING_SEARCH = booleanPreferencesKey("switch_to_bing_search")
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
            dynamicColor = preferences[Keys.DYNAMIC_COLOR] ?: true,
            autoClearClipboard = preferences[Keys.AUTO_CLEAR_CLIPBOARD] ?: false,
            switchToBingSearch = preferences[Keys.SWITCH_TO_BING_SEARCH] ?: false,
            lastSelectedPlatformIndex = preferences[Keys.LAST_SELECTED_PLATFORM_INDEX] ?: 0,
            typingContents = preferences[Keys.TYPING_CONTENTS] ?: "",
            latitude = preferences[Keys.LATITUDE] ?: "",
            longitude = preferences[Keys.LONGITUDE] ?: "",
            haveTappedAddButton = preferences[Keys.HAVE_TAPPED_ADD_BUTTON] ?: false,
            customVolume = preferences[Keys.CUSTOM_VOLUME] ?: Int.MIN_VALUE,
            haveTappedVolumeButton = preferences[Keys.HAVE_TAPPED_VOLUME_BUTTON] ?: 0,
            wheelOfFortuneItems = preferences[Keys.WHEEL_OF_FORTUNE_ITEMS],
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

    suspend fun toggleSwitchToBingSearch(enabled: Boolean) {
        dataStore.edit { it[Keys.SWITCH_TO_BING_SEARCH] = enabled }
    }

    suspend fun updateLastSelectedPlatformIndex(index: Int) {
        dataStore.edit { it[Keys.LAST_SELECTED_PLATFORM_INDEX] = index }
    }

    suspend fun updateTypingContents(contents: String) {
        dataStore.edit { it[Keys.TYPING_CONTENTS] = contents }
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

    suspend fun increaseHaveTappedVolumeButton() {
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
    val switchToBingSearch: Boolean,
    val lastSelectedPlatformIndex: Int,
    val typingContents: String,
    val latitude: String,
    val longitude: String,
    val haveTappedAddButton: Boolean,
    val customVolume: Int,
    val haveTappedVolumeButton: Int,
    val wheelOfFortuneItems: String?,
    val cardShownStates: Map<String, Boolean>,
)
