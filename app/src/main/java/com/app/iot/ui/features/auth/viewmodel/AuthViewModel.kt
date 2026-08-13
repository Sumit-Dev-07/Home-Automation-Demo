package com.app.iot.ui.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.compose.data.remote.model.login.LoginRequest
import com.app.compose.data.remote.model.login.LoginResponse
import com.app.iot.domain.usecase.AuthUseCase
import com.app.iot.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState.asStateFlow()

    fun validateUsername(username: String): String? = when {
        username.isBlank() -> "Username cannot be empty"
        else -> null
    }

    fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password cannot be empty"
        else -> null
    }

    fun login(loginReq: LoginRequest) {
        viewModelScope.launch {
            authUseCase.login(loginReq)
                .onStart { _loginState.value = UiState.Loading }
                .catch { error -> _loginState.value = UiState.Error("${error.localizedMessage}") }
                .collect { result ->
                    _loginState.value = result
                }
        }
    }
}