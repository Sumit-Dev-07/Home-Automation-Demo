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
 * A reusable Text component that uses the project's default font family.
 */
@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    fontFamily: FontFamily = AppFont.onestRegular,
    fontSize : TextUnit = TextUnit.Unspecified
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
fun TitleText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    AppText(
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
    AppText(
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
    AppText(
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
            AppText(text = "Onest Light", fontFamily = AppFont.onestLight)
            AppText(text = "Onest Regular", fontFamily = AppFont.onestRegular)
            AppText(text = "Onest Medium", fontFamily = AppFont.onestMedium)
            AppText(text = "Onest SemiBold", fontFamily = AppFont.onestSemiBold)
            AppText(text = "Onest Bold", fontFamily = AppFont.onestBold)
        }
    }
}
