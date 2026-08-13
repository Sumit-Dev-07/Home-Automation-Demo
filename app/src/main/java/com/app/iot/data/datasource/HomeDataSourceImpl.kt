package com.app.iot.data.datasource

import com.app.iot.data.ApiPath
import com.app.iot.data.ApiService
import com.app.iot.util.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(private val apiService: ApiService) : HomeDataSource {
    override suspend fun ledOn(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/ledon")
    }

    override suspend fun ledOff(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/ledoff")
    }

    override suspend fun getStatus(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/status")
    }
}
