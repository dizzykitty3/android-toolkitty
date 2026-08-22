package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.dizzykitty3.androidtoolkitty.R

@Composable
fun Gradient(
    textToDisplay: String, colors: List<Color>
) {
    Row {
        val text = buildAnnotatedString {
            withStyle(SpanStyle(Brush.horizontalGradient(colors))) {
                append(textToDisplay)
            }
        }
        Text(
            text, style = TextStyle.Default.copy(
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                lineHeight = 40.0.sp,
                letterSpacing = 0.0.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientPreview() {
    Gradient(
        "hello world", listOf(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun GradientSmall(textToDisplay: String, colors: List<Color>) {
    Row {
        val text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    Brush.horizontalGradient(colors), fontWeight = FontWeight.Black
                )
            ) { append(textToDisplay) }
        }
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientSmallPreview() {
    GradientSmall(
        "hello world",
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
    )
}

@Composable
fun AnnotatedString.Builder.ItalicText(@StringRes text: Int) {
    val italicTextStyle = SpanStyle(
        fontStyle = FontStyle.Italic, fontWeight = FontWeight.Light
    )
    withStyle(italicTextStyle) { append(stringResource(text)) }
}

@Composable
fun AnnotatedString.Builder.ItalicText(text: String) {
    val italicTextStyle = SpanStyle(
        fontStyle = FontStyle.Italic, fontWeight = FontWeight.Light
    )
    withStyle(italicTextStyle) { append(text) }
}

@Preview(showBackground = true)
@Composable
private fun ItalicTextPreview() {
    Text(buildAnnotatedString { ItalicText("hello world") })
}

@Composable
fun AnnotatedString.Builder.PrimaryColorText(@StringRes id: Int) {
    val materialPrimaryColorStyle = SpanStyle(color = MaterialTheme.colorScheme.primary)
    withStyle(materialPrimaryColorStyle) { append(stringResource(id)) }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryColorTextPreview() {
    Text(buildAnnotatedString { PrimaryColorText(android.R.string.ok) })
}

@Composable
fun Tip(@StringRes message: Int) = Tip(stringResource(message))

@Composable
fun Tip(msg: String) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row(
            Modifier
                .padding(dimensionResource(R.dimen.padding_tip))
                .fillMaxWidth()
        ) {
            Icon(
                Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(24.dp)
            )
            IconAndTextPadding()
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
    SpacerPadding()
}

@Preview(showBackground = true)
@Composable
private fun TipPreview() {
    Tip("hello world")
}

@Composable
fun ErrorTip(msg: String) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_shape)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Row(
            Modifier
                .padding(dimensionResource(R.dimen.padding_tip))
                .fillMaxWidth()
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            IconAndTextPadding()
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
    SpacerPadding()
}

@Composable
fun DevBuildTip() = Tip(R.string.debug_build_top_tip)

@Composable
fun GroupTitle(@StringRes title: Int) = GroupTitle(stringResource(title))

@Composable
fun GroupTitle(title: String) {
    Text(
        title, color = MaterialTheme.colorScheme.primary
    )
    SpacerPadding()
}

@Preview(showBackground = true)
@Composable
private fun GroupTitlePreview() {
    GroupTitle("hello world")
}

@Composable
fun GroupTitleNoColor(title: String) = Text(title, style = MaterialTheme.typography.titleMedium)

@Preview(showBackground = true)
@Composable
private fun GroupTitleNoColorPreview() {
    GroupTitleNoColor("hello world")
}

@Composable
private fun ScrollableTextContent(
    text: String,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
) = Box(Modifier.horizontalScroll(rememberScrollState())) {
    Text(text, fontWeight = fontWeight, fontStyle = fontStyle, maxLines = maxLines)
}

@Composable
fun ScrollableText(text: String) = ScrollableTextContent(text)

@Composable
fun ScrollableText(@StringRes text: Int) = ScrollableText(stringResource(text))

@Preview(showBackground = true)
@Composable
private fun ScrollableTextPreview() {
    ScrollableText("hello world")
}

@Composable
fun ScrollableBoldText(text: String) =
    ScrollableTextContent(text, fontWeight = FontWeight.Bold)

@Composable
fun ScrollableBoldText(@StringRes text: Int) = ScrollableBoldText(stringResource(text))

@Preview(showBackground = true)
@Composable
private fun ScrollableBoldTextPreview() {
    ScrollableBoldText("hello world")
}

@Composable
fun ScrollableItalicText(@StringRes text: Int) =
    ScrollableItalicText(stringResource(text))

@Composable
fun ScrollableItalicText(text: String) =
    ScrollableTextContent(
        text,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        maxLines = 1,
    )

@Preview(showBackground = true)
@Composable
private fun ScrollableItalicTextPreview() {
    ScrollableItalicText("hello world")
}

@Composable
fun Description(@StringRes text: Int) = Description(stringResource(text))

@Composable
fun Description(text: String) = Text(
    text,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3F)
)

@Preview(showBackground = true)
@Composable
private fun DescriptionPreview() {
    Description("hello world")
}

@Composable
fun LabelText(text: String) = Text(text, style = MaterialTheme.typography.labelMedium)

@Composable
fun LabelText(@StringRes text: Int) = LabelText(stringResource(text))

@Preview(showBackground = true)
@Composable
private fun LabelTextPreview() {
    LabelText("hello world")
}

@Composable
fun LabelAndValueTextRow(label: String, text: String) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(0.4F)) { LabelText(label) }
        Column(Modifier.weight(0.6F)) { ScrollableText(text) }
    }

@Composable
fun LabelAndValueTextRow(@StringRes label: Int, text: String) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(0.4F)) { LabelText(stringResource(label)) }
        Column(Modifier.weight(0.6F)) { ScrollableText(text) }
    }

@Preview(showBackground = true)
@Composable
private fun LabelAndValueTextRowPreview() {
    LabelAndValueTextRow(label = "hello world", text = "hello world")
}
