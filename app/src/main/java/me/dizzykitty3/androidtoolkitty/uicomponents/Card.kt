package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtils.showSnackbar

@Composable
fun BaseCard(
    @StringRes title: Int,
    icon: ImageVector? = null,
    hasShowMore: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    BaseCard(
        title = stringResource(title),
        icon = icon,
        hasShowMore = hasShowMore,
        onClick = onClick,
        content = content
    )
}

@Composable
fun BaseCard(
    title: String,
    icon: ImageVector? = null,
    hasShowMore: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(R.dimen.padding_card_content_top_and_bottom),
                    bottom = dimensionResource(R.dimen.padding_card_content_top_and_bottom),
                    start = dimensionResource(R.dimen.padding_card_content),
                    end = dimensionResource(R.dimen.padding_card_content)
                )
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1F)) {
                    Column {
                        if (icon == null) {
                            GroupTitleNoColor(title)
                        } else {
                            SpacerPadding()
                            Row {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                IconAndTextPadding()
                                CardTitle(title)
                            }
                            SpacerPadding()
                        }
                    }
                }
                if (hasShowMore) {
                    if (onClick != null) {
                        FilledTonalButton(onClick = onClick) {
                            Icon(
                                imageVector = Icons.Outlined.MoreHoriz,
                                contentDescription = null
                            )
                        }
                    } else { // onClick == null. (for development)
                        FilledTonalButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            view.showSnackbar("haven't set up")
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreHoriz,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            SpacerPadding()
            content()
        }
    }
    CardSpacePadding()
}

@Composable
private fun CardTitle(title: String) =
    Text(text = title, style = MaterialTheme.typography.titleLarge)