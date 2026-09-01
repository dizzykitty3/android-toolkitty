package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.HTTPS
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.uicomponents.ItalicText
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openURL
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.removeTrailingPeriod
import me.dizzykitty3.androidtoolkitty.utils.URLUtils.addSuffix
import me.dizzykitty3.androidtoolkitty.utils.URLUtils.getSuffix
import timber.log.Timber

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalSettingsViewModel provides viewModel) {
                ToolkitScreen(
                    title = R.string.search,
                    dynamicColor = state.dynamicColor
                ) {
                    BaseCard(R.string.webpage) { Webpage() }
                    BaseCard(R.string.social_finder) { SocialMediaProfile() }
                    BaseCard(R.string.check_app_on_market) { CheckAppOnMarket() }
                }
            }
        }
    }
}

@Composable
private fun Webpage() {
    val vm = LocalSettingsViewModel.current
    val state by vm.settingsState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val focus = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    var url by remember { mutableStateOf("") }
    val fullWidthPeriod = "。"
    val halfWidthPeriod = "."
    val fullWidthSpace = "　"
    val halfWidthSpace = " "

    LaunchedEffect(state.typingContents) {
        if (url != state.typingContents) {
            url = state.typingContents
        }
    }

    OutlinedTextField(
        value = url,
        onValueChange = {
            url = it
            vm.updateTypingContents(
                it.replace(fullWidthPeriod, halfWidthPeriod)
                    .replace(halfWidthSpace, halfWidthPeriod)
                    .replace(fullWidthSpace, halfWidthPeriod)
            )
        },
        label = { Text(stringResource(R.string.url)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done, keyboardType = KeyboardType.Ascii
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
                view.context.onTapVisitURLButton(url)
            }),
        trailingIcon = {
            ClearInput(url) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                url = ""
                vm.updateTypingContents("")
            }
        },
        supportingText = {
            Text(buildAnnotatedString {
                append(stringResource(R.string.url_input_hint_1))
                ItalicText(" www. ")
                append(stringResource(R.string.url_input_hint_2))
                ItalicText(" .com ")
                append(stringResource(R.string.url_input_hint_3))
                ItalicText(" .net ")
                append(stringResource(R.string.url_input_hint_4))
            })
        },
        prefix = {
            if (!url.contains(HTTPS)) {
                Text(HTTPS)
            }
        },
        suffix = {
            Text(
                if (url.isEmpty()) ""
                else if (url.last() == '.') url.removeTrailingPeriod().getSuffix().removePrefix(".")
                else url.removeTrailingPeriod().getSuffix()
            )
        })

    TextButton(onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        focus.clearFocus()
        view.context.onTapVisitURLButton(url)
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

private fun Context.onTapVisitURLButton(url: String) {
    if (url.isBlank()) return
    Timber.d("onTapVisitURLButton")
    this.openURL(url.removeTrailingPeriod().addSuffix())
}
