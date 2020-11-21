package com.project.simoneconigliaro.jetpackarchitecture.repository

import android.util.Log
import com.simoneconigliaro.pictureengine.repository.safeApiCall
import com.simoneconigliaro.pictureengine.repository.safeCacheCall
import com.simoneconigliaro.pictureengine.utils.*
import com.simoneconigliaro.pictureengine.utils.Constants.NETWORK_ERROR
import com.simoneconigliaro.pictureengine.utils.Constants.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>
constructor(
    private val dispatcher: CoroutineDispatcher,
    private val stateEvent: StateEvent,
    private val apiCall: suspend () -> NetworkObj?,
    private val cacheCall: suspend () -> CacheObj?
) {

    private val TAG = "NetworkBoundResource"

    val result: Flow<DataState<ViewState>> = flow {

        val cacheResult = safeCacheCall(dispatcher) { cacheCall.invoke() }

        when (cacheResult) {
            
            is CacheResult.GenericError -> {
                emit(
                    DataState.error(
                        errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + cacheResult.errorMessage,
                        stateEvent = stateEvent
                    )
                )
            }
            is CacheResult.Success -> {
                if (cacheResult.value != null) {
                    if (shouldReturnCache(cacheResult.value)) {
                        // cache contains valid data
                        Log.d("AppDebug", "NetworkBoundResource: return cache with valid data")
                        // set state event to null, we want to continue showing the progress bar until the cache is updated and showed to the user
                        // this will only set the current cache, still valid (not expired) but not updated, to be shown to the UI
                        Log.d(TAG, "CACHE: ${cacheResult.value} ")
                        emit(returnSuccessCache(cacheResult.value, null))
                    } 
                } 
            }
        }

        // get data from api and update cache
        val apiResult = safeApiCall(dispatcher) { apiCall.invoke() }

        when (apiResult) {
            is ApiResult.GenericError -> {
                emit(
                    DataState.error(
                        errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + apiResult.errorMessage,
                        stateEvent = stateEvent
                    )
                )
            }
            is ApiResult.NetworkError -> {
                emit(
                    DataState.error(
                        errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + NETWORK_ERROR,
                        stateEvent = stateEvent
                    )
                )
            }
            is ApiResult.Success -> {
                if (apiResult.value == null) {
                    emit(
                        DataState.error(
                            errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + UNKNOWN_ERROR,
                            stateEvent = stateEvent
                        )
                    )
                } else {
                    Log.d("NetworkBoundResource", "API: ${apiResult.value}")
                    updateCache(apiResult.value)
                }
            }
        }

        // handle updated cache to show it to the UI
        emit(handleUpdatedCache())
    }

    // it determines if the current cache should be displayed to the user.
    // It depends on if the user wants to refresh the data or the cache is empty or expired
    abstract suspend fun shouldReturnCache(cacheResult: CacheObj?): Boolean

    private suspend fun handleUpdatedCache(): DataState<ViewState> {

        val cacheResult = safeCacheCall(dispatcher) { cacheCall.invoke() }

        return object : CacheResponseHandler<ViewState, CacheObj>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override fun handleSuccess(resultObj: CacheObj): DataState<ViewState> {
                return returnSuccessCache(resultObj, stateEvent)
            }
        }.result
    }

    abstract suspend fun updateCache(networkObj: NetworkObj)

    abstract fun returnSuccessCache(
        resultObj: CacheObj,
        stateEvent: StateEvent?
    ): DataState<ViewState>


}