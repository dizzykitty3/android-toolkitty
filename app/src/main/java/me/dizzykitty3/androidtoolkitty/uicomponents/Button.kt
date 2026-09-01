package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openSystemSettings

@Composable
fun SystemSettingButton(
    settingType: String,
    @StringRes text: Int,
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    TextButton({
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        view.context.openSystemSettings(settingType)
    }) {
        Text(stringResource(text))

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
        )
    }
}

/**
 * TrailingIcon param of a @Composable TextField, to clear the user input.
 */
@Composable
fun ClearInput(input: String, onClick: () -> Unit) {
    if (input.isNotEmpty()) {
        IconButton(onClick) {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = stringResource(R.string.clear_input),
            )
        }
    }
}