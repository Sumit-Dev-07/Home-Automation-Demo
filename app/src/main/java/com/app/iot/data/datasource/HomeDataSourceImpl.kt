package com.app.iot.data.datasource

import com.app.iot.data.ApiPath
import com.app.iot.data.ApiService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(private val apiService: ApiService) : HomeDataSource {
    override suspend fun toggleRelay(name: String, isOn: Boolean): Response<ResponseBody> {
        val action = if (isOn) "on" else "off"
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/relay/$action?relay=$name")
    }

    override suspend fun getStatus(): Response<ResponseBody> {
        return apiService.controlLed("http://${ApiPath.LOCAL_WIFI_IP_URL}/status")
    }
}
