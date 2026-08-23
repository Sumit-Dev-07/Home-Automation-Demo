package com.app.iot.data.datasource

import com.app.iot.data.ApiPath
import com.app.iot.data.ApiService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(private val apiService: ApiService) : HomeDataSource {
    override suspend fun toggleRelay(name: String, isOn: Boolean): Response<ResponseBody> {
        val status = if (isOn) "ON" else "OFF"
        return apiService.toggleRelay("http://${ApiPath.LOCAL_WIFI_IP_URL}/relay/toggle?relay=$name&status=$status")
    }

    override suspend fun getStatus(): Response<ResponseBody> {
        return apiService.getStatus("http://${ApiPath.LOCAL_WIFI_IP_URL}/status")
    }

    override suspend fun addDevice(name: String, pin: String, syncPin: String?): Response<ResponseBody> {
        val url = "http://${ApiPath.LOCAL_WIFI_IP_URL}/device/add?name=$name&pin=$pin" +
                if (syncPin != null) "&syncPin=$syncPin" else ""
        return apiService.addDevice(url)
    }

    override suspend fun removeDevice(name: String): Response<ResponseBody> {
        return apiService.removeDevice("http://${ApiPath.LOCAL_WIFI_IP_URL}/device/remove?name=$name")
    }

    override suspend fun updateWifi(password: String): Response<ResponseBody> {
        return apiService.updateWifi("http://${ApiPath.LOCAL_WIFI_IP_URL}/wifi/update?password=$password")
    }
}
