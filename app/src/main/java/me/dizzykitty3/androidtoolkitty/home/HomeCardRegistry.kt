package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.runtime.Composable
import androidx.annotation.StringRes
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.UserSettings

/**
 * The single source of truth for the cards shown on the home screen.
 *
 * Keeping the id and the UI together prevents a new card from requiring
 * changes in several unrelated places.
 */
enum class HomeCardId(
    val key: String,
    @param:StringRes val title: Int,
) {
    YEAR_PROGRESS("card_year_progress", R.string.year_progress),
    VOLUME("card_volume", R.string.volume),
    CLIPBOARD("card_clipboard", R.string.clipboard),
    SEARCH("card_webpage", R.string.search),
    SYSTEM_SETTINGS("card_sys_setting", R.string.system_shortcuts),
    WHEEL_OF_FORTUNE("card_wheel_of_fortune", R.string.wheel_of_fortune),
    BLUETOOTH_DEVICE("card_bluetooth_device", R.string.bluetooth_devices),
    CHARACTER_CODES("card_unicode", R.string.codes_of_characters),
    MAPS("card_google_maps", R.string.maps),
    FONT_WEIGHT("card_font_weight", R.string.font_weight_test),
    COMPOSE_CATALOG("card_compose_catalog", R.string.compose),
    HAPTIC_FEEDBACK("card_haptic_feedback", R.string.haptic_test),
}

class HomeCardDefinition(
    val id: HomeCardId,
    val content: @Composable () -> Unit,
)

val homeCardDefinitions = listOf(
    HomeCardDefinition(HomeCardId.YEAR_PROGRESS) { YearProgress() },
    HomeCardDefinition(HomeCardId.VOLUME) { Volume() },
    HomeCardDefinition(HomeCardId.CLIPBOARD) { Clipboard() },
    HomeCardDefinition(HomeCardId.SEARCH) { Search() },
    HomeCardDefinition(HomeCardId.SYSTEM_SETTINGS) { SysSettings() },
    HomeCardDefinition(HomeCardId.WHEEL_OF_FORTUNE) { WheelOfFortune() },
    HomeCardDefinition(HomeCardId.BLUETOOTH_DEVICE) { BluetoothDevice() },
    HomeCardDefinition(HomeCardId.CHARACTER_CODES) { CodesOfCharacters() },
    HomeCardDefinition(HomeCardId.MAPS) { Maps() },
    HomeCardDefinition(HomeCardId.FONT_WEIGHT) { FontWeight() },
    HomeCardDefinition(HomeCardId.COMPOSE_CATALOG) { ComposeCatalog() },
    HomeCardDefinition(HomeCardId.HAPTIC_FEEDBACK) { HapticFeedback() },
)

fun UserSettings.visibleHomeCards(): List<HomeCardDefinition> =
    homeCardDefinitions.filter { card -> cardShownStates[card.id.key] ?: true }
