package com.app.iot.ui.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.iot.domain.usecase.HomeUseCase
import com.app.iot.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okhttp3.ResponseBody
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
        
        val subnet = systemIp.substringBeforeLast(".") + "."
        
        viewModelScope.launch(Dispatchers.IO) {
            _scanState.value = UiState.Loading
            
            // Limit parallelism to avoid overwhelming the network stack
            val semaphore = Semaphore(30)
            
            val jobs = (1..254).map { i ->
                async {
                    semaphore.withPermit {
                        val targetIp = subnet + i
                        try {
                            // Use a generous timeout for discovery as ESP8266 can be slow
                            withTimeout(5000) {
                                val result = homeUseCase.getStatus(targetIp)
                                    .filter { it !is UiState.Loading }
                                    .first()
                                
                                if (result is UiState.Success) {
                                    targetIp
                                } else null
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }
            
            val discovered = jobs.awaitAll().filterNotNull()
            
            if (discovered.isEmpty()) {
                _scanState.value = UiState.Error("No devices found on $subnet*. Check if ESP8266 is on same WiFi.")
            } else {
                _scanState.value = UiState.Success(discovered)
            }
        }
    }

    fun resetScanState() {
        _scanState.value = UiState.Idle
    }
}
