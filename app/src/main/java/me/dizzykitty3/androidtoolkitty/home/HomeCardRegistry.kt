package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.runtime.Composable
import me.dizzykitty3.androidtoolkitty.CARD_1
import me.dizzykitty3.androidtoolkitty.CARD_10
import me.dizzykitty3.androidtoolkitty.CARD_11
import me.dizzykitty3.androidtoolkitty.CARD_12
import me.dizzykitty3.androidtoolkitty.CARD_2
import me.dizzykitty3.androidtoolkitty.CARD_3
import me.dizzykitty3.androidtoolkitty.CARD_4
import me.dizzykitty3.androidtoolkitty.CARD_5
import me.dizzykitty3.androidtoolkitty.CARD_6
import me.dizzykitty3.androidtoolkitty.CARD_7
import me.dizzykitty3.androidtoolkitty.CARD_8
import me.dizzykitty3.androidtoolkitty.CARD_9
import me.dizzykitty3.androidtoolkitty.datastore.UserSettings

/**
 * The single source of truth for the cards shown on the home screen.
 *
 * Keeping the id and the UI together prevents a new card from requiring
 * changes in several unrelated places.
 */
class HomeCardDefinition(
    val id: String,
    val content: @Composable () -> Unit,
)

val homeCardDefinitions = listOf(
    HomeCardDefinition(CARD_1) { YearProgress() },
    HomeCardDefinition(CARD_2) { Volume() },
    HomeCardDefinition(CARD_3) { Clipboard() },
    HomeCardDefinition(CARD_4) { Search() },
    HomeCardDefinition(CARD_5) { SysSettings() },
    HomeCardDefinition(CARD_6) { WheelOfFortune() },
    HomeCardDefinition(CARD_7) { BluetoothDevice() },
    HomeCardDefinition(CARD_8) { CodesOfCharacters() },
    HomeCardDefinition(CARD_9) { Maps() },
    HomeCardDefinition(CARD_10) { FontWeight() },
    HomeCardDefinition(CARD_11) { ComposeCatalog() },
    HomeCardDefinition(CARD_12) { HapticFeedback() },
)

fun UserSettings.visibleHomeCards(): List<HomeCardDefinition> =
    homeCardDefinitions.filter { card -> cardShownStates[card.id] ?: true }
