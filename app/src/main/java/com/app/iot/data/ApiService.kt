package com.app.iot.data

import com.app.compose.data.remote.model.login.LoginRequest
import com.app.compose.data.remote.model.login.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST(ApiPath.LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET
    suspend fun getStatus(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun toggleRelay(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun addDevice(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun removeDevice(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun updateWifi(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun controlLed(@Url url: String): Response<ResponseBody>

}
