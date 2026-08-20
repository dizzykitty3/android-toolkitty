package me.dizzykitty3.androidtoolkitty.home

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.ui.home.SearchActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ButtonDivider
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openSearch
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.searchOnYouTube
import timber.log.Timber

@Composable
fun Search() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    BaseCard(
        title = R.string.search, icon = Icons.Outlined.Search, hasShowMore = true, onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(SearchActivity::class.java)
        }) { SearchComposable() }
}

@Composable
private fun SearchComposable() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val focus = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    var query by remember { mutableStateOf("") }

    LaunchedEffect(state.typingContents) {
        if (query != state.typingContents) {
            query = state.typingContents
        }
    }

    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
            vm.updateTypingContents(it)
        },
        label = { Text(stringResource(R.string.query)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
                view.context.onTapSearchButton(query, state.switchToBingSearch)
            }),
        trailingIcon = {
            ClearInput(query) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                query = ""
                vm.updateTypingContents("")
            }
        },
    )

    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton({
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            focus.clearFocus()
            view.context.onTapSearchButton(query, state.switchToBingSearch)
        }) {
            Text(stringResource(R.string.search))
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
            )
        }
        ButtonDivider()
        TextButton({
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            focus.clearFocus()
            view.context.onTapCheckOnYouTubeButton(query, state.switchToBingSearch)
        }) {
            Text(stringResource(if (state.switchToBingSearch) R.string.search_on_bilibili else R.string.search_on_youtube))
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
            )
        }
    }
}

private fun Context.onTapSearchButton(query: String, bingSearch: Boolean = false) {
    if (query.isBlank()) return
    Timber.d("onTapSearchButton ${if (bingSearch) "Bing" else "Google"}")
    this.openSearch(query, bingSearch)
}

private fun Context.onTapCheckOnYouTubeButton(query: String, bingSearch: Boolean = false) {
    if (query.isBlank()) return
    Timber.d("onTapCheckOnYouTubeButton")
    this.searchOnYouTube(query, bingSearch)
}
