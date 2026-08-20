package me.dizzykitty3.androidtoolkitty.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.YearProgressIndicator
import me.dizzykitty3.androidtoolkitty.utils.DateUtil
import me.dizzykitty3.androidtoolkitty.utils.DateUtil.toProgress

@Composable
fun YearProgress() {
    BaseCard(title = R.string.year_progress, icon = Icons.Outlined.HourglassTop) {
        YearProgressIndicator()
        Text(DateUtil.yearProgress.toProgress(stringResource(R.string.happy_new_year)))
    }
}
