package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.ui.home.HapticTestActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen

@Composable
fun HapticFeedback() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(
        title = R.string.haptic_test,
        icon = Icons.Outlined.Vibration,
        hasShowMore = true,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(HapticTestActivity::class.java)
        }) { }
}
