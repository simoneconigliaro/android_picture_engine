package com.simoneconigliaro.pictureengine.utils

interface UIController {

    fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback)
}