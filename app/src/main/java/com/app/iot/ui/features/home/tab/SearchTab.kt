package com.app.iot.ui.features.home.tab

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.iot.data.ApiPath
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.OnestBold
import com.app.iot.ui.theme.OnestMedium
import com.app.iot.ui.theme.OnestRegular
import com.app.iot.ui.theme.OnestSemiBold
import com.app.iot.util.UiState

@Composable
fun SearchTab(
    innerPadding: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scanState by viewModel.scanState.collectAsState()
    val ledState by viewModel.ledState.collectAsState()
    
    var isWifiConnected by remember { mutableStateOf(viewModel.isWifiConnected()) }
    var systemIpAddress by remember { mutableStateOf(viewModel.getWifiIpAddress()) }

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isWifiConnected = true
                systemIpAddress = viewModel.getWifiIpAddress()
            }

            override fun onLost(network: Network) {
                isWifiConnected = viewModel.isWifiConnected()
                systemIpAddress = viewModel.getWifiIpAddress()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                isWifiConnected = true
                systemIpAddress = viewModel.getWifiIpAddress()
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Device Discovery",
            fontFamily = OnestBold,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Scan Button Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White.copy(alpha = 0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isWifiConnected) "Connected to WiFi" else "Not connected to WiFi",
                    fontFamily = OnestMedium,
                    fontSize = 14.sp,
                    color = if (isWifiConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                if (isWifiConnected) {
                    Text(
                        text = "IP: $systemIpAddress",
                        fontFamily = OnestRegular,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.findEspDevices(systemIpAddress) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64A19)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isWifiConnected && scanState !is UiState.Loading
                ) {
                    if (scanState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search for Devices", fontFamily = OnestSemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Found Devices",
            fontFamily = OnestSemiBold,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            when (val state = scanState) {
                is UiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { device ->
                            SearchDeviceItem(
                                name = device.name,
                                ip = device.ip,
                                onTurnOn = {
                                    ApiPath.LOCAL_WIFI_IP_URL = device.ip
                                    viewModel.controlLed(1, true)
                                },
                                onTurnOff = {
                                    ApiPath.LOCAL_WIFI_IP_URL = device.ip
                                    viewModel.controlLed(1, false)
                                }
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.White, fontFamily = OnestMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
                is UiState.Loading -> {
                    // Loading is handled by button
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No devices found yet. Tap search to begin.", color = Color.White.copy(alpha = 0.7f), fontFamily = OnestRegular)
                    }
                }
            }
        }
        
        if (ledState is UiState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun SearchDeviceItem(
    name: String,
    ip: String,
    onTurnOn: () -> Unit,
    onTurnOff: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE64A19).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = Color(0xFFE64A19))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = name, fontFamily = OnestSemiBold, fontSize = 16.sp, color = Color(0xFF212121))
                    Text(text = ip, fontFamily = OnestRegular, fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            Row {
                Button(
                    onClick = onTurnOn,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("ON", fontFamily = OnestMedium, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onTurnOff,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("OFF", fontFamily = OnestMedium, fontSize = 12.sp)
                }
            }
        }
    }
}
