package com.app.iot.ui.features.home.tab.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.theme.AppPreview
import com.app.iot.ui.theme.HomeAutomationTheme
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette

@Composable
fun WifiStatusHeader(
	isConnected: Boolean,
	ssid: String,
	ipAddress: String,
	isRefreshing: Boolean,
	onRefresh: () -> Unit,
	onSettingsClick: () -> Unit
) {
	val innerCornerRadius = 20.dp
	
	Box(
		modifier = Modifier.fillMaxWidth(),
		contentAlignment = Alignment.Center
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(innerCornerRadius)),
			color = AppPalette.white
		) {
			Row(
				modifier = Modifier.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				if (isRefreshing) {
					CircularProgressIndicator(
						modifier = Modifier.size(24.dp),
						strokeWidth = 2.dp,
						color = AppPalette.green
					)
				} else {
					Icon(
						imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
						contentDescription = null,
						tint = if (isConnected) AppPalette.green else AppPalette.red,
						modifier = Modifier.size(24.dp)
					)
				}
				Spacer(modifier = Modifier.width(12.dp))
				Column {
					Text(
						text = if (isConnected) ssid.ifEmpty { "System Online" } else "System Offline",
						fontFamily = AppFont.onestMedium,
						fontSize = 14.sp,
						color = if (isConnected) AppPalette.darkGreen else AppPalette.darkRed
					)
					if (isConnected) {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Text(
								text = "IP: $ipAddress",
								fontFamily = AppFont.onestRegular,
								fontSize = 11.sp,
								color = AppPalette.gray
							)
						}
					}
				}
				Spacer(modifier = Modifier.weight(1f))
				if (isConnected) {
					IconButton(onClick = onSettingsClick) {
						Icon(
							Icons.Default.Settings,
							contentDescription = "WiFi Settings",
							tint = AppPalette.gray,
							modifier = Modifier.size(20.dp)
						)
					}
				}
				if (!isConnected) {
					TextButton(
						onClick = onRefresh,
						contentPadding = PaddingValues(horizontal = 8.dp)
					) {
						Text(
							text = "Connect",
							fontFamily = AppFont.onestSemiBold,
							fontSize = 12.sp,
							color = AppPalette.darkRed
						)
					}
				}
			}
		}
	}
}

@Composable
private fun WifiStatusHeaderPreviewItem(
    isConnected: Boolean = true,
    ssid: String = "Home_WiFi",
    ipAddress: String = "192.168.1.5",
    isRefreshing: Boolean = false
) {
    AppPreview {
        WifiStatusHeader(
            isConnected = isConnected,
            ssid = ssid,
            ipAddress = ipAddress,
            isRefreshing = isRefreshing,
            onRefresh = {},
            onSettingsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WifiStatusHeaderConnectedPreview() {
    WifiStatusHeaderPreviewItem()
}

@Preview(showBackground = true)
@Composable
fun WifiStatusHeaderDisconnectedPreview() {
    WifiStatusHeaderPreviewItem(
        isConnected = false,
        ssid = "",
        ipAddress = ""
    )
}

@Preview(showBackground = true)
@Composable
fun WifiStatusHeaderRefreshingPreview() {
    WifiStatusHeaderPreviewItem(
        isRefreshing = true
    )
}
