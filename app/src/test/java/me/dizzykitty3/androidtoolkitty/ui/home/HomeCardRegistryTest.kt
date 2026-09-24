package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.Context
import me.dizzykitty3.androidtoolkitty.datastore.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeCardRegistryTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    fun orderedHomeCards_ignoresUnknownAndDuplicateKeysAndAppendsMissingCards() {
        val maps = HomeCardId.MAPS.preferenceKey
        val settings = UserSettings.default().copy(homeCardOrder = listOf("removed_card", maps, maps))
        assertEquals(
            listOf(HomeCardId.MAPS) + HomeCardId.entries.filter { it != HomeCardId.MAPS },
            settings.orderedHomeCards().map { it.id },
        )
    }

    @Test
    fun visibleHomeCards_hidingAndRestoringCardPreservesCustomPosition() {
        val order = HomeCardId.entries.reversed().map { it.preferenceKey }
        val settings = UserSettings.default().copy(homeCardOrder = order)
        val hidden = settings.copy(shownItemStates = mapOf(HomeCardId.MAPS.preferenceKey to false))
        assertEquals(order.filter { it != HomeCardId.MAPS.preferenceKey },
            hidden.visibleHomeCards().map { it.id.preferenceKey })
        assertEquals(order, hidden.copy(shownItemStates = emptyMap()).visibleHomeCards().map { it.id.preferenceKey })
    }

    @Test
    fun homeCardDefinitions_containEveryCardIdExactlyOnce() {
        val cardIds = homeCardDefinitions.map { it.id }

        assertEquals(HomeCardId.entries.toList(), cardIds)
        assertEquals(cardIds.size, cardIds.distinct().size)
    }

    @Test
    fun everyHomeCardId_usesTheSettingsRepositoryCardKeyConvention() {
        HomeCardId.entries.forEach { cardId ->
            assertTrue(cardId.preferenceKey.startsWith("card_"))
        }
    }

    @Test
    fun everyHomeCardId_hasANonBlankTitleResource() {
        HomeCardId.entries.forEach { cardId ->
            assertTrue(context.getString(cardId.title).isNotBlank())
        }
    }

    @Test
    fun visibleHomeCards_respectsPersistedVisibilityAndKeepsRegistryOrder() {
        val settings = UserSettings.default().copy(
            shownItemStates = mapOf(
                HomeCardId.SEARCH.preferenceKey to false,
                HomeCardId.MAPS.preferenceKey to false,
            ),
        )

        val visibleIds = settings.visibleHomeCards().map { it.id }
        assertTrue(HomeCardId.SEARCH !in visibleIds)
        assertTrue(HomeCardId.MAPS !in visibleIds)
        assertEquals(HomeCardId.YEAR_PROGRESS, visibleIds.first())
        assertEquals(HomeCardId.HAPTIC_FEEDBACK, visibleIds.last())
    }

    @Test
    fun visibleHomeCards_returnsTheFullRegistryForDefaultSettings() {
        assertEquals(homeCardDefinitions, UserSettings.default().visibleHomeCards())
    }

    @Test
    fun visibleHomeCards_returnsNoCardsWhenEveryCardIsHidden() {
        val settings = UserSettings.default().copy(
            shownItemStates = HomeCardId.entries.associate { it.preferenceKey to false },
        )

        assertTrue(settings.visibleHomeCards().isEmpty())
    }
}
