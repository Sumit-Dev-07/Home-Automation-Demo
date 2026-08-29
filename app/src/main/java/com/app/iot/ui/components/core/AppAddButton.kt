package com.app.iot.ui.components.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.R
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

@Composable
fun AppAddButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    dashWidth: Dp = 10.dp,
    gapWidth: Dp = 6.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .drawBehind {
                val outline = shape.createOutline(size, layoutDirection, this)
                val path = Path().apply {
                    addOutline(outline)
                }
                drawPath(
                    path = path,
                    color = AppPalette.white.copy(alpha = 0.4f),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
                            phase = 0f
                        )
                    )
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            AppText.Medium(
                text = text,
                fontSize = 18.sp,
                color = AppPalette.white
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppAddButtonPreview() {
    AppPreview(color = AppPalette.secondary) {
        AppAddButton(text = "Add Schedule", onClick = {})
    }
}
