package me.dizzykitty3.androidtoolkitty.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.CardSpacePadding
import me.dizzykitty3.androidtoolkitty.uicomponents.GroupTitleNoColor
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.ToolkitScreen

@AndroidEntryPoint
class ComposeCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.settingsState.collectAsStateWithLifecycle()

            ToolkitScreen(
                title = R.string.compose,
                dynamicColor = state.dynamicColor
            ) {
                ComposeCatalogComposable()
            }
        }
    }
}

@Composable
private fun ComposeCatalogComposable() {
    Column(Modifier.fillMaxWidth()) {
        GroupTitleNoColor("Typography")
        SpacerPadding()
        Text("Display Large", style = MaterialTheme.typography.displayLarge)
        HorizontalDivider()
        Text("Display Medium", style = MaterialTheme.typography.displayMedium)
        HorizontalDivider()
        Text("Display Small", style = MaterialTheme.typography.displaySmall)
        HorizontalDivider()
        Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
        HorizontalDivider()
        Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
        HorizontalDivider()
        Text("Headline Small", style = MaterialTheme.typography.headlineSmall)
        HorizontalDivider()
        Text("Title Large", style = MaterialTheme.typography.titleLarge)
        HorizontalDivider()
        Text("Title Medium", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider()
        Text("Title Small", style = MaterialTheme.typography.titleSmall)
        HorizontalDivider()
        Text("Body Large", style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider()
        Text("Body Medium", style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider()
        Text("Body Small", style = MaterialTheme.typography.bodySmall)
        HorizontalDivider()
        Text("Label Large", style = MaterialTheme.typography.labelLarge)
        HorizontalDivider()
        Text("Label Medium", style = MaterialTheme.typography.labelMedium)
        HorizontalDivider()
        Text("Label Small", style = MaterialTheme.typography.labelSmall)

        CardSpacePadding()
        GroupTitleNoColor("Color Scheme")
        Color(MaterialTheme.colorScheme.primary, "primary")
        Color(MaterialTheme.colorScheme.primaryContainer, "primaryContainer")
        Color(MaterialTheme.colorScheme.inversePrimary, "inversePrimary")
        Color(MaterialTheme.colorScheme.secondary, "secondary")
        Color(MaterialTheme.colorScheme.secondaryContainer, "secondaryContainer")
        Color(MaterialTheme.colorScheme.tertiary, "tertiary")
        Color(MaterialTheme.colorScheme.tertiaryContainer, "tertiaryContainer")
        Color(MaterialTheme.colorScheme.error, "error")
        Color(MaterialTheme.colorScheme.errorContainer, "errorContainer")
        Color(MaterialTheme.colorScheme.background, "background")
        Color(MaterialTheme.colorScheme.surface, "surface")
        Color(MaterialTheme.colorScheme.surfaceVariant, "surfaceVariant")
        Color(MaterialTheme.colorScheme.surfaceTint, "surfaceTint")
        Color(MaterialTheme.colorScheme.inverseSurface, "inverseSurface")
        Color(MaterialTheme.colorScheme.outline, "outline")
        Color(MaterialTheme.colorScheme.outlineVariant, "outlineVariant")
        Color(MaterialTheme.colorScheme.scrim, "scrim")
        Color(MaterialTheme.colorScheme.surfaceBright, "surfaceBright")
        Color(MaterialTheme.colorScheme.surfaceDim, "surfaceDim")
        Color(MaterialTheme.colorScheme.surfaceContainer, "surfaceContainer")
        Color(MaterialTheme.colorScheme.surfaceContainerHigh, "surfaceContainerHigh")
        Color(MaterialTheme.colorScheme.surfaceContainerHighest, "surfaceContainerHighest")
        Color(MaterialTheme.colorScheme.surfaceContainerLow, "surfaceContainerLow")
        Color(MaterialTheme.colorScheme.surfaceContainerLowest, "surfaceContainerLowest")

        CardSpacePadding()
    }
}

@Composable
private fun Color(color: Color, text: String) = Row(
    Modifier
        .fillMaxWidth()
        .height(45.dp)
) {
    Surface(Modifier.fillMaxSize(), color = color) { Text(text, textAlign = TextAlign.Center) }
}
