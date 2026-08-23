package com.app.iot.domain.repository

import com.app.iot.util.ApiState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface HomeRepository {
    suspend fun toggleRelay(name: String, isOn: Boolean): Flow<ApiState<ResponseBody>>
    suspend fun getStatus(): Flow<ApiState<ResponseBody>>
    suspend fun addDevice(name: String, pin: String, syncPin: String?): Flow<ApiState<ResponseBody>>
    suspend fun removeDevice(name: String): Flow<ApiState<ResponseBody>>
    suspend fun updateWifi(password: String): Flow<ApiState<ResponseBody>>
}
