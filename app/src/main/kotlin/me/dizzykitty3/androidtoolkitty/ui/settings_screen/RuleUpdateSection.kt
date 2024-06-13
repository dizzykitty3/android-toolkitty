package me.dizzykitty3.androidtoolkitty.ui.settings_screen

import android.view.HapticFeedbackConstants
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.data.sharedpreferences.SettingsSharedPref
import me.dizzykitty3.androidtoolkitty.ui_components.GroupTitle
import me.dizzykitty3.androidtoolkitty.ui_components.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.HttpUtil
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil

@Composable
fun RuleUpdateSection() {
    GroupTitle(R.string.rule_update)
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val view = LocalView.current
    val errorMessage = stringResource(id = R.string.error_rule_update)
    val success = stringResource(id = R.string.success)

    OutlinedButton(
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            coroutineScope.launch {
                isLoading = true
                onUpdateSocialMedia(
                    onSuccess = {
                        SettingsSharedPref.socialMedia = it
                        isLoading = false
                        SnackbarUtil.snackbar(view, success)
                    },
                    onFailure = {
                        isLoading = false
                        SnackbarUtil.snackbar(view, errorMessage)
                    }
                )
            }
        }
    ) {
        Text(stringResource(id = R.string.social_media_platforms_list))
    }

    OutlinedButton(
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            coroutineScope.launch {
                isLoading = true
                onUpdateDomainSuffix(
                    onSuccess = {
                        SettingsSharedPref.domainSuffix = it
                        isLoading = false
                        SnackbarUtil.snackbar(view, success)
                    },
                    onFailure = {
                        isLoading = false
                        SnackbarUtil.snackbar(view, errorMessage)
                    }
                )
            }
        }
    ) {
        Text(stringResource(R.string.webpage_suffixes))
    }

    if (isLoading || SettingsSharedPref.uiTesting) {
        SpacerPadding()
        CircularProgressIndicator()
    }
}

private suspend fun onUpdateSocialMedia(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
    val url = "https://api.yanqishui.work/toolkitten/open/social-media"
    handleRequest(url = url, onSuccess = onSuccess, onFailure = onFailure)
}

private suspend fun onUpdateDomainSuffix(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
    val url = "https://api.yanqishui.work/toolkitten/open/domain-suffix"
    handleRequest(url = url, onSuccess = onSuccess, onFailure = onFailure)
}

private suspend fun handleRequest(
    url: String,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    try {
        val response: HttpResponse = HttpUtil.get(url)
        if (response.status == HttpStatusCode.OK) {
            val responseBody = response.bodyAsText()
            onSuccess(responseBody)
        } else {
            onFailure()
        }
    } catch (_: Exception) {
        onFailure()
    }
}