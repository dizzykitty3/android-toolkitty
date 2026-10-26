package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import me.dizzykitty3.androidtoolkitty.R

@Composable
fun CustomIconPopup(
    deviceType: String,
    deviceAddress: String
) {
    val haptic = LocalHapticFeedback.current
    var showPopup by remember { mutableStateOf(false) }

    Row {
        Surface(shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_small))) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.info),
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showPopup = true
                    },
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        if (showPopup) {
            Popup(
                onDismissRequest = { showPopup = false },
                popupPositionProvider = object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize
                    ): IntOffset {
                        val positionAbove = anchorBounds.top - popupContentSize.height

                        return if (positionAbove >= 0) {
                            IntOffset(anchorBounds.left, positionAbove)
                        } else {
                            IntOffset(anchorBounds.left, anchorBounds.bottom)
                        }
                    }
                }
            ) {
                Surface(
                    shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_small)),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp),
                ) {
                    Text(
                        ("$deviceType, $deviceAddress"),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}