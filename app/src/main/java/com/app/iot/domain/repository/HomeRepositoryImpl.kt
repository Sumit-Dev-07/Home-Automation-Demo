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
    override suspend fun ledOn(ipAddress: String): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.ledOn(ipAddress) }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }

    override suspend fun ledOff(ipAddress: String): Flow<ApiState<ResponseBody>> {
        return flow {
            val result = safeApiCall { homeDataSource.ledOff(ipAddress) }
            emit(result)
        }.flowOn(dispatchersProvider.io())
    }
}
