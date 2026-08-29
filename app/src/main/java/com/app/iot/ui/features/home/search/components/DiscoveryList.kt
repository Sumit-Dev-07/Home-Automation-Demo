package com.app.iot.ui.features.home.search.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.features.home.viewmodel.ConnectionStatus
import com.app.iot.ui.features.home.viewmodel.DeviceDiscoveryStatus
import com.app.iot.ui.theme.AppPalette

@Composable
fun DiscoveryList(
    devices: List<DeviceDiscoveryStatus>,
    modifier: Modifier = Modifier,
    onRetry: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 16.dp),
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
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppPalette.secondary)
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
                AppText.SemiBold(
                    text = status.device.name,
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
                
                AppText.Normal(
                    text = statusText,
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

@Preview(showBackground = true)
@Composable
private fun DiscoveredDeviceItemPreview() {
    com.app.iot.ui.theme.HomeAutomationTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DiscoveredDeviceItem(
                status = DeviceDiscoveryStatus(
                    device = com.app.iot.ui.features.home.viewmodel.DiscoveredDevice(
                        name = "Living Room ESP32",
                        ip = "192.168.1.15"
                    ),
                    connectionStatus = ConnectionStatus.CONNECTED
                ),
                onRetry = {}
            )
            
            DiscoveredDeviceItem(
                status = DeviceDiscoveryStatus(
                    device = com.app.iot.ui.features.home.viewmodel.DiscoveredDevice(
                        name = "Kitchen Controller",
                        ip = "192.168.1.16"
                    ),
                    connectionStatus = ConnectionStatus.CONNECTING
                ),
                onRetry = {}
            )
            
            DiscoveredDeviceItem(
                status = DeviceDiscoveryStatus(
                    device = com.app.iot.ui.features.home.viewmodel.DiscoveredDevice(
                        name = "Bedroom Sensor",
                        ip = "192.168.1.17"
                    ),
                    connectionStatus = ConnectionStatus.FAILED
                ),
                onRetry = {}
            )
        }
    }
}

