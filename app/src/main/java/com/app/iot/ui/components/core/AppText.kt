package com.app.iot.ui.components.core

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.app.iot.ui.theme.AppPreview
import com.app.iot.ui.theme.AppFont

/**
 * Reusable Text components using the project's font family.
 */
object AppText {

    @Composable
    private fun TextBase(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        overflow: TextOverflow = TextOverflow.Clip,
        fontFamily: FontFamily,
        fontSize: TextUnit = TextUnit.Unspecified
    ) {
        Text(
            text = text,
            modifier = modifier,
            style = style.copy(fontFamily = fontFamily),
            fontSize = fontSize,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow
        )
    }

    @Composable
    fun Light(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        overflow: TextOverflow = TextOverflow.Clip,
        fontSize: TextUnit = TextUnit.Unspecified
    ) = TextBase(text, modifier, style, color, textAlign, maxLines, overflow, AppFont.onestLight, fontSize)

    @Composable
    fun Normal(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        overflow: TextOverflow = TextOverflow.Clip,
        fontSize: TextUnit = TextUnit.Unspecified
    ) = TextBase(text, modifier, style, color, textAlign, maxLines, overflow, AppFont.onestRegular, fontSize)

    @Composable
    fun Medium(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        overflow: TextOverflow = TextOverflow.Clip,
        fontSize: TextUnit = TextUnit.Unspecified
    ) = TextBase(text, modifier, style, color, textAlign, maxLines, overflow, AppFont.onestMedium, fontSize)

    @Composable
    fun SemiBold(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        overflow: TextOverflow = TextOverflow.Clip,
        fontSize: TextUnit = TextUnit.Unspecified
    ) = TextBase(text, modifier, style, color, textAlign, maxLines, overflow, AppFont.onestSemiBold, fontSize)

    @Composable
    fun Bold(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        overflow: TextOverflow = TextOverflow.Clip,
        fontSize: TextUnit = TextUnit.Unspecified
    ) = TextBase(text, modifier, style, color, textAlign, maxLines, overflow, AppFont.onestBold, fontSize)
}

@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    AppText.Bold(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun MediumTitleText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    AppText.SemiBold(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = textAlign
    )
}

@Composable
fun ErrorTextInputField(
    modifier: Modifier = Modifier,
    text: String
) {
    AppText.Normal(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}

@Preview(showBackground = true)
@Composable
fun AppTextFontsPreview() {
    AppPreview {
        Column {
            AppText.Light(text = "Onest Light")
            AppText.Normal(text = "Onest Regular")
            AppText.Medium(text = "Onest Medium")
            AppText.SemiBold(text = "Onest SemiBold")
            AppText.Bold(text = "Onest Bold")
        }
    }
}
