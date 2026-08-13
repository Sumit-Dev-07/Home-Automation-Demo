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
    override suspend fun ledOn(): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.ledOn() }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun ledOff(): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.ledOff() }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun led2On(): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.led2On() }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun led2Off(): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.led2Off() }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun getStatus(): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.getStatus() }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }
}
