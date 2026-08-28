package com.app.iot.ui.features.home.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.features.home.viewmodel.ConnectionStatus
import com.app.iot.ui.features.home.viewmodel.DeviceDiscoveryStatus
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette

@Composable
fun DiscoveryList(
    devices: List<DeviceDiscoveryStatus>,
    modifier: Modifier = Modifier,
    onRetry: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(devices) { status ->
            DiscoveredDeviceItem(status = status, onRetry = { onRetry(status.device.ip) })
        }
    }
}

@Composable
fun DiscoveredDeviceItem(
    status: DeviceDiscoveryStatus,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AppPalette.white,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Device Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppPalette.lightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeveloperBoard,
                    contentDescription = null,
                    tint = AppPalette.gray
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.device.name,
                    fontFamily = AppFont.onestSemiBold,
                    fontSize = 15.sp,
                    color = AppPalette.black
                )
                
                val statusText = when (status.connectionStatus) {
                    ConnectionStatus.CONNECTING -> "Connecting..."
                    ConnectionStatus.CONNECTED -> "Connected"
                    ConnectionStatus.FAILED -> "Unable to connect"
                    else -> ""
                }
                
                val statusColor = when (status.connectionStatus) {
                    ConnectionStatus.CONNECTING -> AppPalette.primary
                    ConnectionStatus.CONNECTED -> AppPalette.green
                    ConnectionStatus.FAILED -> AppPalette.red
                    else -> AppPalette.gray
                }
                
                Text(
                    text = statusText,
                    fontFamily = AppFont.onestRegular,
                    fontSize = 12.sp,
                    color = statusColor
                )
            }
            
            if (status.connectionStatus == ConnectionStatus.FAILED) {
                IconButton(onClick = onRetry) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = AppPalette.gray
                    )
                }
            }
        }
    }
}
