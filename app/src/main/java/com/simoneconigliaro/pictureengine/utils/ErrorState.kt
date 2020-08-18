package com.simoneconigliaro.pictureengine.utils

// data class that contains a string message
// this message will contain a stateEvent.errorInfo() + the reason of this error (response error, network error, unknown error)

data class ErrorState(
    var message: String
)