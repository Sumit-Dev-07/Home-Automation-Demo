package com.app.iot.domain.repository

import com.app.compose.data.remote.model.login.LoginRequest
import com.app.compose.data.remote.model.login.LoginResponse
import com.app.iot.data.datasource.AuthDataSource
import com.app.iot.util.ApiState
import com.app.iot.util.DispatchersProvider
import com.app.iot.util.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val dispatchersProvider: DispatchersProvider
) : AuthRepository {
    override suspend fun login(loginReq: LoginRequest): Flow<ApiState<LoginResponse>> {
        return flow {
            val result = safeApiCall { authDataSource.login(loginReq) }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }
}