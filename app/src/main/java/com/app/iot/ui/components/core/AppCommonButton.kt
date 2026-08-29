package com.app.iot.ui.components.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

/**
 * A reusable common button component with AppPalette.secondary background and white text.
 */
@Composable
fun AppCommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = AppPalette.secondary,
    contentColor: Color = AppPalette.white
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
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
fun AppCommonButtonPreview() {
    AppPreview {
        AppCommonButton(
            text = "Click Me",
            onClick = {}
        )
    }
}
