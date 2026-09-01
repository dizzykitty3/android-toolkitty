package me.dizzykitty3.androidtoolkitty.home

import android.view.View
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.ContentPasteSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.clearClipboard
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtils.showSnackbar
import timber.log.Timber

@Composable
fun Clipboard() {
    BaseCard(title = R.string.clipboard, icon = Icons.Outlined.ContentPasteSearch) {
        val view = LocalView.current
        val haptic = LocalHapticFeedback.current

        OutlinedButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.onClearClipboardButton()
        }) {
            Icon(
                imageVector = Icons.Outlined.ClearAll,
                contentDescription = stringResource(R.string.clear_clipboard),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            SpacerPadding()
            Text(stringResource(R.string.clear_clipboard))
        }
    }
}

private fun View.onClearClipboardButton() {
    val cleared = context.clearClipboard()
    Timber.i("Clipboard cleared")
    this.showSnackbar(if (cleared) R.string.clipboard_cleared else R.string.clipboard_is_empty)
}
