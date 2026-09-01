package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.ui.home.ComposeCatalogActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen

@Composable
fun ComposeCatalog() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(
        title = R.string.compose,
        icon = Icons.Outlined.DashboardCustomize,
        hasShowMore = true,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(ComposeCatalogActivity::class.java)
        }) { }
}
