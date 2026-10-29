package me.dizzykitty3.androidtoolkitty.ui.home

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.UserSettings
import me.dizzykitty3.androidtoolkitty.datastore.normalizedHomeCardOrder
import me.dizzykitty3.androidtoolkitty.home.BluetoothDevice
import me.dizzykitty3.androidtoolkitty.home.CharacterCodes
import me.dizzykitty3.androidtoolkitty.home.Clipboard
import me.dizzykitty3.androidtoolkitty.home.ComposeCatalog
import me.dizzykitty3.androidtoolkitty.home.FontWeight
import me.dizzykitty3.androidtoolkitty.home.HapticFeedback
import me.dizzykitty3.androidtoolkitty.home.Maps
import me.dizzykitty3.androidtoolkitty.home.Search
import me.dizzykitty3.androidtoolkitty.home.SystemShortcuts
import me.dizzykitty3.androidtoolkitty.home.Volume
import me.dizzykitty3.androidtoolkitty.home.WheelOfFortune
import me.dizzykitty3.androidtoolkitty.home.YearProgress

/**
 * The single source of truth for the cards shown on the home screen.
 *
 * Keeping the id and the UI together prevents a new card from requiring
 * changes in several unrelated places.
 */
// Preference keys are stable storage identifiers; retain them to preserve saved visibility and order.
enum class HomeCardId(
    val preferenceKey: String,
    @param:StringRes val title: Int,
) {
    YEAR_PROGRESS("card_year_progress", R.string.date_time_tools),
    SEARCH("card_webpage", R.string.search_and_visit),
    CLIPBOARD("card_clipboard", R.string.clipboard),
    SYSTEM_SHORTCUTS("card_sys_setting", R.string.system_shortcuts),
    VOLUME("card_volume", R.string.volume),
    BLUETOOTH_DEVICE("card_bluetooth_device", R.string.bluetooth_devices),
    MAPS("card_google_maps", R.string.maps),
    WHEEL_OF_FORTUNE("card_wheel_of_fortune", R.string.wheel_of_fortune),
    CHARACTER_CODES("card_unicode", R.string.character_codes),
    FONT_WEIGHT("card_font_weight", R.string.font_test),
    COMPOSE_CATALOG("card_compose_catalog", R.string.compose_catalog),
    HAPTIC_FEEDBACK("card_haptic_feedback", R.string.haptic_test),
}

class HomeCardDefinition(
    val id: HomeCardId,
    val content: @Composable () -> Unit,
)

val homeCardDefinitions = listOf(
    HomeCardDefinition(HomeCardId.YEAR_PROGRESS) { YearProgress() },
    HomeCardDefinition(HomeCardId.SEARCH) { Search() },
    HomeCardDefinition(HomeCardId.CLIPBOARD) { Clipboard() },
    HomeCardDefinition(HomeCardId.SYSTEM_SHORTCUTS) { SystemShortcuts() },
    HomeCardDefinition(HomeCardId.VOLUME) { Volume() },
    HomeCardDefinition(HomeCardId.BLUETOOTH_DEVICE) { BluetoothDevice() },
    HomeCardDefinition(HomeCardId.MAPS) { Maps() },
    HomeCardDefinition(HomeCardId.WHEEL_OF_FORTUNE) { WheelOfFortune() },
    HomeCardDefinition(HomeCardId.CHARACTER_CODES) { CharacterCodes() },
    HomeCardDefinition(HomeCardId.FONT_WEIGHT) { FontWeight() },
    HomeCardDefinition(HomeCardId.COMPOSE_CATALOG) { ComposeCatalog() },
    HomeCardDefinition(HomeCardId.HAPTIC_FEEDBACK) { HapticFeedback() },
)

fun UserSettings.orderedHomeCards(): List<HomeCardDefinition> {
    val byKey = homeCardDefinitions.associateBy { it.id.preferenceKey }
    return normalizedHomeCardOrder(homeCardOrder, byKey.keys.toList()).map { byKey.getValue(it) }
}

fun UserSettings.visibleHomeCards(): List<HomeCardDefinition> =
    orderedHomeCards().filter { card -> isShown(card.id.preferenceKey) }
