package me.dizzykitty3.androidtoolkitty.home

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.ui.home.CodesOfCharactersActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.uicomponents.ItalicText
import me.dizzykitty3.androidtoolkitty.utils.copyToClipboard
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtils.showSnackbar
import me.dizzykitty3.androidtoolkitty.utils.StringUtils
import timber.log.Timber

@Composable
fun CodesOfCharacters() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(
        title = R.string.codes_of_characters,
        icon = Icons.AutoMirrored.Outlined.Notes,
        hasShowMore = true,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(CodesOfCharactersActivity::class.java)
        }) { Unicode() }
}

@Composable
fun Unicode() {
    var unicode by remember { mutableStateOf("") }
    var characters by remember { mutableStateOf("") }
    var inputMode by remember { mutableStateOf(ConversionInput.NONE) }
    val view = LocalView.current
    val focus = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val convertUnicode = {
        focus.clearFocus()
        view.onClickConvertButton(unicode, { characters = it }, ConversionDirection.UNICODE_TO_CHARACTER)
    }
    val convertCharacters = {
        focus.clearFocus()
        view.onClickConvertButton(characters, { unicode = it }, ConversionDirection.CHARACTER_TO_UNICODE)
    }

    OutlinedTextField(
        value = unicode,
        onValueChange = {
            unicode = it
            characters = ""
            inputMode = ConversionInput.UNICODE
        },
        label = { Text(stringResource(R.string.unicode)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        supportingText = {
            Text(buildAnnotatedString {
                append(stringResource(R.string.unicode_input_hint))
                ItalicText(" 00610062")
            })
        },
        keyboardActions = KeyboardActions(
            onDone = {
                if (inputMode == ConversionInput.UNICODE) convertUnicode()
            }),
        trailingIcon = {
            ClearInput(unicode) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                unicode = ""
            }
        },
    )

    OutlinedTextField(
        value = characters,
        onValueChange = {
            characters = it
            unicode = ""
            inputMode = ConversionInput.CHARACTER
        },
        label = { Text(stringResource(R.string.character)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (inputMode == ConversionInput.CHARACTER) convertCharacters()
            }),
        trailingIcon = {
            ClearInput(characters) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                characters = ""
            }
        })

    TextButton(onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        focus.clearFocus()
        when (inputMode) {
            ConversionInput.UNICODE -> convertUnicode()
            ConversionInput.CHARACTER -> convertCharacters()
            ConversionInput.NONE -> Unit
        }
    }) { Text(stringResource(R.string.convert)) }
}

private enum class ConversionInput {
    NONE,
    UNICODE,
    CHARACTER,
}

private enum class ConversionDirection {
    UNICODE_TO_CHARACTER,
    CHARACTER_TO_UNICODE,
}

private fun View.onClickConvertButton(
    input: String,
    updateResult: (String) -> Unit,
    direction: ConversionDirection,
) {
    if (input.isBlank()) return

    Timber.d("onClickConvertButton")

    try {
        val result = when (direction) {
            ConversionDirection.UNICODE_TO_CHARACTER -> StringUtils.unicodeToCharacter(input)
            ConversionDirection.CHARACTER_TO_UNICODE -> StringUtils.characterToUnicode(input)
        }

        updateResult(result)
        context.copyToClipboard(result)
        this.showSnackbar("$result ${context.getString(R.string.copied)}")
    } catch (e: IllegalArgumentException) {
        e.message?.let { this.showSnackbar(it) }
    }
}
