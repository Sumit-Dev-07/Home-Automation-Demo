package com.app.iot.data.datasource

import com.app.iot.data.ApiService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(private val apiService: ApiService) : HomeDataSource {
    override suspend fun ledOn(ipAddress: String): Response<ResponseBody> {
        return apiService.controlLed("http://$ipAddress/ledon")
    }

    override suspend fun ledOff(ipAddress: String): Response<ResponseBody> {
        return apiService.controlLed("http://$ipAddress/ledoff")
    }
}
