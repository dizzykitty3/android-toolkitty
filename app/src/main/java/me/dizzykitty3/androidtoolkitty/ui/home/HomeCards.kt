package me.dizzykitty3.androidtoolkitty.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel

@Composable
fun HomeCards(viewModel: SettingsViewModel) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    state.visibleHomeCards().forEach { card ->
        key(card.id) { card.content() }
    }
}

@Composable
fun TwoColumnHomeCards(viewModel: SettingsViewModel, gridState: LazyStaggeredGridState) {
    val cardPadding = dimensionResource(R.dimen.padding_card_space)
    val largeCardPadding = dimensionResource(R.dimen.padding_card_space_large)
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val cards = state.visibleHomeCards()

    LazyVerticalStaggeredGrid(
        state = gridState,
        contentPadding = PaddingValues(bottom = 80.dp),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = cardPadding,
        horizontalArrangement = Arrangement.spacedBy(largeCardPadding),
    ) {
        items(cards, key = { it.id }) { card ->
            card.content()
        }
    }
}
