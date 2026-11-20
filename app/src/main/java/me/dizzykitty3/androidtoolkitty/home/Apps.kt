package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.ui.home.AppsActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen

@Composable
fun Apps() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(title = R.string.apps_title, icon = Icons.Outlined.Apps) {
        FilledTonalButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(AppsActivity::class.java)
        }) { Text(stringResource(R.string.apps_installed_apps)) }
    }
}
