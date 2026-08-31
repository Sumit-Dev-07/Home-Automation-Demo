package com.app.iot.ui.features.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.app.iot.bottom_nav.CustomBottomNavigation
import com.app.iot.ui.features.home.tab.HomeTab
import com.app.iot.ui.features.home.tab.ScheduleTab
import com.app.iot.ui.features.home.tab.SettingTab
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

@Composable
fun HomeScreen() {
	var selectedItem by remember { mutableIntStateOf(0) }
	val visited = remember { mutableStateOf(setOf(0)) }
	
	LaunchedEffect(selectedItem) {
		if (selectedItem !in visited.value) {
			visited.value += selectedItem
		}
	}
	
	Scaffold(
		containerColor = AppPalette.secondary,
		bottomBar = {
			CustomBottomNavigation(
				selectedItem = selectedItem,
				onItemSelected = { 
					if (it != selectedItem) {
						selectedItem = it 
					}
				}
			)
		}
	) { innerPadding ->
		Box(modifier = Modifier.fillMaxSize()) {
			for (i in 0..4) {
				if (i in visited.value) {
					Box(
						modifier = Modifier
							.fillMaxSize()
							.graphicsLayer {
								alpha = if (selectedItem == i) 1f else 0f
							}
							.zIndex(if (selectedItem == i) 1f else 0f)
					) {
						when (i) {
							0 -> HomeTab(innerPadding)
							1 -> ScheduleTab(innerPadding)
							2 -> BaseContent("Cart", innerPadding)
							3 -> BaseContent("Favorite", innerPadding)
							4 -> SettingTab(innerPadding)
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun HomeScreenPreview() {
	AppPreview(padding = 0.dp) {
		HomeScreen()
	}
}