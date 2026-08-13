package com.app.iot.domain.repository

import com.app.iot.util.ApiState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface HomeRepository {
    suspend fun ledOn(): Flow<ApiState<ResponseBody>>
    suspend fun ledOff(): Flow<ApiState<ResponseBody>>
    suspend fun getStatus(): Flow<ApiState<ResponseBody>>
}
