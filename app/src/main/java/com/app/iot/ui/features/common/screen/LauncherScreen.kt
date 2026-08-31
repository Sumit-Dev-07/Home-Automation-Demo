package com.app.iot.ui.features.common.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.iot.BuildConfig
import com.app.iot.R
import com.app.iot.ui.components.core.AppImage
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview
import com.commandiron.compose_loading.Wave
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LauncherScreen(
	onNavigateToMain: () -> Unit,
	onNavigateToSearch: () -> Unit,
	viewModel: HomeViewModel = hiltViewModel()
) {
	val selectedDevice by viewModel.selectedDevice.collectAsState()
	LaunchedEffect(Unit) {
		delay(1.5.seconds)
		if (selectedDevice != null) {
			delay(0.5.seconds)
			onNavigateToMain()
		} else {
			delay(0.5.seconds)
			onNavigateToSearch()
		}
	}
	LauncherContent()
}

@Composable
fun LauncherContent() {
	Scaffold(
		contentColor = AppPalette.white
	) { innerPadding ->
		Box(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
				.background(AppPalette.white),
			contentAlignment = Alignment.Center
		) {
			AppImage(
				imageRes = R.drawable.app_logo,
				size = 150.dp
			)
			
			Column(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Wave(
					color = AppPalette.secondary,
					size = 24.dp
				)
				Spacer(modifier = Modifier.height(12.dp))
				AppText.Medium(
					text = "Version ${BuildConfig.VERSION_NAME}",
					color = AppPalette.gray,
				)
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LauncherPreview() {
	AppPreview(padding = 0.dp, color = AppPalette.white) {
		LauncherContent()
	}
}
