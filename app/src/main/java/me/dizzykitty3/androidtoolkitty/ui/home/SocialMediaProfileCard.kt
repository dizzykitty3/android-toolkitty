package me.dizzykitty3.androidtoolkitty.ui.home

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomDropdownMenu
import me.dizzykitty3.androidtoolkitty.uicomponents.ErrorTip
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.Tip
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openURL
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar
import me.dizzykitty3.androidtoolkitty.utils.URLUtil
import timber.log.Timber

@Composable
internal fun SocialMediaProfile() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val focus = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    var username by remember { mutableStateOf("") }
    var lastSelectedPlatformIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(state.typingContents, state.lastSelectedPlatformIndex) {
        Timber.i("LaunchedEffect")
        if (username != state.typingContents) {
            username = state.typingContents
        }
        val platformIndex = state.lastSelectedPlatformIndex.coerceIn(
            0, URLUtil.Platform.entries.lastIndex
        )
        if (lastSelectedPlatformIndex != platformIndex) {
            Timber.d("state.lastSelectedPlatformIndex = ${state.lastSelectedPlatformIndex}")
            lastSelectedPlatformIndex = platformIndex
            if (platformIndex != state.lastSelectedPlatformIndex) {
                vm.updateLastSelectedPlatformIndex(platformIndex)
            }
            Timber.d("platform = ${view.context.getString(URLUtil.Platform.entries[lastSelectedPlatformIndex].platform)}")
        }
        isLoading = false
    }

    if (isLoading) {
        CircularProgressIndicator()
        return
    }

    CustomDropdownMenu(
        items = URLUtil.Platform.entries.map { stringResource(it.platform) },
        onItemSelected = { lastSelectedPlatformIndex = it },
        label = { Text(stringResource(R.string.platform)) },
        selectedPlatformIndex = lastSelectedPlatformIndex
    )

    val selectedPlatform = URLUtil.Platform.entries[lastSelectedPlatformIndex]
    OutlinedTextField(
        value = username,
        onValueChange = {
            username = it
            vm.updateTypingContents(it)
        },
        label = { Text(stringResource(R.string.username)) },
        isError = !isValid(selectedPlatform, username),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
                view.visitProfileOrShowError(
                    username,
                    lastSelectedPlatformIndex,
                    vm
                )
            }
        ),
        trailingIcon = {
            ClearInput(username) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                username = ""
                vm.updateTypingContents("")
            }
        },
        supportingText = {
            Text(
                toProfileFullURL(selectedPlatform, username),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    )

    if (isCaseSensitive(selectedPlatform)) {
        SpacerPadding()
        Tip(R.string.tip_case_sensitive)
    } else if (isInvalidCommonRule(selectedPlatform, username)) {
        SpacerPadding()
        ErrorTip(
            stringResource(
                R.string.invalid_username_common_rule,
                stringResource(selectedPlatform.platform)
            )
        )
    } else if (isInvalidNotNumbersOnly(selectedPlatform, username)) {
        SpacerPadding()
        ErrorTip(
            stringResource(
                R.string.invalid_username_numbers_only,
                stringResource(selectedPlatform.platform)
            )
        )
    }

    TextButton(onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        focus.clearFocus()
        view.visitProfileOrShowError(
            username,
            lastSelectedPlatformIndex,
            vm
        )
    }) {
        Text(stringResource(R.string.visit))
        Icon(
            imageVector = Icons.Outlined.ArrowOutward,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
        )
    }
}

private fun View.visitProfileOrShowError(
    username: String,
    platformIndex: Int,
    viewModel: SettingsViewModel,
) {
    if (username.isBlank()) return
    val platform = URLUtil.Platform.entries.getOrNull(platformIndex) ?: return
    if (isValid(platform, username)) {
        this.context.openURL(toProfileFullURL(platform, username))
        viewModel.updateLastSelectedPlatformIndex(platformIndex)
    } else {
        showSnackbar(R.string.invalid_username_tip)
    }
}
