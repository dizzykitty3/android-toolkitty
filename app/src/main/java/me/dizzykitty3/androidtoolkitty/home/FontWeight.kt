package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.ui.home.FontWeightTestActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen

@Composable
fun FontWeight() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(
        title = R.string.font_weight_test,
        icon = Icons.Outlined.FontDownload,
        hasShowMore = true,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(FontWeightTestActivity::class.java)
        }) { }
}
