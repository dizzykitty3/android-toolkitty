package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import me.dizzykitty3.androidtoolkitty.theme.AppTheme

@Composable
fun ToolkitScreen(
    @StringRes title: Int,
    dynamicColor: Boolean,
    content: @Composable () -> Unit,
) {
    AppTheme(dynamicColor = dynamicColor) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    )
            ) {
                Screen(screenTitle = title, content = content)
            }
        }
    }
}
