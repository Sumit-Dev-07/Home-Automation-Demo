package com.app.iot.ui.features.home.search

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.iot.R
import com.app.iot.ui.features.home.search.components.DiscoveryList
import com.app.iot.ui.features.home.search.components.RadarGraphic
import com.app.iot.ui.features.home.viewmodel.ConnectionStatus
import com.app.iot.ui.features.home.viewmodel.DeviceDiscoveryStatus
import com.app.iot.ui.features.home.viewmodel.DiscoveredDevice
import com.app.iot.ui.features.home.viewmodel.HomeViewModel
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview
import com.app.iot.util.UiState
import com.app.iot.util.hasNearbyDevicePermissions
import com.app.iot.util.isPreview
import com.app.iot.util.nearbyDevicePermissions
import com.app.iot.util.openAppPermissionSettings
import com.app.iot.util.shouldShowNearbyPermissionRationale

enum class SearchFlowStep {
	INITIAL, SCANNING, RESULTS
}

@Composable
fun SearchDeviceScreen(
	onClose: () -> Unit,
	onAddManually: () -> Unit = {},
	onDeviceSelected: (name: String, ip: String) -> Unit = { _, _ -> },
	viewModel: HomeViewModel? = if (isPreview()) null else hiltViewModel()
) {
	if (isPreview()) {
		SearchDeviceContent(
			step = SearchFlowStep.INITIAL,
			wifiSsid = "Home_WiFi",
			devices = emptyList(),
			permissionMessage = null,
			showOpenSettings = false,
			onClose = onClose,
			onAllowAndContinue = {},
			onAddManually = onAddManually,
			onStopScanning = {},
			onScanAgain = {},
			onContinue = {},
			onOpenSettings = {},
			onRetry = {}
		)
		return
	}

	val homeViewModel = viewModel!!
	val context = LocalContext.current
	val activity = context as? Activity
	var currentStep by remember { mutableStateOf(SearchFlowStep.INITIAL) }
	var permissionMessage by remember { mutableStateOf<String?>(null) }
	var showOpenSettings by remember { mutableStateOf(false) }
	val discoveryState by homeViewModel.discoveryState.collectAsState()
	val scanState by homeViewModel.scanState.collectAsState()
	val wifiSsid = remember { homeViewModel.getWifiSsid() }

	fun startScan() {
		permissionMessage = null
		showOpenSettings = false
		homeViewModel.resetScanState()
		currentStep = SearchFlowStep.SCANNING
		homeViewModel.findEspDevices(homeViewModel.getWifiIpAddress())
	}

	val permissionLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { results ->
		if (results.isNotEmpty() && results.values.all { it }) {
			startScan()
		} else {
			val permanentlyDenied = activity != null &&
				!activity.shouldShowNearbyPermissionRationale()
			showOpenSettings = permanentlyDenied
			permissionMessage = if (permanentlyDenied) {
				"Nearby device access is required. Enable it in Settings to continue."
			} else {
				"Nearby device access is required to search for Wi-Fi and Bluetooth devices."
			}
		}
	}

	fun requestPermissionsOrScan() {
		if (context.hasNearbyDevicePermissions()) {
			startScan()
		} else {
			permissionLauncher.launch(nearbyDevicePermissions())
		}
	}

	LaunchedEffect(scanState, currentStep) {
		if (currentStep != SearchFlowStep.SCANNING) return@LaunchedEffect
		when (scanState) {
			is UiState.Success, is UiState.Error -> {
				currentStep = SearchFlowStep.RESULTS
			}
			else -> Unit
		}
	}

	BackHandler {
		if (currentStep == SearchFlowStep.INITIAL) {
			onClose()
		} else {
			currentStep = SearchFlowStep.INITIAL
			homeViewModel.resetScanState()
		}
	}

	SearchDeviceContent(
		step = currentStep,
		wifiSsid = wifiSsid,
		devices = discoveryState,
		permissionMessage = permissionMessage,
		showOpenSettings = showOpenSettings,
		onClose = onClose,
		onAllowAndContinue = { requestPermissionsOrScan() },
		onAddManually = onAddManually,
		onStopScanning = {
			currentStep = SearchFlowStep.INITIAL
			homeViewModel.resetScanState()
		},
		onScanAgain = { requestPermissionsOrScan() },
		onContinue = {
			val selected = discoveryState.firstOrNull {
				it.connectionStatus == ConnectionStatus.CONNECTED
			} ?: discoveryState.firstOrNull()
			if (selected != null) {
				onDeviceSelected(selected.device.name, selected.device.ip)
			}
			onClose()
		},
		onOpenSettings = { context.openAppPermissionSettings() },
		onRetry = { homeViewModel.retryConnection(it) }
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchDeviceContent(
	step: SearchFlowStep,
	wifiSsid: String,
	devices: List<DeviceDiscoveryStatus>,
	permissionMessage: String?,
	showOpenSettings: Boolean,
	onClose: () -> Unit,
	onAllowAndContinue: () -> Unit,
	onAddManually: () -> Unit,
	onStopScanning: () -> Unit,
	onScanAgain: () -> Unit,
	onContinue: () -> Unit,
	onOpenSettings: () -> Unit,
	onRetry: (String) -> Unit
) {
	val title = when (step) {
		SearchFlowStep.INITIAL -> "Search Devices"
		SearchFlowStep.SCANNING -> "Searching..."
		SearchFlowStep.RESULTS -> "Found ${devices.size} Devices"
	}

	val description = when (step) {
		SearchFlowStep.INITIAL -> "Allow us to check nearby devices with Bluetooth or WI-FI"
		SearchFlowStep.SCANNING -> "Please make sure to all our devices connected to the WI-FI or Bluetooth"
		SearchFlowStep.RESULTS -> "If you are unable to see your device. Please scan again"
	}

	val buttonShape = RoundedCornerShape(12.dp)
	val view = LocalView.current

	DisposableEffect(view) {
		if (view.isInEditMode) {
			return@DisposableEffect onDispose { }
		}
		val window = (view.context as? Activity)?.window
		val controller = window?.let { WindowCompat.getInsetsController(it, view) }
		val wasLight = controller?.isAppearanceLightStatusBars
		controller?.isAppearanceLightStatusBars = false
		onDispose {
			wasLight?.let { controller.isAppearanceLightStatusBars = it }
		}
	}

	Box(modifier = Modifier.fillMaxSize()) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						modifier = Modifier.fillMaxWidth()
					) {
						Text(
							text = "Welcome Back",
							fontSize = 12.sp,
							fontFamily = AppFont.onestRegular,
							color = AppPalette.gray
						)
						Text(
							text = wifiSsid.ifEmpty { "WiFi Connected" },
							fontSize = 16.sp,
							fontFamily = AppFont.onestBold,
							color = AppPalette.black
						)
					}
				},
				navigationIcon = {
					IconButton(onClick = onClose) {
						Icon(
							painter = painterResource(R.drawable.outline_arrow_back_24),
							contentDescription = "Back",
							tint = AppPalette.black
						)
					}
				},
				actions = {
					Spacer(modifier = Modifier.width(48.dp))
				},
				colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
			)
		},
		containerColor = AppPalette.white
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(horizontal = 24.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(modifier = Modifier.height(20.dp))

			RadarGraphic(
				devices = devices,
				isScanning = step == SearchFlowStep.SCANNING
			)

			Spacer(modifier = Modifier.height(40.dp))

			Text(
				text = title,
				fontFamily = AppFont.onestBold,
				fontSize = 24.sp,
				color = AppPalette.black
			)

			Spacer(modifier = Modifier.height(8.dp))

			Text(
				text = description,
				fontFamily = AppFont.onestRegular,
				fontSize = 14.sp,
				color = AppPalette.gray,
				textAlign = TextAlign.Center
			)

			if (!permissionMessage.isNullOrEmpty() && step == SearchFlowStep.INITIAL) {
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					text = permissionMessage,
					fontFamily = AppFont.onestRegular,
					fontSize = 12.sp,
					color = AppPalette.red,
					textAlign = TextAlign.Center
				)
				if (showOpenSettings) {
					TextButton(onClick = onOpenSettings) {
						Text(
							text = "Open settings",
							fontFamily = AppFont.onestSemiBold,
							fontSize = 12.sp,
							color = AppPalette.secondary
						)
					}
				}
			}

			Spacer(modifier = Modifier.height(24.dp))

			Box(modifier = Modifier.weight(1f)) {
				this@Column.AnimatedVisibility(
					visible = step != SearchFlowStep.INITIAL,
					enter = fadeIn() + expandVertically(),
					exit = fadeOut() + shrinkVertically()
				) {
					DiscoveryList(
						devices = devices,
						onRetry = onRetry
					)
				}
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 24.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				when (step) {
					SearchFlowStep.INITIAL -> {
						Button(
							onClick = onAllowAndContinue,
							modifier = Modifier
								.fillMaxWidth()
								.height(56.dp),
							colors = ButtonDefaults.buttonColors(containerColor = AppPalette.secondary),
							shape = buttonShape
						) {
							Text(
								text = "Allow and Continue",
								color = AppPalette.white,
								fontFamily = AppFont.onestMedium,
								fontSize = 14.sp
							)
						}

						OutlinedButton(
							onClick = onAddManually,
							modifier = Modifier
								.fillMaxWidth()
								.height(56.dp),
							shape = buttonShape,
							border = BorderStroke(1.dp, AppPalette.borderGray),
							colors = ButtonDefaults.outlinedButtonColors(contentColor = AppPalette.secondary)
						) {
							Text(
								text = "Add Manually",
								color = AppPalette.secondary,
								fontFamily = AppFont.onestMedium,
								fontSize = 14.sp
							)
						}
					}

					SearchFlowStep.SCANNING -> {
						OutlinedButton(
							onClick = onStopScanning,
							modifier = Modifier
								.fillMaxWidth()
								.height(56.dp),
							shape = buttonShape,
							border = BorderStroke(1.dp, AppPalette.borderGray),
							colors = ButtonDefaults.outlinedButtonColors(contentColor = AppPalette.black)
						) {
							Text(
								text = "Stop Scanning",
								color = AppPalette.black,
								fontFamily = AppFont.onestMedium,
								fontSize = 14.sp
							)
						}
					}

					SearchFlowStep.RESULTS -> {
						Button(
							onClick = onContinue,
							modifier = Modifier
								.fillMaxWidth()
								.height(56.dp),
							colors = ButtonDefaults.buttonColors(containerColor = AppPalette.secondary),
							shape = buttonShape
						) {
							Text(
								text = "Continue",
								color = AppPalette.white,
								fontFamily = AppFont.onestMedium,
								fontSize = 14.sp
							)
						}

						OutlinedButton(
							onClick = onScanAgain,
							modifier = Modifier
								.fillMaxWidth()
								.height(56.dp),
							shape = buttonShape,
							border = BorderStroke(1.dp, AppPalette.secondary),
							colors = ButtonDefaults.outlinedButtonColors(contentColor = AppPalette.secondary)
						) {
							Text(
								text = "Scan again",
								color = AppPalette.secondary,
								fontFamily = AppFont.onestMedium,
								fontSize = 14.sp
							)
						}
					}
				}

				if (step == SearchFlowStep.INITIAL) {
					Text(
						text = "Make sure you have one or more smart devices that support WI-FI or Bluetooth",
						fontFamily = AppFont.onestRegular,
						fontSize = 12.sp,
						color = AppPalette.gray,
						textAlign = TextAlign.Center,
						modifier = Modifier.padding(horizontal = 20.dp)
					)
				}
			}
		}
	}

		Box(
			modifier = Modifier
				.align(Alignment.TopCenter)
				.fillMaxWidth()
				.windowInsetsTopHeight(WindowInsets.statusBars)
				.background(AppPalette.secondary)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun SearchDeviceScreenPreview() {
	AppPreview(padding = 0.dp, color = AppPalette.white) {
		SearchDeviceScreen(onClose = {})
	}
}

@Preview(showBackground = true)
@Composable
fun SearchDeviceResultsPreview() {
	AppPreview(padding = 0.dp, color = AppPalette.white) {
		SearchDeviceContent(
			step = SearchFlowStep.RESULTS,
			wifiSsid = "Home_WiFi",
			devices = listOf(
				DeviceDiscoveryStatus(
					DiscoveredDevice("Living Room Light", "192.168.1.12"),
					ConnectionStatus.CONNECTED
				),
				DeviceDiscoveryStatus(
					DiscoveredDevice("Bedroom Fan", "192.168.1.18"),
					ConnectionStatus.FAILED
				)
			),
			permissionMessage = null,
			showOpenSettings = false,
			onClose = {},
			onAllowAndContinue = {},
			onAddManually = {},
			onStopScanning = {},
			onScanAgain = {},
			onContinue = {},
			onOpenSettings = {},
			onRetry = {}
		)
	}
}
