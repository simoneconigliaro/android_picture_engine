package com.simoneconigliaro.pictureengine.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class StateEventManager {

    private val activeStateEvents: HashMap<String, StateEvent> = HashMap()

    private val _shouldDisplayProgressBar: MutableLiveData<Boolean> = MutableLiveData()

    val shouldDisplayProgressBar: LiveData<Boolean>
        get() = _shouldDisplayProgressBar

    fun getActiveJobNames(): MutableSet<String> {
        return activeStateEvents.keys
    }

    fun clearActiveStateEventCounter() {
        activeStateEvents.clear()
        syncNumActiveStateEvents()
    }

    fun addStateEvent(stateEvent: StateEvent) {
        activeStateEvents.put(stateEvent.eventName(), stateEvent)
        syncNumActiveStateEvents()
    }

    fun removeStateEvent(stateEvent: StateEvent?) {
        activeStateEvents.remove(stateEvent?.eventName())
        syncNumActiveStateEvents()
    }

    fun isStateEventActive(stateEvent: StateEvent): Boolean {
        for (eventName in activeStateEvents.keys) {
            if (stateEvent.eventName().equals(eventName)) {
                return true
            }
        }
        return false
    }

    // check if there are active events that should display a progress bar
    private fun syncNumActiveStateEvents() {
        var shouldDisplayProgressBar = false
        for (stateEvent in activeStateEvents.values) {
            if (stateEvent.shouldDisplayProgressBar()) {
                shouldDisplayProgressBar = true
            }
        }
        _shouldDisplayProgressBar.value = shouldDisplayProgressBar
    }
}