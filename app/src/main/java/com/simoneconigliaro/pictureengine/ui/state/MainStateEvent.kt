package com.simoneconigliaro.pictureengine.ui.state

import com.simoneconigliaro.pictureengine.utils.Constants.UNABLE_TO_RETRIEVE_PICTURES
import com.simoneconigliaro.pictureengine.utils.StateEvent

sealed class MainStateEvent : StateEvent {

    object GetListPicturesEvent : MainStateEvent() {

        override fun errorInfo(): String {
            return UNABLE_TO_RETRIEVE_PICTURES
        }

        override fun eventName(): String {
            return GetListPicturesEvent::class.java.name
        }

        override fun shouldDisplayProgressBar(): Boolean {
            return true
        }
    }
}