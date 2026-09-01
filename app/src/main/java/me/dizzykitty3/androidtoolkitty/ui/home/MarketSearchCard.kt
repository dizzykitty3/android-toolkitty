package me.dizzykitty3.androidtoolkitty.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.ButtonDivider
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.checkOnMarket

@Composable
internal fun CheckAppOnMarket() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val focus = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    var packageName by remember { mutableStateOf("") }

    LaunchedEffect(state.typingContents) {
        if (packageName != state.typingContents) {
            packageName = state.typingContents
        }
    }

    OutlinedTextField(
        value = packageName,
        onValueChange = {
            packageName = it
            vm.updateTypingContents(it)
        },
        label = { Text(stringResource(R.string.package_name_or_search)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
                view.context.checkOnMarket(packageName)
            }
        ),
        trailingIcon = {
            ClearInput(packageName) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                packageName = ""
                vm.updateTypingContents("")
            }
        }
    )

    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton({
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            focus.clearFocus()
            view.context.checkOnMarket(packageName)
        }) {
            Text(stringResource(R.string.open_on_google_play))
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = stringResource(R.string.check_app_on_market),
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
            )
        }
        ButtonDivider()
        TextButton({
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            focus.clearFocus()
            view.context.checkOnMarket(packageName, false)
        }) {
            Text(stringResource(R.string.open_on_other_markets))
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = stringResource(R.string.open_on_other_markets),
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
            )
        }
    }
}
