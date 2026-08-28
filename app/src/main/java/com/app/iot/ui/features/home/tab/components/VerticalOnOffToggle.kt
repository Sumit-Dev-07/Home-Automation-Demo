package com.app.iot.ui.features.home.tab.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import kotlin.math.roundToInt

@Composable
fun VerticalOnOffToggle(
    isOn: Boolean,
    onStatusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    //val containerWidth = 56.dp
    //val containerHeight = 110.dp
    val containerWidth = 40.dp
    val containerHeight = 72.dp
    val innerPadding = 4.dp
    val itemHeight = (containerHeight - (innerPadding * 2)) / 2
    val cornerRadius = 6.dp
    val innerCornerRadius = 6.dp

    //val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val indicatorOffset by animateDpAsState(
        targetValue = if (isOn) itemHeight else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "indicatorOffset"
    )

    val indicatorColor by animateColorAsState(
        targetValue = if (isOn) AppPalette.primary else AppPalette.borderGray.copy(alpha = 0.8f),
        animationSpec = tween(durationMillis = 300),
        label = "indicatorColor"
    )
    
    val containerBorderColor by animateColorAsState(
        targetValue = if (isOn) AppPalette.primary else AppPalette.borderGray,
        animationSpec = tween(durationMillis = 300),
        label = "containerBorderColor"
    )

    Box(
        modifier = modifier
            .width(containerWidth)
            .height(containerHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(AppPalette.white)
            .border(
                width = 1.dp,
                color = containerBorderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                //haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onStatusChange(!isOn)
            }
            .padding(innerPadding)
    ) {
        // Sliding Indicator
        Box(
            modifier = Modifier
                .offset { IntOffset(0, indicatorOffset.toPx().roundToInt()) }
                .fillMaxWidth()
                .height(itemHeight)
                .clip(RoundedCornerShape(innerCornerRadius))
                .background(indicatorColor)
        )

        // Labels
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val offTextColor by animateColorAsState(
                    targetValue = if (!isOn) AppPalette.black else AppPalette.black,
                    label = "offTextColor"
                )
                Text(
                    text = "OFF",
                    fontFamily = AppFont.onestSemiBold,
                    fontSize = 8.sp,
                    color = offTextColor
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val onTextColor by animateColorAsState(
                    targetValue = if (isOn) AppPalette.white else AppPalette.black,
                    label = "onTextColor"
                )
                Text(
                    text = "ON",
                    fontFamily = AppFont.onestSemiBold,
                    fontSize = 8.sp,
                    color = onTextColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerticalOnOffTogglePreview() {
    Row(
        modifier = Modifier.padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        VerticalOnOffToggle(isOn = false, onStatusChange = {})
        VerticalOnOffToggle(isOn = true, onStatusChange = {})
    }
}
