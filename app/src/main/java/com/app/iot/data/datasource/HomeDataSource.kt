package com.app.iot.data.datasource

import okhttp3.ResponseBody
import retrofit2.Response

interface HomeDataSource {
    suspend fun toggleRelay(name: String, isOn: Boolean): Response<ResponseBody>
    suspend fun getStatus(): Response<ResponseBody>
}
