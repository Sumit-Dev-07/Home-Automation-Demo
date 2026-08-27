package com.app.iot.ui.features.common.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.commandiron.compose_loading.CubeGrid
import com.app.iot.BuildConfig
import com.app.iot.data.ApiPath
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.HomeAutomationTheme
import com.app.iot.util.UiState
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
            // Note: We don't unbind here because HomeTab will take over and bind it again.
            // If we unbind here, there might be a gap where the process is not bound.
        }
    }

    LaunchedEffect(isWifiReady) {
        if (isWifiReady) {
            statusText = "Scanning for devices..."
            val ip = viewModel.getWifiIpAddress()
            viewModel.findEspDevices(ip)
        } else {
            // Wait a bit to see if WiFi connects
            delay(3.seconds)
            if (!isWifiReady) {
                statusText = "WiFi not connected"
                delay(1.seconds)
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
                    delay(1.seconds)
                    onNavigateToMain()
                } else {
                    statusText = "No devices found"
                    delay(2.seconds)
                    onNavigateToMain()
                }
            }
            is UiState.Error -> {
                // Only show error if we were actually trying to scan
                if (statusText == "Scanning for devices...") {
                    statusText = state.message
                    delay(2.seconds)
                    onNavigateToMain()
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CubeGrid(
                color = AppPalette.secondary,
                size = DpSize(100.dp, 100.dp),
            )
        }

        AppText(
            "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            fontFamily = AppFont.onestSemiBold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            color = AppPalette.gray
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LauncherPreview() {
    HomeAutomationTheme() {
        LauncherScreen(onNavigateToMain = {})
    }
}
