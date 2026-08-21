package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.HTTPS
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.ButtonDivider
import me.dizzykitty3.androidtoolkitty.uicomponents.ClearInput
import me.dizzykitty3.androidtoolkitty.uicomponents.CustomDropdownMenu
import me.dizzykitty3.androidtoolkitty.uicomponents.ErrorTip
import me.dizzykitty3.androidtoolkitty.uicomponents.ItalicText
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.Tip
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.checkOnMarket
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openURL
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.dropSpaces
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.isInvalidUsername
import me.dizzykitty3.androidtoolkitty.utils.StringUtil.removeTrailingPeriod
import me.dizzykitty3.androidtoolkitty.utils.URLUtil
import me.dizzykitty3.androidtoolkitty.utils.URLUtil.addSuffix
import me.dizzykitty3.androidtoolkitty.utils.URLUtil.getSuffix
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

@Composable
private fun SocialMediaProfile() {
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
    } else {
        CustomDropdownMenu(
            items = URLUtil.Platform.entries.map { stringResource(it.platform) },
            onItemSelected = {
                lastSelectedPlatformIndex = it
            },
            label = { Text(stringResource(R.string.platform)) },
            selectedPlatformIndex = lastSelectedPlatformIndex // TODO why won't this refresh
        )
    }

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
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
                if (isValid(selectedPlatform, username)) {
                    view.context.onTapVisitProfileButton(
                        username, lastSelectedPlatformIndex
                    )
                    vm.updateLastSelectedPlatformIndex(lastSelectedPlatformIndex)
                } else {
                    view.showSnackbar(R.string.invalid_username_tip)
                }
            }),
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
        })

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
    } else if (isInvalidNotNumbersOnly(
            selectedPlatform, username
        )
    ) {
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
        if (isValid(selectedPlatform, username)) {
            view.context.onTapVisitProfileButton(
                username, lastSelectedPlatformIndex
            )
            vm.updateLastSelectedPlatformIndex(lastSelectedPlatformIndex)
        } else {
            view.showSnackbar(R.string.invalid_username_tip)
        }
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

private fun Context.onTapVisitProfileButton(username: String, platformIndex: Int) {
    if (username.isBlank()) return
    Timber.d("onTapVisitProfileButton")
    val platform = URLUtil.Platform.entries.getOrNull(platformIndex) ?: return
    val url = toProfileFullURL(platform, username)
    this.openURL(url)
}

/**
 * @see me.dizzykitty3.androidtoolkitty.utils.URLUtil.Platform
 */
private fun toProfileFullURL(platform: URLUtil.Platform, username: String): String =
    when (platform) {
        URLUtil.Platform.BLUESKY -> if (username.contains(".")) "${platform.prefix}$username" // user custom
        else if (username.isNotBlank()) "${platform.prefix}${username.dropSpaces()}.bsky.social" // default
        else platform.prefix // for app UI display

        URLUtil.Platform.FANBOX, URLUtil.Platform.BOOTH, URLUtil.Platform.TUMBLR, URLUtil.Platform.CARRD -> "${username.dropSpaces()}${platform.prefix}"

        URLUtil.Platform.BILIBILI_AV -> if (username.lowercase()
                .startsWith("av")
        ) "${platform.prefix}${username.dropSpaces()}"
        else "${platform.prefix}av${username.dropSpaces()}"

        URLUtil.Platform.BILIBILI_BV -> if (username.lowercase()
                .startsWith("bv")
        ) "${platform.prefix}${username.dropSpaces()}"
        else "${platform.prefix}BV${username.dropSpaces()}"

        URLUtil.Platform.YOUTUBE_SEARCH, URLUtil.Platform.STEAM_SEARCH_STORE -> "${platform.prefix}${username.trim()}"

        else -> "${platform.prefix}${username.dropSpaces()}"
    }

private fun numbersOnlyPlatforms(platform: URLUtil.Platform): Boolean =
    platform == URLUtil.Platform.BILIBILI_UUID || platform == URLUtil.Platform.BILIBILI_AV || platform == URLUtil.Platform.PIXIV_ARTWORK || platform == URLUtil.Platform.PIXIV_UUID || platform == URLUtil.Platform.STEAM_UUID || platform == URLUtil.Platform.WEIBO_UUID || platform == URLUtil.Platform.GOOGLE_ISSUE_TRACKER

private fun isInvalidNotNumbersOnly(platform: URLUtil.Platform, username: String): Boolean =
    numbersOnlyPlatforms(platform) && username.isNotBlank() && !username.dropSpaces().isDigitsOnly()

private fun commonRulePlatforms(platform: URLUtil.Platform): Boolean =
    platform == URLUtil.Platform.X

private fun isInvalidCommonRule(platform: URLUtil.Platform, username: String): Boolean =
    commonRulePlatforms(platform) && username.isNotBlank() && username.dropSpaces()
        .isInvalidUsername()

private fun isValid(platform: URLUtil.Platform, username: String): Boolean =
    !isInvalidCommonRule(platform, username) && !isInvalidNotNumbersOnly(platform, username)

private fun isCaseSensitive(platform: URLUtil.Platform): Boolean =
    platform == URLUtil.Platform.LIT_LINK

@Composable
private fun CheckAppOnMarket() {
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
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
                view.context.checkOnMarket(packageName)
            }),
        trailingIcon = {
            ClearInput(packageName) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                packageName = ""
                vm.updateTypingContents("")
            }
        })
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
