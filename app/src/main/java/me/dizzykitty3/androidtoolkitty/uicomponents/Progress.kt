package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.dizzykitty3.androidtoolkitty.utils.DateUtils

@Composable
fun YearProgressIndicator() {
    YearProgressContent(targetProgress = DateUtils.yearProgress)
}

@Composable
private fun YearProgressContent(targetProgress: Float) {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "year progress animation"
    )

    LaunchedEffect(targetProgress) {
        progress = targetProgress
    }

    Column(Modifier.fillMaxWidth()) {
        SpacerPadding()
        LinearProgressIndicator(
            progress = { animatedProgress },
            trackColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        )
        SpacerPadding()
    }
}

@Preview(showBackground = true)
@Composable
private fun YearProgressPreview30() {
    YearProgressContent(targetProgress = 0.3f)
}

@Preview(showBackground = true)
@Composable
private fun YearProgressPreview80() {
    YearProgressContent(targetProgress = 0.8f)
}