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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.R
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import kotlin.math.roundToInt

@Composable
fun HorizontalOnOffToggle(
    isOn: Boolean,
    onStatusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showIcon: Boolean = false,
    enabled: Boolean = true
) {
    val containerWidth = if (showIcon) 100.dp else 100.dp
    val containerHeight = 44.dp
    val gap = 2.dp
    val outerCornerRadius = 8.dp
    val innerCornerRadius = 8.dp
    val innerPadding = 4.dp
    
    val itemWidth = (containerWidth - (gap * 2) - (innerPadding * 2)) / 2

    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val indicatorOffset by animateDpAsState(
        targetValue = if (isOn) itemWidth else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "indicatorOffset"
    )

    val indicatorColor by animateColorAsState(
        targetValue = if (isOn) AppPalette.primary else AppPalette.borderGray.copy(alpha = 0.8f),
        animationSpec = tween(durationMillis = 300),
        label = "indicatorColor"
    )
    
    val containerBorderColor by animateColorAsState(
        targetValue = if (isOn) AppPalette.primary else AppPalette.black.copy(alpha = 0.1f),
        animationSpec = tween(durationMillis = 300),
        label = "containerBorderColor"
    )

    Box(
        modifier = modifier
            .width(containerWidth)
            .height(containerHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onStatusChange(!isOn)
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer border box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(AppPalette.black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    containerBorderColor,
                    RoundedCornerShape(outerCornerRadius),
                )
        )

        // Inner toggle container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius))
                .background(AppPalette.white)
                .padding(innerPadding)
        ) {
            // Sliding Indicator
            Box(
                modifier = Modifier
                    .offset { IntOffset(indicatorOffset.toPx().roundToInt(), 0) }
                    .fillMaxHeight()
                    .width(itemWidth)
                    .clip(RoundedCornerShape(innerCornerRadius - 2.dp))
                    .background(indicatorColor)
            )

            // Labels
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (showIcon) {
                        if (!isOn) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_power_button),
                                contentDescription = null,
                                tint = AppPalette.white,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "ON",
                                fontFamily = AppFont.onestSemiBold,
                                fontSize = 14.sp,
                                color = AppPalette.black
                            )
                        }
                    } else {
                        val color by animateColorAsState(
                            targetValue = if (!isOn) AppPalette.white else AppPalette.black,
                            label = "offLabelColor"
                        )
                        Text(
                            text = "OFF",
                            fontFamily = AppFont.onestSemiBold,
                            fontSize = 14.sp,
                            color = color
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (showIcon) {
                        if (isOn) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_power_button),
                                contentDescription = null,
                                tint = AppPalette.white,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "OFF",
                                fontFamily = AppFont.onestSemiBold,
                                fontSize = 14.sp,
                                color = AppPalette.gray
                            )
                        }
                    } else {
                        val color by animateColorAsState(
                            targetValue = if (isOn) AppPalette.white else AppPalette.black,
                            label = "onLabelColorNormal"
                        )
                        Text(
                            text = "ON",
                            fontFamily = AppFont.onestSemiBold,
                            fontSize = 14.sp,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalOnOffTogglePreview() {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Normal Type
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            HorizontalOnOffToggle(isOn = false, onStatusChange = {})
            HorizontalOnOffToggle(isOn = true, onStatusChange = {})
        }
        
        // Icon Type
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            HorizontalOnOffToggle(isOn = false, onStatusChange = {}, showIcon = true)
            HorizontalOnOffToggle(isOn = true, onStatusChange = {}, showIcon = true)
        }
    }
}
