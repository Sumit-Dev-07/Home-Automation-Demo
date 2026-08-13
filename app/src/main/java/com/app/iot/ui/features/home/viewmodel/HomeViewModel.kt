package com.app.iot.ui.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.iot.domain.usecase.HomeUseCase
import com.app.iot.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
}
