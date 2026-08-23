package com.app.iot.domain.repository

import com.app.iot.data.datasource.HomeDataSource
import com.app.iot.util.ApiState
import com.app.iot.util.DispatchersProvider
import com.app.iot.util.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource,
    private val dispatchersProvider: DispatchersProvider
) : HomeRepository {
    override suspend fun toggleRelay(name: String, isOn: Boolean): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.toggleRelay(name, isOn) }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun getStatus(): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.getStatus() }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun addDevice(
        name: String,
        pin: String,
        syncPin: String?
    ): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.addDevice(name, pin, syncPin) }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun removeDevice(name: String): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.removeDevice(name) }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun updateWifi(password: String): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.updateWifi(password) }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }
}
