package com.app.iot.ui.components.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

/**
 * A reusable outlined button component with AppPalette.secondary border and configurable text color.
 */
@Composable
fun AppOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = AppPalette.secondary,
    borderColor: Color = AppPalette.secondary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        )
    ) {
        AppText.SemiBold(
            text = text,
            color = contentColor,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun AppOutlineButtonPreview() {
    AppPreview {
        AppOutlineButton(
            text = "Outline Button",
            onClick = {}
        )
    }
}
