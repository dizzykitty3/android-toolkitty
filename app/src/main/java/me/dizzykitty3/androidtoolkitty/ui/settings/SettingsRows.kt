package me.dizzykitty3.androidtoolkitty.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.dizzykitty3.androidtoolkitty.uicomponents.SettingsRow
import me.dizzykitty3.androidtoolkitty.uicomponents.SectionTitle

@Composable
internal fun SettingsSection(
    @StringRes title: Int? = null,
    selectable: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        if (title != null) {
            SectionTitle(stringResource(title))
        }
        // No group background: the gaps show the page beneath the individual rows.
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                .then(if (selectable) Modifier.selectableGroup() else Modifier),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) { content() }
    }
}

@Composable
internal fun SettingsLinkRow(
    icon: ImageVector? = null,
    title: String,
    onClick: () -> Unit,
    trailingIcon: ImageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
    trailingContentDescription: String? = null,
) {
    SettingsRow(interaction = Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = MaterialTheme.colorScheme.onSurfaceVariant),
        onClick = onClick,
    )) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(20.dp))
        }
        Text(title, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(16.dp))
        Icon(trailingIcon, trailingContentDescription, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun SettingsLinkRow(
    icon: ImageVector? = null,
    @StringRes title: Int,
    onClick: () -> Unit,
    trailingIcon: ImageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
    trailingContentDescription: String? = null,
) = SettingsLinkRow(icon, stringResource(title), onClick, trailingIcon, trailingContentDescription)

@Composable
internal fun SettingsInfoRow(
    icon: ImageVector? = null,
    @StringRes title: Int,
    text: String,
) {
    SettingsRow {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(stringResource(title))
            Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}
