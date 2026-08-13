package com.app.iot.data

import com.app.compose.data.remote.model.login.LoginRequest
import com.app.compose.data.remote.model.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST(ApiPath.LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

}