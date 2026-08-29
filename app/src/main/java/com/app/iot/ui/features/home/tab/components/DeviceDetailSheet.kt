package com.app.iot.ui.features.home.tab.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.R
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailSheet(
    deviceName: String,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden }
        ),
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
    ) {
        DeviceDetailContent(deviceName = deviceName, onDismiss = onDismiss)
    }
}

@Composable
fun DeviceDetailContent(
    deviceName: String,
    onDismiss: () -> Unit
) {
    var isOn by remember { mutableStateOf(true) }
    var brightness by remember { mutableFloatStateOf(0.33f) }
    var colorTemp by remember { mutableFloatStateOf(0.2f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                AppText.Bold(
                    text = deviceName,
                    fontSize = 18.sp,
                    color = AppPalette.black
                )
                AppText.Normal(
                    text = "3 Devices",
                    fontSize = 14.sp,
                    color = AppPalette.gray
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = AppPalette.darkGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lamp Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow Effect
            if (isOn) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFE0B2).copy(alpha = 0.4f),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width / 2, size.height * 0.7f),
                                    radius = size.width * 0.6f
                                )
                            )
                        }
                )
            }
            
            Icon(
                painter = painterResource(id = R.drawable.ic_product),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Brightness and Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sunlight),
                    contentDescription = null,
                    tint = Color(0xFFD79E23),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AppText.Bold(
                    text = "${(brightness * 100).roundToInt()}%",
                    fontSize = 28.sp,
                    color = AppPalette.black
                )
                Spacer(modifier = Modifier.width(16.dp))
                BrightnessBarSlider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    modifier = Modifier.width(140.dp)
                )
            }

            HorizontalOnOffToggle(
                isOn = isOn,
                onStatusChange = { isOn = it },
                showIcon = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Color Temperature Slider
        ColorTempSlider(
            value = colorTemp,
            onValueChange = { colorTemp = it }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFD79E23), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                AppText.Medium(text = "Warm", fontSize = 14.sp, color = AppPalette.black)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppText.Medium(text = "Cool", fontSize = 14.sp, color = AppPalette.black)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF3FB5FF), CircleShape))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Info Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(
                icon = R.drawable.ic_clock,
                value = "04 Hr",
                label = "Run time",
                iconBgColor = Color(0xFF3FB5FF),
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = R.drawable.ic_ai,
                value = "72 AQI",
                label = "Moderate",
                iconBgColor = Color(0xFFD79E23),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BrightnessBarSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val barCount = 12
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val progress = i.toFloat() / (barCount - 1)
            val isActive = progress <= value
            val barHeight = 8.dp + (if (i % 2 == 0) 8.dp else 4.dp)
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeight)
                    .clip(CircleShape)
                    .background(
                        if (isActive) Color(0xFF3FB5FF) else AppPalette.gray.copy(alpha = 0.4f)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onValueChange(progress)
                        }
                    }
            )
        }
    }
}

@Composable
fun ColorTempSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFA726), // Warm
                        Color(0xFFE0E0E0), // Neutral
                        Color(0xFF29B6F6)  // Cool
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onValueChange((offset.x / size.width).coerceIn(0f, 1f))
                }
            }
    ) {
        BoxWithConstraints {
            val thumbSize = 20.dp
            val xPos = with(density) { (value * (maxWidth - thumbSize).toPx()) }
            
            Box(
                modifier = Modifier
                    .offset(x = with(density) { xPos.toDp() })
                    .size(thumbSize)
                    .align(Alignment.CenterStart)
                    .border(4.dp, Color.White, CircleShape)
                    .background(Color.Transparent)
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: Int,
    value: String,
    label: String,
    iconBgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppPalette.black.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconBgColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                AppText.Bold(text = value, fontSize = 16.sp, color = AppPalette.black)
                AppText.Normal(text = label, fontSize = 12.sp, color = AppPalette.gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceDetailContentPreview() {
    AppPreview(padding = 0.dp) {
        DeviceDetailContent(deviceName = "Lumana Supra H45", onDismiss = {})
    }
}
