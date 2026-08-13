package com.app.iot.data.datasource

import com.app.compose.data.remote.model.login.LoginRequest
import com.app.compose.data.remote.model.login.LoginResponse
import com.app.iot.data.ApiService
import retrofit2.Response
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(private var apiService: ApiService) : AuthDataSource {
    override suspend fun login(loginReq: LoginRequest): Response<LoginResponse> {
        return apiService.login(loginReq)
    }
}