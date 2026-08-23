package com.app.iot.data.datasource

import okhttp3.ResponseBody
import retrofit2.Response

interface HomeDataSource {
    suspend fun toggleRelay(name: String, isOn: Boolean): Response<ResponseBody>
    suspend fun getStatus(): Response<ResponseBody>
    suspend fun addDevice(name: String, pin: String, syncPin: String?): Response<ResponseBody>
    suspend fun removeDevice(name: String): Response<ResponseBody>
    suspend fun updateWifi(password: String): Response<ResponseBody>
}
