package com.app.iot.ui.features.home.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.app.iot.bottom_nav.CustomBottomNavigation
import com.app.iot.ui.features.home.tab.HomeTab

@Composable
fun HomeScreen() {
	val context = LocalContext.current
	var selectedItem by remember { mutableIntStateOf(0) }
	
	// Handle Network Binding at HomeScreen level to ensure local communication
	// works even without internet on the Wi-Fi network.
	DisposableEffect(context) {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkRequest = NetworkRequest.Builder()
			.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
			.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
			.build()
		
		val networkCallback = object : ConnectivityManager.NetworkCallback() {
			override fun onAvailable(network: Network) {
				// Force the app to use the Wi-Fi network even if it has no internet
				connectivityManager.bindProcessToNetwork(network)
			}
			
			override fun onLost(network: Network) {
				connectivityManager.bindProcessToNetwork(null)
			}
		}
		
		connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
		
		onDispose {
			connectivityManager.unregisterNetworkCallback(networkCallback)
			connectivityManager.bindProcessToNetwork(null)
		}
	}
	
	Scaffold(
		containerColor = Color(0xFFF18673),
		bottomBar = {
			CustomBottomNavigation(
				selectedItem = selectedItem,
				onItemSelected = { selectedItem = it }
			)
		}
	) { innerPadding ->
		when (selectedItem) {
			0 -> HomeTab(innerPadding)
			1 -> BaseContent("Search", innerPadding)
			2 -> BaseContent("Cart", innerPadding)
			3 -> BaseContent("Favorite", innerPadding)
			4 -> BaseContent("Profile", innerPadding)
		}
	}
}