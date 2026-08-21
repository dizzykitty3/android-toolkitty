package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.home.Unicode
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
import me.dizzykitty3.androidtoolkitty.utils.DateUtil
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.toASCII

@AndroidEntryPoint
class CodesOfCharactersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            ToolkitScreen(
                title = R.string.codes_of_characters,
                dynamicColor = state.dynamicColor
            ) {
                BaseCard(R.string.unicode) { Unicode() }
                BaseCard(R.string.ascii) { ASCII() }
                BaseCard("Unix Timestamp") { UnixTimestamp() }
            }
        }
    }
}

@Composable
private fun ASCII() {
    val focus = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    var stringToASCII by remember { mutableStateOf("") }
    var toASCIIResult by remember { mutableStateOf("") }

    OutlinedTextField(
        value = stringToASCII,
        onValueChange = { stringToASCII = it },
        label = { Text(stringResource(R.string.character)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
                toASCIIResult = stringToASCII.toASCII()
            }),
        trailingIcon = {
            ClearInput(stringToASCII) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                stringToASCII = ""
            }
        },
    )

    if (toASCIIResult != "") {
        Text("${stringResource(R.string.result)} $toASCIIResult")
    }

    TextButton(onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        focus.clearFocus()
        toASCIIResult = stringToASCII.toASCII()
    }) { Text(stringResource(R.string.convert_to_ascii_values)) }
}

@Composable
private fun UnixTimestamp() {
    Text("current unix timestamp in seconds:")
    Text(DateUtil.unixTimestampInSeconds)
}
