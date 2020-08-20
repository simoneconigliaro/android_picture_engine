package com.simoneconigliaro.pictureengine.utils

interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback)
}