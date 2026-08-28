package com.app.iot.ui.features.home.search.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.app.iot.ui.features.home.viewmodel.ConnectionStatus
import com.app.iot.ui.features.home.viewmodel.DeviceDiscoveryStatus
import com.app.iot.ui.theme.AppPalette
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarGraphic(
    modifier: Modifier = Modifier,
    devices: List<DeviceDiscoveryStatus> = emptyList(),
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "rotation"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(280.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Radar Circles and Sweeper
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            
            val ringColor = AppPalette.borderGray.copy(alpha = 0.6f)

            // Draw concentric circles
            drawCircle(
                color = ringColor,
                radius = radius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = ringColor,
                radius = radius * 0.66f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = ringColor,
                radius = radius * 0.33f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            
            // Draw crosshairs
            drawLine(
                color = ringColor,
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = ringColor,
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 1.dp.toPx()
            )

            if (isScanning) {
                // Draw sweeper
                val angleRad = Math.toRadians(rotation.toDouble() - 90)
                
                drawArc(
                    brush = Brush.sweepGradient(
                        0f to AppPalette.primary.copy(alpha = 0f),
                        0.5f to AppPalette.primary.copy(alpha = pulseAlpha),
                        1f to AppPalette.primary.copy(alpha = 0f),
                        center = center
                    ),
                    startAngle = rotation - 90f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = size
                )
            }
        }

        // Place discovered devices
        devices.forEachIndexed { index, status ->
            // Distribute devices around the radar
            val angle = (index * 72f) + 45f // Offset start angle
            val distance = 0.4f + (index % 3) * 0.2f // Vary distance from center
            
            DeviceOnRadar(
                status = status,
                angle = angle,
                distanceScale = distance
            )
        }
    }
}

@Composable
fun DeviceOnRadar(
    status: DeviceDiscoveryStatus,
    angle: Float,
    distanceScale: Float
) {
    val radius = 120.dp // Base radius for placement
    val angleRad = Math.toRadians(angle.toDouble())
    
    val offsetX = (radius * distanceScale * cos(angleRad).toFloat())
    val offsetY = (radius * distanceScale * sin(angleRad).toFloat())

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(50.dp),
        contentAlignment = Alignment.Center
    ) {
        // Device Icon Container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppPalette.white)
                .border(
                    width = 1.dp,
                    color = if (status.connectionStatus == ConnectionStatus.FAILED) AppPalette.red 
                            else AppPalette.lightGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DeveloperBoard,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (status.connectionStatus == ConnectionStatus.CONNECTED) AppPalette.primary 
                       else AppPalette.gray
            )
        }
        
        // Status Badge
        if (status.connectionStatus == ConnectionStatus.FAILED) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = AppPalette.red,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .background(AppPalette.white, CircleShape)
            )
        }
    }
}
