package com.app.iot.data.datasource

import com.app.compose.data.remote.model.login.LoginRequest
import com.app.compose.data.remote.model.login.LoginResponse
import retrofit2.Response

interface AuthDataSource {
    suspend fun login(loginReq: LoginRequest): Response<LoginResponse>
}