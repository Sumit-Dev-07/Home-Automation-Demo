package com.app.iot.ui.features.home.tab.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.R
import com.app.iot.ui.components.core.AppImage
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

@Composable
fun HomeHeader(
	isConnected: Boolean,
	onDeviceClick: () -> Unit,
	onWifiClick: () -> Unit
) {
	val innerCornerRadius = 0.dp
	
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
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					AppImage(
						imageRes = R.drawable.app_logo,
						size = 50.dp
					)
					Spacer(modifier = Modifier.width(width = 4.dp))
					Image(
						painter = painterResource(id = R.drawable.ic_smart_hub),
						contentDescription = null,
						modifier = Modifier.height(50.dp).width(120.dp)
					)
				}
				
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					val buttonSize = 48.dp
					val outerCorner = 12.dp
					val innerCorner = 10.dp
					val gap = 3.dp
					
					Box(
						modifier = Modifier
							.size(buttonSize),
						contentAlignment = Alignment.Center
					) {
						Box(
							modifier = Modifier
								.fillMaxSize()
								.clip(RoundedCornerShape(outerCorner))
								.background(AppPalette.black.copy(alpha = 0.05f))
								.border(
									1.dp,
									AppPalette.black.copy(alpha = 0.1f),
									RoundedCornerShape(outerCorner),
								)
						)
						
						Surface(
							modifier = Modifier
								.fillMaxSize()
								.padding(gap),
							shape = RoundedCornerShape(innerCorner),
							color = AppPalette.white,
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
					}
					
					Spacer(modifier = Modifier.width(width = 8.dp))
					
					Box(
						modifier = Modifier
							.size(buttonSize),
						contentAlignment = Alignment.Center
					) {
						Box(
							modifier = Modifier
								.fillMaxSize()
								.clip(RoundedCornerShape(outerCorner))
								.background(AppPalette.black.copy(alpha = 0.05f))
								.border(
									1.dp,
									AppPalette.black.copy(alpha = 0.1f),
									RoundedCornerShape(outerCorner),
								)
						)
						
						Surface(
							modifier = Modifier
								.fillMaxSize()
								.padding(gap),
							shape = RoundedCornerShape(innerCorner),
							color = AppPalette.white,
							onClick = onWifiClick
						) {
							Box(
								contentAlignment = Alignment.Center
							) {
								Icon(
									painter = if (isConnected) painterResource(id = R.drawable.ic_wifi) else painterResource(
										id = R.drawable.ic_wifi_off
									),
									contentDescription = null,
									tint = if (isConnected) AppPalette.green else AppPalette.red,
									modifier = Modifier.size(20.dp)
								)
							}
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
	AppPreview(color = AppPalette.secondary) {
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
