package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import me.dizzykitty3.androidtoolkitty.ui.home.DateTimeActivity
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.YearProgressIndicator
import me.dizzykitty3.androidtoolkitty.utils.DateUtils
import me.dizzykitty3.androidtoolkitty.utils.DateUtils.toProgress

@Composable
fun YearProgress() {
    val context = LocalContext.current
    BaseCard(title = R.string.date_time_tools, icon = Icons.Outlined.HourglassTop,
        hasShowMore = true, onClick = { context.openScreen(DateTimeActivity::class.java) }) {
        YearProgressIndicator()
        Text(DateUtils.yearProgress.toProgress(stringResource(R.string.happy_new_year)))
    }
}
