package com.app.iot.data.datasource

import okhttp3.ResponseBody
import retrofit2.Response

interface HomeDataSource {
    suspend fun ledOn(ipAddress: String): Response<ResponseBody>
    suspend fun ledOff(ipAddress: String): Response<ResponseBody>
    suspend fun getStatus(ipAddress: String): Response<ResponseBody>
}
