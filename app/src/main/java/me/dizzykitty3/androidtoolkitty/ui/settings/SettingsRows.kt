package me.dizzykitty3.androidtoolkitty.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.uicomponents.IconAndTextPadding
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding

@Composable
internal fun SettingsLinkRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    trailingIcon: ImageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
    trailingContentDescription: String? = null,
) {
    Surface(
        shape = RoundedCornerShape(
            dimensionResource(R.dimen.rounded_corner_shape)
        ),
        color = MaterialTheme.colorScheme.surfaceBright
    ) {
        Column(
            Modifier
                .requiredHeightIn(min = dimensionResource(R.dimen.height_setting_row))
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1F), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = null)
                    IconAndTextPadding()
                    Text(title)
                }
                SpacerPadding()
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = trailingContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3F)
                )
                SpacerPadding()
            }
        }
    }
}
