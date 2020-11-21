package com.simoneconigliaro.pictureengine.repository

import com.simoneconigliaro.pictureengine.utils.ApiResult
import com.simoneconigliaro.pictureengine.utils.CacheResult
import com.simoneconigliaro.pictureengine.utils.Constants.CACHE_ERROR_TIMEOUT
import com.simoneconigliaro.pictureengine.utils.Constants.CACHE_TIMEOUT
import com.simoneconigliaro.pictureengine.utils.Constants.NETWORK_ERROR_TIMEOUT
import com.simoneconigliaro.pictureengine.utils.Constants.NETWORK_TIMEOUT
import com.simoneconigliaro.pictureengine.utils.Constants.UNKNOWN_ERROR
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher, // determines what thread or threads the corresponding coroutine uses for its execution
    apiCall: suspend () -> T? // apiCall that returns a ApiResult
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(NETWORK_TIMEOUT) {
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    ApiResult.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ApiResult.GenericError(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    ApiResult.GenericError(
                        null,
                        UNKNOWN_ERROR
                    )
                }
            }
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT) {
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    CacheResult.GenericError(
                        UNKNOWN_ERROR
                    )
                }
            }
        }
    }
}


private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.toString()
    } catch (exception: Exception) {
        UNKNOWN_ERROR
    }
}