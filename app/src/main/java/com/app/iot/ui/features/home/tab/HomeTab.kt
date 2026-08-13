package com.app.iot.ui.features.home.tab

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.theme.OnestBold
import com.app.iot.ui.theme.OnestMedium
import com.app.iot.ui.theme.OnestRegular
import com.app.iot.ui.theme.OnestSemiBold

@Composable
fun HomeTab(modifier: PaddingValues) {
    val context = LocalContext.current
    var isAppWifiConnected by remember { mutableStateOf(false) }
    var systemIpAddress by remember { mutableStateOf("0.0.0.0") }

    fun updateIpAddress() {
        val wifiMan = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        systemIpAddress = if (ipAddress == 0) "0.0.0.0"
        else String.format(
            Locale.getDefault(),
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
    }

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isAppWifiConnected = true
                updateIpAddress()
            }

            override fun onLost(network: Network) {
                isAppWifiConnected = false
                systemIpAddress = "0.0.0.0"
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                // Update IP in case it changed (e.g. DHCP renewal)
                updateIpAddress()
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    var devices by remember {
        mutableStateOf(
            listOf(
                DeviceStatus("Light", Icons.Default.Lightbulb, true, true, "192.168.1.10"),
                DeviceStatus("AC", Icons.Default.Air, false, true, "192.168.1.15"),
                DeviceStatus("Smart TV", Icons.Default.Tv, true, false, "0.0.0.0"),
                DeviceStatus("Thermostat", Icons.Default.DeviceThermostat, false, true, "192.168.1.20")
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(modifier)
            .padding(16.dp)
    ) {
        // App WiFi Status
        WifiStatusHeader(isConnected = isAppWifiConnected, ipAddress = systemIpAddress)
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(devices) { device ->
                DeviceItem(
                    device = device,
                    onCheckedChange = { isChecked ->
                        devices = devices.map {
                            if (it.name == device.name) it.copy(isOn = isChecked) else it
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WifiStatusHeader(isConnected: Boolean, ipAddress: String) {
    val outerCornerRadius = 24.dp
    val innerCornerRadius = 20.dp
    val gap = 6.dp

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        // Main Card (White background like bottom nav inner bar)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isConnected) "System Online " else "System Offline",
                        fontFamily = OnestMedium,
                        fontSize = 14.sp,
                        color = if (isConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    if (isConnected) {
                        Text(
                            text = "IP: $ipAddress",
                            fontFamily = OnestRegular,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (!isConnected) {
                    TextButton(
                        onClick = { /* Refresh WiFi status */ },
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

data class DeviceStatus(
    val name: String,
    val icon: ImageVector,
    val isOn: Boolean,
    val isConnected: Boolean,
    val ipAddress: String
)

@Composable
fun DeviceItem(
    device: DeviceStatus,
    onCheckedChange: (Boolean) -> Unit
) {
    val outerCornerRadius = 24.dp
    val innerCornerRadius = 20.dp
    val gap = 6.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer layer (Glow/Border effect like bottom nav)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(Color.Black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    Color.Black.copy(alpha = 0.1f),
                    RoundedCornerShape(outerCornerRadius),
                )
        )

        // Main Card (White background like bottom nav inner bar)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = device.icon,
                        contentDescription = null,
                        tint = if (device.isOn && device.isConnected) Color(0xFFE64A19) 
                               else if (!device.isConnected) Color(0xFFF44336)
                               else Color(0xFF9E9E9E),
                        modifier = Modifier.size(28.dp)
                    )
                    Switch(
                        checked = device.isOn,
                        onCheckedChange = onCheckedChange,
                        enabled = device.isConnected,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFE64A19),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0),
                            disabledCheckedTrackColor = Color(0xFFE64A19).copy(alpha = 0.3f),
                            disabledUncheckedTrackColor = Color(0xFFE0E0E0).copy(alpha = 0.3f)
                        )
                    )
                }
                
                Column {
                    Text(
                        text = device.name,
                        fontFamily = OnestSemiBold,
                        fontSize = 15.sp,
                        color = if (!device.isConnected) Color(0xFFC62828) else Color(0xFF212121)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = if (device.isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    shape = RoundedCornerShape(50)
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (device.isConnected) "Active" else "Inactive",
                            fontFamily = OnestRegular,
                            fontSize = 10.sp,
                            color = if (device.isConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                    Text(
                        text = if (device.isOn) "On" else "Off",
                        fontFamily = OnestMedium,
                        fontSize = 13.sp,
                        color = if (device.isOn && device.isConnected) Color(0xFFE64A19) 
                                else Color(0xFF9E9E9E)
                    )
                }
            }
        }
    }
}
