package com.app.iot.ui.features.home.tab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.R
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

@Composable
fun HomeHeader(
	isConnected: Boolean,
	onDeviceClick: () -> Unit,
	onWifiClick: () -> Unit
) {
	val innerCornerRadius = 20.dp
	
	Box(
		modifier = Modifier.fillMaxWidth(),
		contentAlignment = Alignment.Center
	) {
		
		Surface(
			modifier = Modifier
				.clip(RoundedCornerShape(innerCornerRadius)),
			color = AppPalette.transparent
		) {
			
			Row(
				modifier = Modifier.fillMaxWidth().padding(16.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				AppText.Medium(
					"Good Morning",
					color = AppPalette.black
				)
				
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					
					Surface(
						modifier = Modifier.size(42.dp),
						shape = RoundedCornerShape(12.dp),
						color = AppPalette.gray.copy(alpha = 0.1f),
						onClick = onDeviceClick
					) {
						Box(
							contentAlignment = Alignment.Center
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_product),
								contentDescription = null,
								tint = AppPalette.black,
								modifier = Modifier.size(24.dp)
							)
						}
					}
					Spacer(modifier = Modifier.width(width = 16.dp))
					Surface(
						modifier = Modifier.size(42.dp),
						shape = RoundedCornerShape(12.dp),
						color = AppPalette.white,
						onClick = onWifiClick
					) {
						Box(
							contentAlignment = Alignment.Center
						) {
							Icon(
								painter = if(isConnected) painterResource(id = R.drawable.ic_wifi) else painterResource(id = R.drawable.ic_wifi_off),
								contentDescription = null,
								tint = AppPalette.black,
								modifier = Modifier.size(20.dp)
							)
						}
					}
					
				}
			}
		}
	}
}

@Composable
private fun HomeHeaderPreviewItem(
	isConnected: Boolean = true,
) {
	AppPreview(color = AppPalette.transparent) {
		HomeHeader(
			isConnected = isConnected,
			onDeviceClick = {},
			onWifiClick = {}
		)
	}
}

@Preview(showBackground = true)
@Composable
fun HomeHeaderConnectedPreview() {
	HomeHeaderPreviewItem()
}

@Preview(showBackground = true)
@Composable
fun HomeHeaderDisconnectedPreview() {
	HomeHeaderPreviewItem(false)
}
