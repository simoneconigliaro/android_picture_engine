package com.simoneconigliaro.pictureengine.repository

import com.simoneconigliaro.pictureengine.utils.ApiResult
import com.simoneconigliaro.pictureengine.utils.CacheResponseHandler
import com.simoneconigliaro.pictureengine.utils.Constants.NETWORK_ERROR
import com.simoneconigliaro.pictureengine.utils.Constants.UNKNOWN_ERROR
import com.simoneconigliaro.pictureengine.utils.DataState
import com.simoneconigliaro.pictureengine.utils.StateEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@FlowPreview
abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>
constructor(
    private val dispatcher: CoroutineDispatcher, // responsible for making api and cache calls. so if it gets cancelled it cancels everything
    private val stateEvent: StateEvent, // follow the request from the beginning till the result is showed in the ui
    private val apiCall: suspend () -> NetworkObj?,
    private val cacheCall: suspend () -> CacheObj?
) {

    private val TAG: String = "AppDebug"

    // to create a flow ->  flow { and then use emit() to return the data
    val result: Flow<DataState<ViewState>> = flow {

        // ****** STEP 1: VIEW CACHE ******
        emit(returnCache(markJobComplete = false))

        // ****** STEP 2: MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
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
                }
                // if succeed, updates the cache
                else {
                    updateCache(apiResult.value as NetworkObj)
                }
            }
        }

        // ****** STEP 3: VIEW CACHE and MARK JOB COMPLETED ******
        emit(returnCache(markJobComplete = true))
    }

    private suspend fun returnCache(markJobComplete: Boolean): DataState<ViewState> {

        // makes a cached request and handles the errors
        val cacheResult = safeCacheCall(dispatcher) { cacheCall.invoke() }

        var jobCompleteMarker: StateEvent? = null
        if (markJobComplete) {
            jobCompleteMarker = stateEvent
        }

        // CacheRespondHandler gets the cached object and wrap it in a DataState object
        return object : CacheResponseHandler<ViewState, CacheObj>(
            response = cacheResult,
            stateEvent = jobCompleteMarker
        ) {
            override fun handleSuccess(resultObj: CacheObj): DataState<ViewState> {
                return handleCacheSuccess(resultObj)
            }

        }.result

    }

    abstract suspend fun updateCache(networkObject: NetworkObj)

    abstract fun handleCacheSuccess(resultObj: CacheObj): DataState<ViewState> // make sure to return null for stateEvent


}