package com.app.iot.ui.features.common.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.iot.BuildConfig
import com.app.iot.R
import com.app.iot.data.ApiPath
import com.app.iot.ui.components.core.AppImage
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview
import com.app.iot.util.UiState
import com.commandiron.compose_loading.Wave
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LauncherScreen(
	onNavigateToMain: () -> Unit,
	viewModel: HomeViewModel = hiltViewModel()
) {
	val context = LocalContext.current
	val scanState by viewModel.scanState.collectAsState()
	var statusText by remember { mutableStateOf("Initializing...") }
	var isWifiReady by remember { mutableStateOf(false) }
	
	DisposableEffect(context) {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkRequest = NetworkRequest.Builder()
			.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
			.build()
		
		val networkCallback = object : ConnectivityManager.NetworkCallback() {
			override fun onAvailable(network: Network) {
				connectivityManager.bindProcessToNetwork(network)
				isWifiReady = true
			}
			
			override fun onLost(network: Network) {
				connectivityManager.bindProcessToNetwork(null)
				isWifiReady = false
			}
		}
		
		connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
		
		onDispose {
			connectivityManager.unregisterNetworkCallback(networkCallback)
		}
	}
	
	LaunchedEffect(isWifiReady) {
		if (isWifiReady) {
			statusText = "Scanning for devices..."
			val ip = viewModel.getWifiIpAddress()
			viewModel.findEspDevices(ip)
		} else {
			delay(3.seconds)
			if (!isWifiReady) {
				statusText = "WiFi not connected"
				delay(2.seconds)
				onNavigateToMain()
			}
		}
	}
	
	LaunchedEffect(scanState) {
		when (val state = scanState) {
			is UiState.Success -> {
				val firstDevice = state.data.firstOrNull()
				if (firstDevice != null) {
					statusText = "Device found: ${firstDevice.name}"
					ApiPath.LOCAL_WIFI_IP_URL = firstDevice.ip
					ApiPath.SELECTED_DEVICE_NAME = firstDevice.name
					delay(1.seconds)
					onNavigateToMain()
				} else {
					statusText = "No devices found"
					delay(2.seconds)
					onNavigateToMain()
				}
			}
			
			is UiState.Error -> {
				if (statusText == "Scanning for devices...") {
					statusText = state.message
					delay(2.seconds)
					onNavigateToMain()
				}
			}
			
			else -> {}
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
				AppText(
					text = "Version ${BuildConfig.VERSION_NAME}",
					fontFamily = AppFont.onestMedium,
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
