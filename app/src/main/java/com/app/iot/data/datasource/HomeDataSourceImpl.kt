package com.app.iot.data.datasource

import com.app.iot.data.ApiPath
import com.app.iot.data.ApiService
import com.app.iot.util.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(private val apiService: ApiService) : HomeDataSource {
    override suspend fun ledOn(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/led1on")
    }

    override suspend fun ledOff(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/led1off")
    }

    override suspend fun led2On(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/led2on")
    }

    override suspend fun led2Off(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/led2off")
    }

    override suspend fun getStatus(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/status")
    }
}
