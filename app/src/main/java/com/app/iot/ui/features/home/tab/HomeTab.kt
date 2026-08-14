package com.app.iot.ui.features.home.tab

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.iot.R
import com.app.iot.data.ApiPath
import com.app.iot.ui.features.home.viewmodel.DeviceStatus
import com.app.iot.ui.features.home.viewmodel.DiscoveredDevice
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.OnestBold
import com.app.iot.ui.theme.OnestMedium
import com.app.iot.ui.theme.OnestRegular
import com.app.iot.ui.theme.OnestSemiBold
import com.app.iot.util.UiState

@Composable
fun HomeTab(
    modifier: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isAppWifiConnected by remember { mutableStateOf(false) }
    var systemIpAddress by remember { mutableStateOf("0.0.0.0") }
    var wifiSsid by remember { mutableStateOf("") }
    var selectedDeviceIp by remember { mutableStateOf(ApiPath.LOCAL_WIFI_IP_URL) }
    var selectedDeviceName by remember { mutableStateOf("") }

    val ledState by viewModel.ledState.collectAsState()
    val statusState by viewModel.statusState.collectAsState()
    val scanState by viewModel.scanState.collectAsState()
    val devices by viewModel.devices.collectAsState()

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isAppWifiConnected = true
                systemIpAddress = viewModel.getWifiIpAddress()
                wifiSsid = viewModel.getWifiSsid()
            }

            override fun onLost(network: Network) {
                isAppWifiConnected = false
                systemIpAddress = "0.0.0.0"
                wifiSsid = ""
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                systemIpAddress = viewModel.getWifiIpAddress()
                wifiSsid = viewModel.getWifiSsid()
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(modifier)
                .padding(16.dp)
        ) {
            // App WiFi Status
            WifiStatusHeader(
                isConnected = isAppWifiConnected,
                ssid = wifiSsid,
                ipAddress = systemIpAddress,
                isRefreshing = statusState is UiState.Loading,
                onRefresh = {
                    systemIpAddress = viewModel.getWifiIpAddress()
                    wifiSsid = viewModel.getWifiSsid()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedDeviceIp.isNotEmpty()) {
                SelectedDeviceCard(
                    name = selectedDeviceName.ifEmpty { "Connected Device" },
                    ipAddress = selectedDeviceIp,
                    onChange = {
                        viewModel.findEspDevices(systemIpAddress)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else if (isAppWifiConnected) {
                FindDevicesCard(onFind = { viewModel.findEspDevices(systemIpAddress) })
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(devices) { device ->
                    DeviceItem(
                        device = device,
                        onCheckedChange = { isChecked ->
                            // Trigger API call for Light (LED)
                            if (isAppWifiConnected) {
                                viewModel.controlLed(device.id, isChecked)
                            }
                        }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Error handling
        LaunchedEffect(ledState) {
            if (ledState is UiState.Error) {
                snackbarHostState.showSnackbar((ledState as UiState.Error).message)
            }
        }

        // Fetch status when IP is available
        LaunchedEffect(systemIpAddress) {
            if (systemIpAddress != "0.0.0.0" && isAppWifiConnected) {
                viewModel.fetchStatus()
            }
        }

        if (scanState is UiState.Loading || scanState is UiState.Success || scanState is UiState.Error) {
            DiscoveryDialog(
                state = scanState,
                onDismiss = { viewModel.resetScanState() },
                onSelectDevice = { device ->
                    ApiPath.LOCAL_WIFI_IP_URL = device.ip
                    selectedDeviceIp = device.ip
                    selectedDeviceName = device.name
                    viewModel.resetScanState()
                    viewModel.fetchStatus()
                }
            )
        }
    }
}

@Composable
fun SelectedDeviceCard(name: String, ipAddress: String, onChange: () -> Unit) {
    val outerCornerRadius = 24.dp
    val innerCornerRadius = 20.dp
    val gap = 6.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer layer (Glow/Border effect)
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

        // Main Card
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeveloperBoard,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = name,
                            fontFamily = OnestSemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = "IP: $ipAddress",
                            fontFamily = OnestRegular,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                TextButton(onClick = onChange) {
                    Text(
                        text = "Change",
                        fontFamily = OnestSemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFFE64A19)
                    )
                }
            }
        }
    }
}

@Composable
fun FindDevicesCard(onFind: () -> Unit) {
    val outerCornerRadius = 24.dp
    val innerCornerRadius = 20.dp
    val gap = 6.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer layer (Glow/Border effect)
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

        // Main Card
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_devices),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Find Devices",
                            fontFamily = OnestSemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = "Scan network for ESP devices",
                            fontFamily = OnestRegular,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = onFind,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE64A19)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Scan",
                        fontFamily = OnestMedium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DiscoveryDialog(
    state: UiState<List<DiscoveredDevice>>,
    onDismiss: () -> Unit,
    onSelectDevice: (DiscoveredDevice) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val outerCornerRadius = 24.dp
        val innerCornerRadius = 20.dp
        val gap = 6.dp

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            // Outer layer (Glow/Border effect)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(outerCornerRadius))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .border(
                        1.dp,
                        Color.Black.copy(alpha = 0.1f),
                        RoundedCornerShape(outerCornerRadius),
                    )
            )

            // Main Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gap)
                    .clip(RoundedCornerShape(innerCornerRadius)),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scanning Network",
                        fontFamily = OnestBold,
                        fontSize = 18.sp,
                        color = Color(0xFF212121)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (state) {
                            is UiState.Loading -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF4CAF50),
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Searching for ESP devices...",
                                        fontFamily = OnestRegular,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            is UiState.Success -> {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.data) { device ->
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onSelectDevice(device)
                                                    onDismiss()
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFF5F5F5)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Wifi,
                                                    contentDescription = null,
                                                    tint = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = device.name,
                                                        fontFamily = OnestMedium,
                                                        fontSize = 14.sp,
                                                        color = Color(0xFF424242)
                                                    )
                                                    Text(
                                                        text = device.ip,
                                                        fontFamily = OnestRegular,
                                                        fontSize = 11.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            is UiState.Error -> {
                                Text(
                                    state.message,
                                    fontFamily = OnestRegular,
                                    fontSize = 14.sp,
                                    color = Color(0xFFF44336),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            else -> {}
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Close",
                            fontFamily = OnestSemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFFE64A19)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WifiStatusHeader(
    isConnected: Boolean,
    ssid: String,
    ipAddress: String,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
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
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF4CAF50)
                    )
                } else {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isConnected) (if (ssid.isNotEmpty()) ssid else "System Online") else "System Offline",
                        fontFamily = OnestMedium,
                        fontSize = 14.sp,
                        color = if (isConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    if (isConnected) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "IP: $ipAddress",
                                fontFamily = OnestRegular,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (!isConnected) {
                    TextButton(
                        onClick = onRefresh,
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

@Composable
fun DeviceItem(
    device: DeviceStatus,
    onCheckedChange: (Boolean) -> Unit
) {
    val outerCornerRadius = 24.dp
    val innerCornerRadius = 20.dp
    val gap = 6.dp

    val icon = when (device.iconType) {
        "ac" -> Icons.Default.Air
        "tv" -> Icons.Default.Tv
        "thermostat" -> Icons.Default.DeviceThermostat
        else -> Icons.Default.Lightbulb
    }

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
                        imageVector = icon,
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
