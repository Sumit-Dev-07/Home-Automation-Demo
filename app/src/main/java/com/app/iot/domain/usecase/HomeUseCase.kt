package com.app.iot.domain.usecase

import com.app.iot.domain.repository.HomeRepository
import com.app.iot.util.ApiState
import com.app.iot.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import javax.inject.Inject

class HomeUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    suspend fun toggleRelay(name: String, isOn: Boolean): Flow<UiState<ResponseBody>> {
        return homeRepository.toggleRelay(name, isOn).map { result ->
            when (result) {
                is ApiState.Loading -> UiState.Loading
                is ApiState.Success -> UiState.Success(result.data)
                is ApiState.Error -> UiState.Error(result.message)
            }
        }
    }

    suspend fun getStatus(): Flow<UiState<ResponseBody>> {
        return homeRepository.getStatus().map { result ->
            when (result) {
                is ApiState.Loading -> UiState.Loading
                is ApiState.Success -> UiState.Success(result.data)
                is ApiState.Error -> UiState.Error(result.message)
            }
        }
    }

    suspend fun addDevice(name: String, pin: String, syncPin: String?): Flow<UiState<ResponseBody>> {
        return homeRepository.addDevice(name, pin, syncPin).map { result ->
            when (result) {
                is ApiState.Loading -> UiState.Loading
                is ApiState.Success -> UiState.Success(result.data)
                is ApiState.Error -> UiState.Error(result.message)
            }
        }
    }

    suspend fun removeDevice(name: String): Flow<UiState<ResponseBody>> {
        return homeRepository.removeDevice(name).map { result ->
            when (result) {
                is ApiState.Loading -> UiState.Loading
                is ApiState.Success -> UiState.Success(result.data)
                is ApiState.Error -> UiState.Error(result.message)
            }
        }
    }

    suspend fun updateWifi(password: String): Flow<UiState<ResponseBody>> {
        return homeRepository.updateWifi(password).map { result ->
            when (result) {
                is ApiState.Loading -> UiState.Loading
                is ApiState.Success -> UiState.Success(result.data)
                is ApiState.Error -> UiState.Error(result.message)
            }
        }
    }
}
