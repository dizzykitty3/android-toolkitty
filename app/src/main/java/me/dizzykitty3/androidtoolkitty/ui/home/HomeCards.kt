package me.dizzykitty3.androidtoolkitty.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.visibleHomeCards

@Composable
fun HomeCards(viewModel: SettingsViewModel) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    state.visibleHomeCards().forEach { card ->
        card.content()
    }
}

@Composable
fun TwoColumnHomeCards(viewModel: SettingsViewModel) {
    val cardPadding = dimensionResource(R.dimen.padding_card_space)
    val largeCardPadding = dimensionResource(R.dimen.padding_card_space_large)
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val cards = state.visibleHomeCards()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = cardPadding,
        horizontalArrangement = Arrangement.spacedBy(largeCardPadding),
    ) {
        items(cards, key = { it.id }) { card ->
            card.content()
        }
    }
}
