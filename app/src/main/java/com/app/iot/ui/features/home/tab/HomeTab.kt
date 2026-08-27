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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.iot.R
import com.app.iot.data.ApiPath
import com.app.iot.ui.features.home.tab.components.WifiStatusHeader
import com.app.iot.ui.features.home.viewmodel.DeviceStatus
import com.app.iot.ui.features.home.viewmodel.DiscoveredDevice
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.util.UiState
import okhttp3.ResponseBody

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
    var selectedDeviceName by remember { mutableStateOf(ApiPath.SELECTED_DEVICE_NAME) }

    var showAddDeviceDialog by remember { mutableStateOf(false) }
    var showWifiDialog by remember { mutableStateOf(false) }
    var deviceToDelete by remember { mutableStateOf<DeviceStatus?>(null) }

    val ledState by viewModel.ledState.collectAsState()
    val statusState by viewModel.statusState.collectAsState()
    val scanState by viewModel.scanState.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val addDeviceState by viewModel.addDeviceState.collectAsState()
    val updateWifiState by viewModel.updateWifiState.collectAsState()
    val removeDeviceState by viewModel.removeDeviceState.collectAsState()

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isAppWifiConnected = true
                connectivityManager.bindProcessToNetwork(network)
                systemIpAddress = viewModel.getWifiIpAddress()
                wifiSsid = viewModel.getWifiSsid()
            }

            override fun onLost(network: Network) {
                isAppWifiConnected = false
                connectivityManager.bindProcessToNetwork(null)
                systemIpAddress = "0.0.0.0"
                wifiSsid = ""
                viewModel.clearDevices()
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
                .padding(top = modifier.calculateTopPadding())
                .padding(16.dp)
        ) {
            WifiStatusHeader(
                isConnected = isAppWifiConnected,
                ssid = wifiSsid,
                ipAddress = systemIpAddress,
                isRefreshing = statusState is UiState.Loading,
                onRefresh = {
                    systemIpAddress = viewModel.getWifiIpAddress()
                    wifiSsid = viewModel.getWifiSsid()
                },
                onSettingsClick = { showWifiDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedDeviceIp.isNotEmpty() && isAppWifiConnected) {
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = modifier.calculateBottomPadding() + 80.dp)
            ) {
                if (devices.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeveloperBoard,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = AppPalette.white
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No devices found",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppPalette.white
                            )
                            Text(
                                text = "Tap to add a device",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppPalette.white
                            )
                        }
                    }
                } else {
                    items(devices) { device ->
                        DeviceItem(
                            device = device,
                            onCheckedChange = { isChecked ->
                                if (isAppWifiConnected) {
                                    viewModel.controlLed(device.id, isChecked)
                                }
                            },
                            onDelete = {
                                deviceToDelete = device
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDeviceDialog = true },
            containerColor = AppPalette.primary,
            contentColor = AppPalette.white,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = modifier.calculateBottomPadding() + 32.dp,
                    end = 24.dp
                )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Device")
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = modifier.calculateBottomPadding() + 16.dp)
        )

        LaunchedEffect(ledState) {
            if (ledState is UiState.Error) {
                snackbarHostState.showSnackbar((ledState as UiState.Error).message)
            }
        }

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
                    ApiPath.SELECTED_DEVICE_NAME = device.name
                    selectedDeviceIp = device.ip
                    selectedDeviceName = device.name
                    viewModel.resetScanState()
                    viewModel.fetchStatus()
                }
            )
        }

        if (showAddDeviceDialog) {
            AddDeviceDialog(
                state = addDeviceState,
                onDismiss = {
                    showAddDeviceDialog = false
                    viewModel.resetActionStates()
                },
                onAdd = { name, pin, syncPin ->
                    viewModel.addDevice(name, pin, syncPin)
                }
            )
        }

        if (showWifiDialog) {
            WifiConfigDialog(
                state = updateWifiState,
                onDismiss = {
                    showWifiDialog = false
                    viewModel.resetActionStates()
                },
                onUpdate = { password ->
                    viewModel.updateWifi(password)
                }
            )
        }

        LaunchedEffect(addDeviceState) {
            if (addDeviceState is UiState.Success) {
                snackbarHostState.showSnackbar("Device added successfully")
                showAddDeviceDialog = false
                viewModel.resetActionStates()
            } else if (addDeviceState is UiState.Error) {
                snackbarHostState.showSnackbar((addDeviceState as UiState.Error).message)
            }
        }

        LaunchedEffect(updateWifiState) {
            if (updateWifiState is UiState.Success) {
                snackbarHostState.showSnackbar("WiFi password updated. Device will restart.")
                showWifiDialog = false
                viewModel.resetActionStates()
            } else if (updateWifiState is UiState.Error) {
                snackbarHostState.showSnackbar((updateWifiState as UiState.Error).message)
            }
        }

        LaunchedEffect(removeDeviceState) {
            if (removeDeviceState is UiState.Success) {
                snackbarHostState.showSnackbar("Device removed successfully")
                viewModel.resetActionStates()
            } else if (removeDeviceState is UiState.Error) {
                snackbarHostState.showSnackbar((removeDeviceState as UiState.Error).message)
            }
        }

        deviceToDelete?.let { device ->
            DeleteConfirmationDialog(
                deviceName = device.name,
                onDismiss = { deviceToDelete = null },
                onConfirm = {
                    viewModel.removeDevice(device.name)
                    deviceToDelete = null
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(AppPalette.black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    AppPalette.black.copy(alpha = 0.1f),
                    RoundedCornerShape(outerCornerRadius),
                )
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = AppPalette.white
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
                        tint = AppPalette.green,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = name,
                            fontFamily = AppFont.onestSemiBold,
                            fontSize = 16.sp,
                            color = AppPalette.black
                        )
                        Text(
                            text = "IP: $ipAddress",
                            fontFamily = AppFont.onestRegular,
                            fontSize = 12.sp,
                            color = AppPalette.gray
                        )
                    }
                }

                TextButton(onClick = onChange) {
                    Text(
                        text = "Change",
                        fontFamily = AppFont.onestSemiBold,
                        fontSize = 12.sp,
                        color = AppPalette.primary
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(AppPalette.black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    AppPalette.black.copy(alpha = 0.1f),
                    RoundedCornerShape(outerCornerRadius),
                )
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = AppPalette.white
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
                            fontFamily = AppFont.onestSemiBold,
                            fontSize = 16.sp,
                            color = AppPalette.black
                        )
                        Text(
                            text = "Scan network for ESP devices",
                            fontFamily = AppFont.onestRegular,
                            fontSize = 12.sp,
                            color = AppPalette.gray
                        )
                    }
                }

                Button(
                    onClick = onFind,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPalette.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Scan",
                        fontFamily = AppFont.onestMedium,
                        fontSize = 14.sp,
                        color = AppPalette.white
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
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(outerCornerRadius))
                    .background(AppPalette.black.copy(alpha = 0.05f))
                    .border(
                        1.dp,
                        AppPalette.black.copy(alpha = 0.1f),
                        RoundedCornerShape(outerCornerRadius),
                    )
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gap)
                    .clip(RoundedCornerShape(innerCornerRadius)),
                color = AppPalette.white
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scanning Network",
                        fontFamily = AppFont.onestBold,
                        fontSize = 18.sp,
                        color = AppPalette.black
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
                                        color = AppPalette.green,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Searching for ESP devices...",
                                        fontFamily = AppFont.onestRegular,
                                        fontSize = 14.sp,
                                        color = AppPalette.gray
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
                                            color = AppPalette.lightGray
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Wifi,
                                                    contentDescription = null,
                                                    tint = AppPalette.green,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = device.name,
                                                        fontFamily = AppFont.onestMedium,
                                                        fontSize = 14.sp,
                                                        color = AppPalette.darkGray
                                                    )
                                                    Text(
                                                        text = device.ip,
                                                        fontFamily = AppFont.onestRegular,
                                                        fontSize = 11.sp,
                                                        color = AppPalette.gray
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
                                    fontFamily = AppFont.onestRegular,
                                    fontSize = 14.sp,
                                    color = AppPalette.red,
                                    textAlign = TextAlign.Center
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
                            fontFamily = AppFont.onestSemiBold,
                            fontSize = 14.sp,
                            color = AppPalette.primary
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
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(AppPalette.black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    AppPalette.black.copy(alpha = 0.1f),
                    RoundedCornerShape(outerCornerRadius),
                )
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = AppPalette.white
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (device.isOn && device.isConnected) AppPalette.primary
                            else if (!device.isConnected) AppPalette.red
                            else AppPalette.gray,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Switch(
                        checked = device.isOn,
                        onCheckedChange = onCheckedChange,
                        enabled = device.isConnected,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppPalette.white,
                            checkedTrackColor = AppPalette.primary,
                            uncheckedThumbColor = AppPalette.white,
                            uncheckedTrackColor = AppPalette.borderGray,
                            disabledCheckedTrackColor = AppPalette.primary.copy(alpha = 0.3f),
                            disabledUncheckedTrackColor = AppPalette.borderGray.copy(alpha = 0.3f)
                        )
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = device.name,
                            fontFamily = AppFont.onestSemiBold,
                            fontSize = 15.sp,
                            color = if (!device.isConnected) AppPalette.darkRed else AppPalette.black
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (device.isConnected) AppPalette.green else AppPalette.red,
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (device.isConnected) "Active" else "Inactive",
                                fontFamily = AppFont.onestRegular,
                                fontSize = 10.sp,
                                color = if (device.isConnected) AppPalette.green else AppPalette.red
                            )
                        }
                        Text(
                            text = if (device.isOn) "On" else "Off",
                            fontFamily = AppFont.onestMedium,
                            fontSize = 13.sp,
                            color = if (device.isOn && device.isConnected) AppPalette.primary
                            else AppPalette.gray
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove),
                            contentDescription = "Delete",
                            tint = AppPalette.red.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceDialog(
    state: UiState<ResponseBody>,
    onDismiss: () -> Unit,
    onAdd: (String, String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("D1") }
    var syncPin by remember { mutableStateOf("None") }
    val pins = listOf("D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8")
    val syncPins = listOf("None") + pins

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val outerCornerRadius = 24.dp
        val innerCornerRadius = 20.dp
        val gap = 6.dp

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(outerCornerRadius))
                    .background(AppPalette.black.copy(alpha = 0.05f))
                    .border(1.dp, AppPalette.black.copy(alpha = 0.1f), RoundedCornerShape(outerCornerRadius))
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gap)
                    .clip(RoundedCornerShape(innerCornerRadius)),
                color = AppPalette.white
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add New Device",
                        fontFamily = AppFont.onestBold,
                        fontSize = 20.sp,
                        color = AppPalette.black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Device Name", fontFamily = AppFont.onestRegular) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppPalette.primary,
                            focusedLabelColor = AppPalette.primary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PinDropdown(
                        label = "Select Pin",
                        selectedPin = pin,
                        pins = pins,
                        onPinSelected = { pin = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PinDropdown(
                        label = "Sync Pin (Optional)",
                        selectedPin = syncPin,
                        pins = syncPins,
                        onPinSelected = { syncPin = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state is UiState.Loading) {
                        CircularProgressIndicator(color = AppPalette.primary)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel", color = AppPalette.gray, fontFamily = AppFont.onestMedium)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onAdd(
                                        name,
                                        pin,
                                        if (syncPin == "None") null else syncPin
                                    )
                                },
                                enabled = name.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Add Device", fontFamily = AppFont.onestMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDropdown(
    label: String,
    selectedPin: String,
    pins: List<String>,
    onPinSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedPin,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontFamily = AppFont.onestRegular) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppPalette.primary,
                focusedLabelColor = AppPalette.primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            pins.forEach { p ->
                DropdownMenuItem(
                    text = { Text(p, fontFamily = AppFont.onestRegular) },
                    onClick = {
                        onPinSelected(p)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WifiConfigDialog(
    state: UiState<ResponseBody>,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val outerCornerRadius = 24.dp
        val innerCornerRadius = 20.dp
        val gap = 6.dp

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(outerCornerRadius))
                    .background(AppPalette.black.copy(alpha = 0.05f))
                    .border(1.dp, AppPalette.black.copy(alpha = 0.1f), RoundedCornerShape(outerCornerRadius))
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gap)
                    .clip(RoundedCornerShape(innerCornerRadius)),
                color = AppPalette.white
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WiFi Configuration",
                        fontFamily = AppFont.onestBold,
                        fontSize = 20.sp,
                        color = AppPalette.black
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enter new WiFi password. The device will restart and connect to current SSID with this password.",
                        fontFamily = AppFont.onestRegular,
                        fontSize = 12.sp,
                        color = AppPalette.gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("New WiFi Password", fontFamily = AppFont.onestRegular) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppPalette.primary,
                            focusedLabelColor = AppPalette.primary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state is UiState.Loading) {
                        CircularProgressIndicator(color = AppPalette.primary)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel", color = AppPalette.gray, fontFamily = AppFont.onestMedium)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onUpdate(password) },
                                enabled = password.length >= 8,
                                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Update", fontFamily = AppFont.onestMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    deviceName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
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
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(outerCornerRadius))
                    .background(AppPalette.black.copy(alpha = 0.05f))
                    .border(1.dp, AppPalette.black.copy(alpha = 0.1f), RoundedCornerShape(outerCornerRadius))
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gap)
                    .clip(RoundedCornerShape(innerCornerRadius)),
                color = AppPalette.white
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = null,
                        tint = AppPalette.red,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Delete Device?",
                        fontFamily = AppFont.onestBold,
                        fontSize = 20.sp,
                        color = AppPalette.black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Are you sure you want to remove '$deviceName'? This action cannot be undone.",
                        fontFamily = AppFont.onestRegular,
                        fontSize = 14.sp,
                        color = AppPalette.gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = AppPalette.gray, fontFamily = AppFont.onestMedium)
                        }

                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AppPalette.red),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Delete", color = AppPalette.white, fontFamily = AppFont.onestMedium)
                        }
                    }
                }
            }
        }
    }
}
