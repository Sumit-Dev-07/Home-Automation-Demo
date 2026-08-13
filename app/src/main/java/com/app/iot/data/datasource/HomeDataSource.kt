package com.app.iot.data.datasource

import okhttp3.ResponseBody
import retrofit2.Response

interface HomeDataSource {
    suspend fun ledOn(): Response<ResponseBody>
    suspend fun ledOff(): Response<ResponseBody>
    suspend fun led2On(): Response<ResponseBody>
    suspend fun led2Off(): Response<ResponseBody>
    suspend fun getStatus(): Response<ResponseBody>
}
