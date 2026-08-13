package com.app.iot.ui.features.auth.viewmodel

import com.app.compose.data.remote.model.login.LoginRequest
import com.app.compose.data.remote.model.login.LoginResponse
import com.app.iot.domain.repository.AuthRepository
import com.app.iot.util.ApiState
import com.app.iot.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun login(loginReq: LoginRequest): Flow<UiState<LoginResponse>> {
        return authRepository.login(loginReq).map { result ->
            when (result) {
                is ApiState.Loading -> UiState.Loading
                is ApiState.Success -> UiState.Success(result.data)
                is ApiState.Error -> UiState.Error(result.message)
            }
        }
    }
}