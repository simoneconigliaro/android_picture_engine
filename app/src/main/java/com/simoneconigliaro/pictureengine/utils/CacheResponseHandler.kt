package com.simoneconigliaro.pictureengine.utils

import com.simoneconigliaro.pictureengine.utils.Constants.UNKNOWN_ERROR

abstract class CacheResponseHandler<ViewState, Data>(

    private val response: CacheResult<Data?>,
    private val stateEvent: StateEvent?
) {

    val result: DataState<ViewState> = when (response) {

        is CacheResult.GenericError -> {
            DataState.error(
                errorMessage = stateEvent?.errorInfo() + "\n\n Reason: " + response.errorMessage,
                stateEvent = stateEvent
            )
        }
        is CacheResult.Success -> {
            if (response.value == null) {
                DataState.error(
                    errorMessage = stateEvent?.errorInfo() + "\n\n Reason: " + UNKNOWN_ERROR,
                    stateEvent = stateEvent
                )
            } else {
                handleSuccess(response.value)
            }
        }
    }

    abstract fun handleSuccess(resultObj: Data): DataState<ViewState>


}