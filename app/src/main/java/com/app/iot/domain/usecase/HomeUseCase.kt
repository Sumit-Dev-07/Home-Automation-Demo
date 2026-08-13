package com.app.iot.domain.usecase

import com.app.iot.domain.repository.HomeRepository
import com.app.iot.util.ApiState
import com.app.iot.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import javax.inject.Inject

class HomeUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    suspend fun ledOn(): Flow<UiState<ResponseBody>> {
        return homeRepository.ledOn().map { result ->
            when (result) {
                is ApiState.Loading -> UiState.Loading
                is ApiState.Success -> UiState.Success(result.data)
                is ApiState.Error -> UiState.Error(result.message)
            }
        }
    }

    suspend fun ledOff(): Flow<UiState<ResponseBody>> {
        return homeRepository.ledOff().map { result ->
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
}
