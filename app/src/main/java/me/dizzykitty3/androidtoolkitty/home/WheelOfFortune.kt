package me.dizzykitty3.androidtoolkitty.home

import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.json.Json
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.LocalSettingsViewModel
import me.dizzykitty3.androidtoolkitty.datastore.WheelOfFortuneItems
import me.dizzykitty3.androidtoolkitty.ui.home.WheelOfFortuneActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.IntentUtil.openScreen
import me.dizzykitty3.androidtoolkitty.utils.SnackbarUtil.showSnackbar
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WheelOfFortune() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    BaseCard(
        title = R.string.wheel_of_fortune,
        icon = Icons.Outlined.Casino,
        hasShowMore = true,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            context.openScreen(WheelOfFortuneActivity::class.java)
        }) {
        TheWheel()
    }
}

@Composable
fun TheWheel(withEditableList: Boolean = false) {
    val viewModel = LocalSettingsViewModel.current
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
    val item = stringResource(R.string.item)

    val items = remember(settingsState.wheelOfFortuneItems) {
        val itemsJson = settingsState.wheelOfFortuneItems
        if (itemsJson.isNullOrEmpty()) {
            List(4) { index -> "$item ${index + 1}" }
        } else {
            try {
                val data: WheelOfFortuneItems = Json.decodeFromString(itemsJson)
                data.items
            } catch (e: Exception) {
                Timber.e(e)
                List(4) { index -> "$item ${index + 1}" }
            }
        }
    }

    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val paint = remember {
        Paint().apply {
            color = textColor
            textAlign = Paint.Align.CENTER
            textSize = 40f
        }
    }
    var hasRotated by remember { mutableStateOf(false) }
    var isSpinning by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val colors = List(2) { index ->
        when (index) {
            0 -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
        }
    }
    var targetRotationDegrees by remember { mutableFloatStateOf(0f) }
    val currentRotationDegrees by animateFloatAsState(
        targetValue = targetRotationDegrees,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing), label = "",
    )
    val view = LocalView.current
    var selected by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentRotationDegrees) {
        if (currentRotationDegrees == targetRotationDegrees && hasRotated) {
            isSpinning = false
            val normalizedRotationDegrees = targetRotationDegrees % 360
            val itemsCount = items.size
            val anglePerItem = 360f / itemsCount
            val selectedIndex =
                (((360 - normalizedRotationDegrees + 270) % 360) / anglePerItem).toInt() % itemsCount
            val result = items[selectedIndex]
            selected = result

            view.showSnackbar(result)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val primary = MaterialTheme.colorScheme.primary

        if (withEditableList) {
            ExpandableList(
                items = items,
                onItemsChange = { updatedItems ->
                    val itemsJson = Json.encodeToString(WheelOfFortuneItems(updatedItems))
                    viewModel.updateWheelOfFortuneItems(itemsJson)
                },
                expanded = expanded,
                setExpanded = { value -> expanded = value },
                isSpinning = isSpinning
            )
            SpacerPadding()
            SpacerPadding()
            SpacerPadding()
        }

        Canvas(
            modifier = Modifier
                .size(250.dp)
                .aspectRatio(1F)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            items.indices.forEach { index ->
                val startAngle = (360f / items.size * index + currentRotationDegrees) % 360
                val sweepAngle = 360f / items.size
                val color = colors[index % colors.size]
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(radius * 2, radius * 2),
                    topLeft = Offset(center.x - radius, center.y - radius)
                )
                val endAngleRad = Math.toRadians((startAngle + sweepAngle).toDouble()).toFloat()
                val lineEnd = Offset(
                    center.x + radius * cos(endAngleRad), center.y + radius * sin(endAngleRad)
                )
                drawLine(
                    color = Color.Black, start = center, end = lineEnd, strokeWidth = 2f
                )
            }
            items.forEachIndexed { index, item ->
                val textAngleRad =
                    Math.toRadians((360f / items.size * index + currentRotationDegrees + 360f / items.size / 2) % 360.toDouble())
                        .toFloat()
                val textRadius = radius * 0.7F
                drawContext.canvas.nativeCanvas.drawText(
                    item,
                    center.x + textRadius * cos(textAngleRad),
                    center.y + textRadius * sin(textAngleRad),
                    paint
                )
            }
            val arrowPath = Path().apply {
                moveTo(center.x, center.y - radius - 15)
                lineTo(center.x - 10, center.y - radius - 30)
                lineTo(center.x + 10, center.y - radius - 30)
                close()
            }
            drawPath(arrowPath, primary)
            drawCircle(
                color = Color.Black, radius = radius, center = center, style = Stroke(width = 4f)
            )
        }

        SpacerPadding()

        val haptic = LocalHapticFeedback.current

        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                if (items.isNotEmpty() && !isSpinning) {
                    if (expanded) {
                        expanded = false
                    }
                    isSpinning = true
                    hasRotated = true
                    val randomBaseCircles = 3
                    val fineTunedAngle = Random.nextInt(360)
                    targetRotationDegrees += (360 * randomBaseCircles) + fineTunedAngle
                }
            }) {
            Text(text = stringResource(R.string.start))
        }

        SpacerPadding()

        Column(Modifier.horizontalScroll(rememberScrollState())) {
            Text(
                "${stringResource(R.string.result)} ${selected ?: stringResource(R.string.unknown)}"
            )
        }
    }
}

@Composable
private fun ExpandableList(
    items: List<String>,
    onItemsChange: (List<String>) -> Unit,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    isSpinning: Boolean
) {
    var editingIndex by remember { mutableIntStateOf(-1) }
    val editingText =
        remember { mutableStateListOf<String>().also { list -> items.forEach { list.add(it) } } }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(items) {
        editingText.clear()
        editingText.addAll(items)
    }

    LaunchedEffect(editingIndex, items.size) {
        if (editingIndex >= 0) {
            listState.animateScrollToItem(index = editingIndex)
            keyboardController?.show()
        }
    }

    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isSpinning) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    setExpanded(!expanded)
                }
                .padding(8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)) {
            Text(
                text = items.joinToString(", "),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1F)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = "Expand",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            val item = stringResource(R.string.item)
            Column {
                LazyColumn(state = listState, modifier = Modifier.heightIn(max = 200.dp)) {
                    itemsIndexed(items) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(8.dp)
                        ) {
                            if (editingIndex == index) {
                                TextField(
                                    value = editingText[index],
                                    onValueChange = { editingText[index] = it },
                                    modifier = Modifier.weight(1F),
                                    singleLine = true
                                )
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    modifier = Modifier.clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val updatedList = items.toMutableList()
                                            .also { it[index] = editingText[index] }
                                        onItemsChange(updatedList)
                                        editingIndex = -1
                                    })
                            } else {
                                Text(
                                    text = item,
                                    modifier = Modifier
                                        .weight(1F)
                                        .clickable { editingIndex = index })
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove",
                                    modifier = Modifier.clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val newList =
                                            items.toMutableList().apply { removeAt(index) }
                                        onItemsChange(newList)
                                        if (index == editingIndex) {
                                            editingIndex = -1
                                        } else if (index < editingIndex) {
                                            editingIndex -= 1
                                        }
                                    })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val newItem = "$item ${items.size + 1}"
                            val updatedItems = items + newItem
                            onItemsChange(updatedItems)
                            editingText.add(newItem)
                            editingIndex = items.size
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
