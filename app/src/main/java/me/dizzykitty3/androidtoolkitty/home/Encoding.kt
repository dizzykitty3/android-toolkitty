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
    var isUnicodeInput by remember { mutableStateOf(false) }
    var isCharacterInput by remember { mutableStateOf(false) }
    val view = LocalView.current
    val focus = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    OutlinedTextField(
        value = unicode,
        onValueChange = {
            unicode = it
            characters = ""
            isUnicodeInput = true
            isCharacterInput = false
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
                if (isUnicodeInput) {
                    focus.clearFocus()
                    view.onClickConvertButton(unicode, { characters = it }, true)
                }
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
            isCharacterInput = true
            isUnicodeInput = false
        },
        label = { Text(stringResource(R.string.character)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (isCharacterInput) {
                    focus.clearFocus()
                    view.onClickConvertButton(characters, { unicode = it }, false)
                }
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
        if (isUnicodeInput) {
            view.onClickConvertButton(unicode, { characters = it }, true)
        } else if (isCharacterInput) {
            view.onClickConvertButton(characters, { unicode = it }, false)
        }
    }) { Text(stringResource(R.string.convert)) }
}

private fun View.onClickConvertButton(
    input: String, updateResult: (String) -> Unit, isUnicodeToChar: Boolean
) {
    if (input.isBlank()) return

    Timber.d("onClickConvertButton")

    try {
        val result = if (isUnicodeToChar) StringUtils.unicodeToCharacter(input)
        else StringUtils.characterToUnicode(input)

        updateResult(result)
        context.copyToClipboard(result)
        this.showSnackbar("$result ${context.getString(R.string.copied)}")
    } catch (e: Exception) {
        e.message?.let { this.showSnackbar(it) }
    }
}
