package com.app.iot

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.iot.nav.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private lateinit var connectivityManager: ConnectivityManager
	private val networkCallback = object : ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			connectivityManager.bindProcessToNetwork(network)
		}

		override fun onLost(network: Network) {
			connectivityManager.bindProcessToNetwork(null)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkRequest = NetworkRequest.Builder()
			.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
			.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
			.build()
		connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

		enableEdgeToEdge()
		setContent {
			AppNavHost()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		connectivityManager.unregisterNetworkCallback(networkCallback)
		connectivityManager.bindProcessToNetwork(null)
	}
}
