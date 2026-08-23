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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.theme.OnestMedium
import com.app.iot.ui.theme.OnestRegular
import com.app.iot.ui.theme.OnestSemiBold

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
			color = Color.White
		) {
			Row(
				modifier = Modifier.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				if (isRefreshing) {
					CircularProgressIndicator(
						modifier = Modifier.size(24.dp),
						strokeWidth = 2.dp,
						color = Color(0xFF4CAF50)
					)
				} else {
					Icon(
						imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
						contentDescription = null,
						tint = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
						modifier = Modifier.size(24.dp)
					)
				}
				Spacer(modifier = Modifier.width(12.dp))
				Column {
					Text(
						text = if (isConnected) (if (ssid.isNotEmpty()) ssid else "System Online") else "System Offline",
						fontFamily = OnestMedium,
						fontSize = 14.sp,
						color = if (isConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
					)
					if (isConnected) {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Text(
								text = "IP: $ipAddress",
								fontFamily = OnestRegular,
								fontSize = 11.sp,
								color = Color.Gray
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
							tint = Color.Gray,
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
							fontFamily = OnestSemiBold,
							fontSize = 12.sp,
							color = Color(0xFFC62828)
						)
					}
				}
			}
		}
	}
}