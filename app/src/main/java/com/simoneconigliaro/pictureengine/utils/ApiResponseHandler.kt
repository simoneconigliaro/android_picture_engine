package com.simoneconigliaro.pictureengine.utils

import com.simoneconigliaro.pictureengine.utils.Constants.NETWORK_ERROR
import com.simoneconigliaro.pictureengine.utils.Constants.UNKNOWN_ERROR

abstract class ApiResponseHandler<ViewState, Data>(
    response: ApiResult<Data?>,
    stateEvent: StateEvent
) {

    val result: DataState<ViewState> = when(response) {

        is ApiResult.GenericError -> {
            DataState.error(
                errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + response.errorMessage,
                stateEvent = stateEvent
            )

        }
        is ApiResult.NetworkError ->{
            DataState.error(
                errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + NETWORK_ERROR,
                stateEvent = stateEvent
            )

        }
        is ApiResult.Success -> {
            if(response.value == null) {
                DataState.error(
                    errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + UNKNOWN_ERROR,
                    stateEvent = stateEvent
                )
            } else {
                handleSuccess(response.value)
            }
        }
    }

    abstract fun handleSuccess(resultObj: Data): DataState<ViewState>

}