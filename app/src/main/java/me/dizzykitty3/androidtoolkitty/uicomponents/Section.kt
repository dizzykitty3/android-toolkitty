package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import me.dizzykitty3.androidtoolkitty.R

@Composable
fun Section(
    @StringRes title: Int? = null,
    verticalPadding: Dp = dimensionResource(R.dimen.padding_card_content_top_and_bottom),
    content: @Composable () -> Unit,
) = Section(title?.let { stringResource(it) }, verticalPadding, content)

@Composable
fun Section(
    title: String?,
    verticalPadding: Dp = dimensionResource(R.dimen.padding_card_content_top_and_bottom),
    content: @Composable () -> Unit,
) {
    val horizontalPadding = dimensionResource(R.dimen.padding_card_content)
    Column(Modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(horizontal = horizontalPadding, vertical = dimensionResource(R.dimen.padding_tip))
                    .semantics { heading() },
            )
        }
        Surface(
            shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
            color = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = verticalPadding)) {
                content()
            }
        }
    }
}
