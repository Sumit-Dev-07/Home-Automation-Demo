package com.app.iot.ui.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.iot.domain.usecase.HomeUseCase
import com.app.iot.util.UiState
import com.app.iot.util.WifiConnectivityManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

data class DeviceStatus(
    val id: Int = 1,
    val name: String,
    val isOn: Boolean,
    val isConnected: Boolean,
    val ipAddress: String,
    val iconType: String = "light"
)

data class DiscoveredDevice(
    val name: String,
    val ip: String,
    val type: String = "light"
)

enum class ConnectionStatus { IDLE, CONNECTING, CONNECTED, FAILED }

data class DeviceDiscoveryStatus(
    val device: DiscoveredDevice,
    val connectionStatus: ConnectionStatus = ConnectionStatus.IDLE
)

private data class DiscoveryResponse(
    val device: String? = null,
    val ip: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    private val wifiConnectivityManager: WifiConnectivityManager
) : ViewModel() {

    private val _ledState = MutableStateFlow<UiState<ResponseBody>>(UiState.Idle)
    val ledState: StateFlow<UiState<ResponseBody>> = _ledState.asStateFlow()

    private val _statusState = MutableStateFlow<UiState<ResponseBody>>(UiState.Idle)
    val statusState: StateFlow<UiState<ResponseBody>> = _statusState.asStateFlow()

    private val _scanState = MutableStateFlow<UiState<List<DiscoveredDevice>>>(UiState.Idle)
    val scanState: StateFlow<UiState<List<DiscoveredDevice>>> = _scanState.asStateFlow()

    private val _discoveryState = MutableStateFlow<List<DeviceDiscoveryStatus>>(emptyList())
    val discoveryState: StateFlow<List<DeviceDiscoveryStatus>> = _discoveryState.asStateFlow()

    private val _addDeviceState = MutableStateFlow<UiState<ResponseBody>>(UiState.Idle)
    val addDeviceState: StateFlow<UiState<ResponseBody>> = _addDeviceState.asStateFlow()

    private val _removeDeviceState = MutableStateFlow<UiState<ResponseBody>>(UiState.Idle)
    val removeDeviceState: StateFlow<UiState<ResponseBody>> = _removeDeviceState.asStateFlow()

    private val _updateWifiState = MutableStateFlow<UiState<ResponseBody>>(UiState.Idle)
    val updateWifiState: StateFlow<UiState<ResponseBody>> = _updateWifiState.asStateFlow()

    private val _devices = MutableStateFlow<List<DeviceStatus>>(emptyList())
    val devices: StateFlow<List<DeviceStatus>> = _devices.asStateFlow()

    private val UDP_PORT = 4210
    private val DISCOVERY_MESSAGE = "DISCOVER_ESP"

    fun controlLed(id: Int, turnOn: Boolean) {
        val device = _devices.value.find { it.id == id } ?: return
        viewModelScope.launch {
            homeUseCase.toggleRelay(device.name, turnOn)
                .onStart { _ledState.value = UiState.Loading }
                .catch { error -> _ledState.value = UiState.Error("${error.localizedMessage}") }
                .collect { result ->
                    _ledState.value = result
                    if (result is UiState.Success) {
                        fetchStatus() // Refresh status after control
                    }
                }
        }
    }

    fun fetchStatus() {
        viewModelScope.launch {
            homeUseCase.getStatus()
                .onStart { _statusState.value = UiState.Loading }
                .catch { error -> 
                    _statusState.value = UiState.Error("${error.localizedMessage}")
                    // On error, mark all devices as disconnected
                    _devices.value = _devices.value.map { it.copy(isConnected = false) }
                }
                .collect { result ->
                    _statusState.value = result
                    if (result is UiState.Success) {
                        parseStatusResponse(result.data.string())
                    }
                }
        }
    }

    fun addDevice(name: String, pin: String, syncPin: String?) {
        viewModelScope.launch {
            homeUseCase.addDevice(name, pin, syncPin)
                .onStart { _addDeviceState.value = UiState.Loading }
                .catch { error -> _addDeviceState.value = UiState.Error("${error.localizedMessage}") }
                .collect { result ->
                    _addDeviceState.value = result
                    if (result is UiState.Success) {
                        fetchStatus()
                    }
                }
        }
    }

    fun removeDevice(name: String) {
        viewModelScope.launch {
            homeUseCase.removeDevice(name)
                .onStart { _removeDeviceState.value = UiState.Loading }
                .catch { error -> _removeDeviceState.value = UiState.Error("${error.localizedMessage}") }
                .collect { result ->
                    _removeDeviceState.value = result
                    if (result is UiState.Success) {
                        fetchStatus()
                    }
                }
        }
    }

    fun updateWifi(password: String) {
        viewModelScope.launch {
            homeUseCase.updateWifi(password)
                .onStart { _updateWifiState.value = UiState.Loading }
                .catch { error -> _updateWifiState.value = UiState.Error("${error.localizedMessage}") }
                .collect { result ->
                    _updateWifiState.value = result
                }
        }
    }

    fun resetActionStates() {
        _addDeviceState.value = UiState.Idle
        _removeDeviceState.value = UiState.Idle
        _updateWifiState.value = UiState.Idle
    }

    private fun parseStatusResponse(jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            val ip = json.optString("ip", "")

            val newList = mutableListOf<DeviceStatus>()

            // Parse relays array from ESP firmware
            val hasRelays = json.has("relays")
            val relaysArray = json.optJSONArray("relays")
            if (relaysArray != null) {
                for (i in 0 until relaysArray.length()) {
                    val relayJson = relaysArray.getJSONObject(i)
                    newList.add(
                        DeviceStatus(
                            id = i + 1,
                            name = relayJson.optString("name", "Relay ${i + 1}"),
                            isOn = relayJson.optString("status", "OFF") == "ON",
                            isConnected = true,
                            ipAddress = ip,
                            iconType = "light"
                        )
                    )
                }
            }

            if (newList.isEmpty() && !hasRelays) {
                // Fallback for single relay devices (older firmware without relays array)
                val deviceName = json.optString("device", "ESP Device")
                val status = json.optString("status", "OFF")
                newList.add(
                    DeviceStatus(
                        id = 1,
                        name = deviceName,
                        isOn = status == "ON",
                        isConnected = true,
                        ipAddress = ip,
                        iconType = "light"
                    )
                )
            }

            _devices.value = newList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun findEspDevices(systemIp: String) {
        if (systemIp == "0.0.0.0" || systemIp.isEmpty()) {
            _scanState.value = UiState.Error("Please connect to WiFi first")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _scanState.value = UiState.Loading
            val discovered = mutableMapOf<String, DiscoveredDevice>()
            var socket: DatagramSocket? = null
            val gson = Gson()

            try {
                socket = DatagramSocket()
                socket.broadcast = true
                socket.soTimeout = 3000 // Wait 3 seconds for responses

                val sendData = DISCOVERY_MESSAGE.toByteArray()
                val broadcastAddress = InetAddress.getByName("255.255.255.255")
                val sendPacket = DatagramPacket(sendData, sendData.size, broadcastAddress, UDP_PORT)
                
                socket.send(sendPacket)

                val receiveData = ByteArray(1024)
                val receivePacket = DatagramPacket(receiveData, receiveData.size)

                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < 3000) {
                    try {
                        socket.receive(receivePacket)
                        val responseJson = String(receivePacket.data, 0, receivePacket.length)
                        val ip = receivePacket.address.hostAddress ?: ""
                        
                        try {
                            val parsed = gson.fromJson(responseJson, DiscoveryResponse::class.java)
                            val name = parsed.device ?: "Unknown Device"
                            discovered[ip] = DiscoveredDevice(name, ip)
                        } catch (e: Exception) {
                            discovered[ip] = DiscoveredDevice("ESP Device", ip)
                        }
                    } catch (e: Exception) {
                        break
                    }
                }

                if (discovered.isEmpty()) {
                    _scanState.value = UiState.Error("No devices found. Check if ESP8266 is on same WiFi.")
                } else {
                    val deviceList = discovered.values.toList()
                    _scanState.value = UiState.Success(deviceList)
                    
                    // Initialize discovery state with CONNECTING for each found device
                    _discoveryState.value = deviceList.map { 
                        DeviceDiscoveryStatus(it, ConnectionStatus.CONNECTING) 
                    }
                    
                    // Simulate individual connections
                    deviceList.forEachIndexed { index, device ->
                        launch {
                            delay((1000..3000).random().toLong())
                            val isSuccess = (0..10).random() > 2 // 80% success rate
                            updateDeviceConnectionStatus(
                                device.ip, 
                                if (isSuccess) ConnectionStatus.CONNECTED else ConnectionStatus.FAILED
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _scanState.value = UiState.Error("Discovery failed: ${e.localizedMessage}")
            } finally {
                socket?.close()
            }
        }
    }

    fun resetScanState() {
        _scanState.value = UiState.Idle
        _discoveryState.value = emptyList()
    }

    private fun updateDeviceConnectionStatus(ip: String, status: ConnectionStatus) {
        _discoveryState.value = _discoveryState.value.map {
            if (it.device.ip == ip) it.copy(connectionStatus = status) else it
        }
    }

    fun retryConnection(ip: String) {
        val deviceStatus = _discoveryState.value.find { it.device.ip == ip } ?: return
        updateDeviceConnectionStatus(ip, ConnectionStatus.CONNECTING)
        viewModelScope.launch {
            delay(2000)
            updateDeviceConnectionStatus(ip, ConnectionStatus.CONNECTED)
        }
    }

    fun clearDevices() {
        _devices.value = emptyList()
        _statusState.value = UiState.Idle
    }

    fun isWifiConnected(): Boolean = wifiConnectivityManager.isWifiConnected()

    fun getWifiIpAddress(): String = wifiConnectivityManager.getWifiIpAddress()

    fun getWifiSsid(): String = wifiConnectivityManager.getWifiSsid()
}
