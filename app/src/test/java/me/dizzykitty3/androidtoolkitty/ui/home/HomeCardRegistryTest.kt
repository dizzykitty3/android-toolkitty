package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.dizzykitty3.androidtoolkitty.datastore.SettingsRepository
import me.dizzykitty3.androidtoolkitty.datastore.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeCardRegistryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun persistedCardCustomization_drivesHomeListAndResetKeepsVisibility() = runTest {
        val file = File(temporaryFolder.root, "home.preferences_pb")
        val repository = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) { file },
        )
        val defaults = homeCardDefinitions.map { it.id.preferenceKey }
        repository.saveShownState(HomeCardId.VOLUME.preferenceKey, false)
        repository.saveShownState(HomeCardId.CLIPBOARD.preferenceKey, false)
        repository.moveHomeCard(HomeCardId.SEARCH.preferenceKey, -1, defaults)

        val moved = repository.settingsFlow.first()
        val expectedOrder = listOf(
            HomeCardId.SEARCH, HomeCardId.VOLUME, HomeCardId.CLIPBOARD, HomeCardId.YEAR_PROGRESS,
        ) + HomeCardId.entries.drop(4)
        assertEquals(expectedOrder, moved.orderedHomeCards().map { it.id })
        assertEquals(
            listOf(HomeCardId.SEARCH, HomeCardId.YEAR_PROGRESS) + HomeCardId.entries.drop(4),
            moved.visibleHomeCards().map { it.id },
        )

        repository.resetHomeCardOrder()
        val reset = repository.settingsFlow.first()
        assertEquals(homeCardDefinitions, reset.orderedHomeCards())
        assertEquals(
            HomeCardId.entries.filter { it != HomeCardId.VOLUME && it != HomeCardId.CLIPBOARD },
            reset.visibleHomeCards().map { it.id },
        )

        repository.saveShownState(HomeCardId.VOLUME.preferenceKey, true)
        repository.saveShownState(HomeCardId.CLIPBOARD.preferenceKey, true)
        assertEquals(homeCardDefinitions, repository.settingsFlow.first().visibleHomeCards())
    }

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
