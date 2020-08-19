package com.simoneconigliaro.pictureengine.utils

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
abstract class DataChannelManager<ViewState> {

    private var channelScope: CoroutineScope? = null
    private val stateEventManager: StateEventManager = StateEventManager()

    val errorStack = ErrorStack()

    val shouldDisplayProgressBar = stateEventManager.shouldDisplayProgressBar

    fun setupChannel() {
        cancelJobs()
    }

    abstract fun handleNewData(data: ViewState)

    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<ViewState>?>
    ) {
        if (canExecuteNewStateEvent(stateEvent)) {
            Log.d("DCM", "launching job: ${stateEvent.eventName()}")
            addStateEvent(stateEvent)
            jobFunction
                .onEach { dataState ->
                    dataState?.let { dState ->
                        withContext(Dispatchers.Main) {
                            dataState.data?.let { data ->
                                handleNewData(data)
                            }
                            dataState.error?.let { error ->
                                handleNewErrorState(error)
                            }
                            dataState.stateEvent?.let { stateEvent ->
                                removeStateEvent(stateEvent)
                            }
                        }
                    }
                }
                .launchIn(getChannelScope())
        }
    }

    private fun canExecuteNewStateEvent(stateEvent: StateEvent): Boolean {
        // If a job is already active, do not allow duplication
        if (isJobAlreadyActive(stateEvent)) {
            return false
        }
        // if an error is showing, do not allow new StateEvents
        if (!isErrorStackEmpty()) {
            return false
        }
        return true
    }

    fun isErrorStackEmpty(): Boolean {
        return errorStack.isStackEmpty()
    }

    private fun handleNewErrorState(errorState: ErrorState) {
        appendErrorState(errorState)
    }

    private fun appendErrorState(errorState: ErrorState) {
        errorStack.add(errorState)
    }

    fun clearErrorState(index: Int = 0) {
        Log.d("DataChannelManager", "clear error state")
        errorStack.removeAt(index)
    }

    fun clearAllErrorStates() = errorStack.clear()

    fun printStateMessages() {
        for (error in errorStack) {
            Log.d("DCM", "${error.message}")
        }
    }

    // for debugging
    fun getActiveJobs() = stateEventManager.getActiveJobNames()

    fun clearActiveStateEventCounter() = stateEventManager.clearActiveStateEventCounter()

    fun addStateEvent(stateEvent: StateEvent) = stateEventManager.addStateEvent(stateEvent)

    fun removeStateEvent(stateEvent: StateEvent?) = stateEventManager.removeStateEvent(stateEvent)

    private fun isStateEventActive(stateEvent: StateEvent) =
        stateEventManager.isStateEventActive(stateEvent)

    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        return isStateEventActive(stateEvent)
    }

    fun getChannelScope(): CoroutineScope {
        return channelScope ?: setupNewChannelScope(CoroutineScope(Dispatchers.IO))
    }

    private fun setupNewChannelScope(coroutineScope: CoroutineScope): CoroutineScope {
        channelScope = coroutineScope
        return channelScope as CoroutineScope
    }

    fun cancelJobs() {
        if (channelScope != null) {
            if (channelScope?.isActive == true) {
                channelScope?.cancel()
            }
            channelScope = null
        }
        clearActiveStateEventCounter()
    }

}