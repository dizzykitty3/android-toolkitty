package me.dizzykitty3.androidtoolkitty.home

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Map
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.checkOnGoogleMaps
import timber.log.Timber

@Composable
fun Maps() {
    BaseCard(title = R.string.maps, icon = Icons.Outlined.Map) {
        val vm = LocalSettingsViewModel.current
        val state by vm.settingsState.collectAsStateWithLifecycle()
        val view = LocalView.current
        val focus = LocalFocusManager.current
        val haptic = LocalHapticFeedback.current
        val focusRequester1 = remember { FocusRequester() }
        val focusRequester2 = remember { FocusRequester() }
        var latitude by remember { mutableStateOf("") }
        var longitude by remember { mutableStateOf("") }

        LaunchedEffect(state.latitude) {
            if (latitude != state.latitude) {
                latitude = state.latitude
            }
        }

        OutlinedTextField(
            value = latitude,
            onValueChange = { input ->
                val sanitizedInput = sanitizeCoordinateInput(input)
                latitude = sanitizedInput
                vm.updateLatitude(sanitizedInput)
            },
            suffix = {
                Text(
                    latitude.getLatitudeSuffix(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3F)
                )
            },
            label = { Text(stringResource(R.string.latitude)) },
            isError = latitude.errorLatitude(),
            supportingText = { Text(stringResource(R.string.latitude_description)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester1),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (longitude.isBlank()) {
                        focusRequester2.requestFocus()
                    } else {
                        focus.clearFocus()
                        view.context.onClickOpenGoogleMapsButton(latitude, longitude)
                    }
                }),
            trailingIcon = {
                ClearInput(latitude) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    latitude = ""
                    vm.updateLatitude("")
                }
            },
        )

        LaunchedEffect(state.longitude) {
            if (longitude != state.longitude) {
                longitude = state.longitude
            }
        }

        OutlinedTextField(
            value = longitude,
            onValueChange = { input ->
                val sanitizedInput = sanitizeCoordinateInput(input)
                longitude = sanitizedInput
                vm.updateLongitude(sanitizedInput)
            },
            suffix = {
                Text(
                    longitude.getLongitudeSuffix(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3F)
                )
            },
            isError = longitude.errorLongitude(),
            label = { Text(stringResource(R.string.longitude)) },
            supportingText = { Text(stringResource(R.string.longitude_description)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester2),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (latitude.isBlank()) {
                        focusRequester1.requestFocus()
                    } else {
                        focus.clearFocus()
                        view.context.onClickOpenGoogleMapsButton(latitude, longitude)
                    }
                }),
            trailingIcon = {
                ClearInput(longitude) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    longitude = ""
                    vm.updateLongitude("")
                }
            },
        )

        TextButton({
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            focus.clearFocus()
            view.context.onClickOpenGoogleMapsButton(latitude, longitude)
        }) {
            Text(stringResource(R.string.google_maps))
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = stringResource(R.string.google_maps),
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
            )
        }
    }
}

private fun Context.onClickOpenGoogleMapsButton(latitude: String, longitude: String) {
    if (latitude.errorLatitude() || longitude.errorLongitude()) return

    Timber.d("onClickOpenGoogleMapsButton")
    this.checkOnGoogleMaps(latitude, longitude)
}

private fun String.getLatitudeSuffix(): String {
    try {
        val input = this.toFloat()
        if (input > 0F && input <= 90F) return "N"
        if (input < 0F && input >= -90F) return "S"
    } catch (_: NumberFormatException) {
    }
    return ""
}

private fun String.getLongitudeSuffix(): String {
    try {
        val input = this.toFloat()
        if (input > 0F && input <= 180F) return "E"
        if (input < 0F && input >= -180F) return "W"
    } catch (_: NumberFormatException) {
    }
    return ""
}

private fun String.errorLatitude(): Boolean {
    return !(this.isBlank() || this.getLatitudeSuffix() == "N" || this.getLatitudeSuffix() == "S")
}

private fun String.errorLongitude(): Boolean {
    return !(this.isBlank() || this.getLongitudeSuffix() == "E" || this.getLongitudeSuffix() == "W")
}

private fun sanitizeCoordinateInput(input: String): String {
    return buildString {
        var hasMinus = false
        var hasDot = false

        input.forEachIndexed { index, c ->
            when {
                c.isDigit() -> append(c)
                c == '-' && index == 0 && !hasMinus -> {
                    append(c)
                    hasMinus = true
                }

                c == '.' && !hasDot -> {
                    append(c)
                    hasDot = true
                }
            }
        }
    }
}
