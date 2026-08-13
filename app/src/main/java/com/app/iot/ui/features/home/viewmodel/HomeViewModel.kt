package com.app.iot.ui.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.iot.domain.usecase.HomeUseCase
import com.app.iot.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _ledState = MutableStateFlow<UiState<ResponseBody>>(UiState.Idle)
    val ledState: StateFlow<UiState<ResponseBody>> = _ledState.asStateFlow()

    private val _statusState = MutableStateFlow<UiState<ResponseBody>>(UiState.Idle)
    val statusState: StateFlow<UiState<ResponseBody>> = _statusState.asStateFlow()

    private val _scanState = MutableStateFlow<UiState<List<String>>>(UiState.Idle)
    val scanState: StateFlow<UiState<List<String>>> = _scanState.asStateFlow()

    private val UDP_PORT = 4210
    private val DISCOVERY_MESSAGE = "DISCOVER_ESP"

    fun controlLed(ipAddress: String, turnOn: Boolean) {
        viewModelScope.launch {
            val call = if (turnOn) homeUseCase.ledOn(ipAddress) else homeUseCase.ledOff(ipAddress)
            call.onStart { _ledState.value = UiState.Loading }
                .catch { error -> _ledState.value = UiState.Error("${error.localizedMessage}") }
                .collect { result ->
                    _ledState.value = result
                }
        }
    }

    fun fetchStatus(ipAddress: String) {
        viewModelScope.launch {
            homeUseCase.getStatus(ipAddress)
                .onStart { _statusState.value = UiState.Loading }
                .catch { error -> _statusState.value = UiState.Error("${error.localizedMessage}") }
                .collect { result ->
                    _statusState.value = result
                }
        }
    }

    fun findEspDevices(systemIp: String) {
        if (systemIp == "0.0.0.0" || systemIp.isEmpty()) {
            _scanState.value = UiState.Error("Please connect to WiFi first")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _scanState.value = UiState.Loading
            val discovered = mutableSetOf<String>()
            var socket: DatagramSocket? = null

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
                        // The response is JSON, we just need the IP from the packet
                        val ip = receivePacket.address.hostAddress
                        if (ip != null) {
                            discovered.add(ip)
                        }
                    } catch (e: Exception) {
                        // Timeout reached or receive failed
                        break
                    }
                }

                if (discovered.isEmpty()) {
                    _scanState.value = UiState.Error("No devices found. Check if ESP8266 is on same WiFi.")
                } else {
                    _scanState.value = UiState.Success(discovered.toList())
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
    }
}
