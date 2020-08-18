package com.simoneconigliaro.pictureengine.ui.state

import com.simoneconigliaro.pictureengine.utils.StateEvent

sealed class MainStateEvent : StateEvent {

    object GetListPicturesEvent : MainStateEvent() {

        override fun errorInfo(): String {
            return "Unable to retrieve list pictures"
        }

        override fun eventName(): String {
            return GetListPicturesEvent::class.java.name
        }
    }
}