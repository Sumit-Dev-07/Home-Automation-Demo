package com.app.iot.ui.features.home.tab

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.iot.R
import com.app.iot.data.ApiPath
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.features.home.search.SearchDeviceScreen
import com.app.iot.ui.features.home.tab.components.BannerData
import com.app.iot.ui.features.home.tab.components.DeviceDetailSheet
import com.app.iot.ui.features.home.tab.components.HomeBannerList
import com.app.iot.ui.features.home.tab.components.HomeHeader
import com.app.iot.ui.features.home.tab.components.VerticalOnOffToggle
import com.app.iot.ui.features.home.viewmodel.DeviceStatus
import com.app.iot.ui.features.home.viewmodel.DiscoveredDevice
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.AppPalette
import com.app.iot.util.UiState
import com.app.iot.util.isPreview
import okhttp3.ResponseBody

@Composable
fun HomeTab(
	modifier: PaddingValues,
	viewModel: HomeViewModel? = if (isPreview()) null else hiltViewModel()
) {
	if (isPreview()) {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text("Home Tab Preview", color = AppPalette.white)
		}
		return
	}
	
	// Safe to use viewModel here as it won't be null when not in inspection mode
	val homeViewModel = viewModel!!
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	var isAppWifiConnected by remember { mutableStateOf(false) }
	var systemIpAddress by remember { mutableStateOf("0.0.0.0") }
	var wifiSsid by remember { mutableStateOf("") }
	var selectedDeviceIp by remember { mutableStateOf(ApiPath.LOCAL_WIFI_IP_URL) }
	var selectedDeviceName by remember { mutableStateOf(ApiPath.SELECTED_DEVICE_NAME) }
	
	var showAddDeviceDialog by remember { mutableStateOf(false) }
	var showWifiDialog by remember { mutableStateOf(false) }
	var showSearchFlow by remember { mutableStateOf(false) }
	var deviceToDelete by remember { mutableStateOf<DeviceStatus?>(null) }
	var deviceForDetail by remember { mutableStateOf<DeviceStatus?>(null) }
	
	val ledState by homeViewModel.ledState.collectAsState()
	val statusState by homeViewModel.statusState.collectAsState()
	val scanState by homeViewModel.scanState.collectAsState()
	val devices by homeViewModel.devices.collectAsState()
	val addDeviceState by homeViewModel.addDeviceState.collectAsState()
	val updateWifiState by homeViewModel.updateWifiState.collectAsState()
	val removeDeviceState by homeViewModel.removeDeviceState.collectAsState()
	
	val banners = remember {
		listOf(
			BannerData(
				"Smart Home",
				"Optimize your energy consumption with AI.",
				listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
			),
			BannerData(
				"Test",
				"Physical therapy for body function improvement.",
				listOf(Color(0xFF8E24AA), Color(0xFF64B5F6))
			),
		)
	}
	
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
				systemIpAddress = homeViewModel.getWifiIpAddress()
				wifiSsid = homeViewModel.getWifiSsid()
			}
			
			override fun onLost(network: Network) {
				isAppWifiConnected = false
				connectivityManager.bindProcessToNetwork(null)
				systemIpAddress = "0.0.0.0"
				wifiSsid = ""
				homeViewModel.clearDevices()
			}
			
			override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
				systemIpAddress = homeViewModel.getWifiIpAddress()
				wifiSsid = homeViewModel.getWifiSsid()
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
			HomeHeader(isConnected = isAppWifiConnected, onDeviceClick = {
				showSearchFlow = true
			}, onWifiClick = {
				context.startActivity(
					Intent(Settings.ACTION_WIFI_SETTINGS)
				)
			})
			
			Spacer(modifier = Modifier.height(0.dp))
			
			HomeBannerList(banners = banners)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			/*WifiStatusHeader(
				isConnected = isAppWifiConnected,
				ssid = wifiSsid,
				ipAddress = systemIpAddress,
				//isRefreshing = statusState is UiState.Loading,
				isRefreshing = false,
				onRefresh = {
					systemIpAddress = homeViewModel.getWifiIpAddress()
					wifiSsid = homeViewModel.getWifiSsid()
				},
				onSettingsClick = { showWifiDialog = true }
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			if (selectedDeviceIp.isNotEmpty() && isAppWifiConnected) {
				SelectedDeviceCard(
					name = selectedDeviceName.ifEmpty { "Connected Device" },
					ipAddress = selectedDeviceIp,
					onChange = {
						showSearchFlow = true
					}
				)
				Spacer(modifier = Modifier.height(16.dp))
			} else if (isAppWifiConnected) {
				FindDevicesCard(onFind = { showSearchFlow = true })
				Spacer(modifier = Modifier.height(16.dp))
			}*/
			
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
									homeViewModel.controlLed(device.id, isChecked)
								}
							},
							onDelete = {
								deviceToDelete = device
							},
							onClick = {
								deviceForDetail = device
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
				homeViewModel.fetchStatus()
			}
		}
		
		if (showSearchFlow) {
			Dialog(
				onDismissRequest = { showSearchFlow = false },
				properties = DialogProperties(
					usePlatformDefaultWidth = false,
					decorFitsSystemWindows = false
				)
			) {
				Surface(modifier = Modifier.fillMaxSize()) {
					SearchDeviceScreen(
						onClose = { showSearchFlow = false },
						onAddManually = {
							showSearchFlow = false
							showAddDeviceDialog = true
						},
						onDeviceSelected = { name, ip ->
							selectedDeviceName = name
							selectedDeviceIp = ip
							ApiPath.SELECTED_DEVICE_NAME = name
							ApiPath.LOCAL_WIFI_IP_URL = ip
							homeViewModel.fetchStatus()
							showSearchFlow = false
						}
					)
				}
			}
		}
		
		if (showAddDeviceDialog) {
			AddDeviceDialog(
				state = addDeviceState,
				onDismiss = {
					showAddDeviceDialog = false
					homeViewModel.resetActionStates()
				},
				onAdd = { name, pin, syncPin ->
					homeViewModel.addDevice(name, pin, syncPin)
				}
			)
		}
		
		if (showWifiDialog) {
			WifiConfigDialog(
				state = updateWifiState,
				onDismiss = {
					showWifiDialog = false
					homeViewModel.resetActionStates()
				},
				onUpdate = { password ->
					homeViewModel.updateWifi(password)
				}
			)
		}
		
		LaunchedEffect(addDeviceState) {
			if (addDeviceState is UiState.Success) {
				snackbarHostState.showSnackbar("Device added successfully")
				showAddDeviceDialog = false
				homeViewModel.resetActionStates()
			} else if (addDeviceState is UiState.Error) {
				snackbarHostState.showSnackbar((addDeviceState as UiState.Error).message)
			}
		}
		
		LaunchedEffect(updateWifiState) {
			if (updateWifiState is UiState.Success) {
				snackbarHostState.showSnackbar("WiFi password updated. Device will restart.")
				showWifiDialog = false
				homeViewModel.resetActionStates()
			} else if (updateWifiState is UiState.Error) {
				snackbarHostState.showSnackbar((updateWifiState as UiState.Error).message)
			}
		}
		
		LaunchedEffect(removeDeviceState) {
			if (removeDeviceState is UiState.Success) {
				snackbarHostState.showSnackbar("Device removed successfully")
				homeViewModel.resetActionStates()
			} else if (removeDeviceState is UiState.Error) {
				snackbarHostState.showSnackbar((removeDeviceState as UiState.Error).message)
			}
		}
		
		deviceToDelete?.let { device ->
			DeleteConfirmationDialog(
				deviceName = device.name,
				onDismiss = { deviceToDelete = null },
				onConfirm = {
					homeViewModel.removeDevice(device.name)
					deviceToDelete = null
				}
			)
		}
		
		deviceForDetail?.let { device ->
			DeviceDetailSheet(
				deviceName = device.name,
				onDismiss = { deviceForDetail = null }
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
						AppText.SemiBold(
							text = name,
							fontSize = 16.sp,
							color = AppPalette.black
						)
						AppText.Normal(
							text = "IP: $ipAddress",
							fontSize = 12.sp,
							color = AppPalette.gray
						)
					}
				}
				
				TextButton(onClick = onChange) {
					AppText.SemiBold(
						text = "Change",
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
						AppText.SemiBold(
							text = "Find Devices",
							fontSize = 16.sp,
							color = AppPalette.black
						)
						AppText.Normal(
							text = "Scan network for ESP devices",
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
					AppText.Medium(
						text = "Scan",
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
					AppText.Bold(
						text = "Scanning Network",
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
									AppText.Normal(
										text = "Searching for ESP devices...",
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
													AppText.Medium(
														text = device.name,
														fontSize = 14.sp,
														color = AppPalette.darkGray
													)
													AppText.Normal(
														text = device.ip,
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
								AppText.Normal(
									text = state.message,
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
						AppText.SemiBold(
							text = "Close",
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
	onDelete: () -> Unit,
	onClick: () -> Unit
) {
	val outerCornerRadius = 28.dp
	val innerCornerRadius = 24.dp
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
			.height(180.dp),
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
				.clip(RoundedCornerShape(innerCornerRadius))
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null,
					onClick = onClick
				),
			color = AppPalette.white
		) {
			Row(
				modifier = Modifier
					.padding(16.dp)
					.fillMaxSize(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Top
			) {
				Column(
					modifier = Modifier
						.weight(1f)
						.fillMaxHeight(),
					verticalArrangement = Arrangement.SpaceBetween
				) {
					Icon(
						imageVector = icon,
						contentDescription = null,
						tint = if (device.isOn && device.isConnected) AppPalette.primary
						else if (!device.isConnected) AppPalette.red
						else AppPalette.gray,
						modifier = Modifier.size(32.dp)
					)
					
					Column {
						AppText.SemiBold(
							text = device.name,
							fontSize = 18.sp,
							color = if (!device.isConnected) AppPalette.darkRed else AppPalette.black
						)
						
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier.padding(vertical = 4.dp)
						) {
							Box(
								modifier = Modifier
									.size(6.dp)
									.background(
										color = if (device.isConnected) AppPalette.green else AppPalette.red,
										shape = RoundedCornerShape(50)
									)
							)
							Spacer(modifier = Modifier.width(6.dp))
							AppText.Medium(
								text = if (device.isConnected) "Active" else "Inactive",
								fontSize = 12.sp,
								color = if (device.isConnected) AppPalette.green else AppPalette.red
							)
						}
					}
				}
				
				VerticalOnOffToggle(
					isOn = device.isOn,
					onStatusChange = onCheckedChange,
					enabled = device.isConnected,
				)
				
				/*Column(
					modifier = Modifier.fillMaxHeight(),
					horizontalAlignment = Alignment.End,
					verticalArrangement = Arrangement.SpaceBetween
				) {
					VerticalOnOffToggle(
						isOn = device.isOn,
						onStatusChange = onCheckedChange,
						enabled = device.isConnected
					)
					
					IconButton(
						onClick = onDelete,
						modifier = Modifier.size(32.dp)
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_remove),
							contentDescription = "Delete",
							tint = AppPalette.red.copy(alpha = 0.6f),
							modifier = Modifier.size(20.dp)
						)
					}
					
					
				}*/
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
					AppText.Bold(
						text = "Add New Device",
						fontSize = 20.sp,
						color = AppPalette.black
					)
					
					Spacer(modifier = Modifier.height(24.dp))
					
					OutlinedTextField(
						value = name,
						onValueChange = { name = it },
						label = { AppText.Normal(text = "Device Name") },
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
								AppText.Medium(text = "Cancel", color = AppPalette.gray)
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
								AppText.Medium(text = "Add Device")
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
			label = { AppText.Normal(text = label) },
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
					text = { AppText.Normal(text = p) },
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
					AppText.Bold(
						text = "WiFi Configuration",
						fontSize = 20.sp,
						color = AppPalette.black
					)
					
					Spacer(modifier = Modifier.height(16.dp))
					AppText.Normal(
						text = "Enter new WiFi password. The device will restart and connect to current SSID with this password.",
						fontSize = 12.sp,
						color = AppPalette.gray,
						textAlign = TextAlign.Center
					)
					
					Spacer(modifier = Modifier.height(24.dp))
					
					OutlinedTextField(
						value = password,
						onValueChange = { password = it },
						label = { AppText.Normal(text = "New WiFi Password") },
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
								AppText.Medium(text = "Cancel", color = AppPalette.gray)
							}
							Spacer(modifier = Modifier.width(8.dp))
							Button(
								onClick = { onUpdate(password) },
								enabled = password.length >= 8,
								colors = ButtonDefaults.buttonColors(containerColor = AppPalette.primary),
								shape = RoundedCornerShape(12.dp)
							) {
								AppText.Medium(text = "Update")
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
					
					AppText.Bold(
						text = "Delete Device?",
						fontSize = 20.sp,
						color = AppPalette.black
					)
					
					Spacer(modifier = Modifier.height(12.dp))
					
					AppText.Normal(
						text = "Are you sure you want to remove '$deviceName'? This action cannot be undone.",
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
							AppText.Medium(text = "Cancel", color = AppPalette.gray)
						}
						
						Button(
							onClick = onConfirm,
							modifier = Modifier.weight(1f),
							colors = ButtonDefaults.buttonColors(containerColor = AppPalette.red),
							shape = RoundedCornerShape(12.dp)
						) {
							AppText.Medium(text = "Delete", color = AppPalette.white)
						}
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun DeviceItemPreview() {
	Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
		DeviceItem(
			device = DeviceStatus(name = "Living Room Light", isOn = true, isConnected = true, ipAddress = "1.1.1.1"),
			onCheckedChange = {},
			onDelete = {},
			onClick = {}
		)
		DeviceItem(
			device = DeviceStatus(name = "Bedroom Light", isOn = false, isConnected = true, ipAddress = "1.1.1.2"),
			onCheckedChange = {},
			onDelete = {},
			onClick = {}
		)
		DeviceItem(
			device = DeviceStatus(name = "Kitchen Light", isOn = false, isConnected = false, ipAddress = "1.1.1.3"),
			onCheckedChange = {},
			onDelete = {},
			onClick = {}
		)
	}
}







