package com.simoneconigliaro.pictureengine.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.parcel.IgnoredOnParcel
import java.lang.IndexOutOfBoundsException

class ErrorStack : ArrayList<ErrorState>() {


    // errorState is observed in the UI and it's used to show error messages
    // so when we set new data to it, the UI will get notified of that change
    // it always gets the element at index 0 from the ErrorStack to be shown in the UI
    @IgnoredOnParcel
    private val _errorState: MutableLiveData<ErrorState?> = MutableLiveData()

    @IgnoredOnParcel
    val errorState: LiveData<ErrorState?>
        get() = _errorState

    fun isStackEmpty(): Boolean {
        return size == 0
    }

    // used to add a collection of ErrorState to the list
    override fun addAll(elements: Collection<ErrorState>): Boolean {
        for (element in elements) {
            add(element)
        }
        return true
    }

    // used to add an element to the stack,
    override fun add(element: ErrorState): Boolean {
        // if it's the first element added, it gets set straight in errorState
        if (this.size == 0) {
            setErrorState(errorState = element)
        }
        // prevent duplicate errors added to stack
        if (this.contains(element)) {
            return false
        }
        // add element to the stack
        return super.add(element)
    }

    // remove and element from the stack at the given index
    override fun removeAt(index: Int): ErrorState {
        try {
            val transaction = super.removeAt(index)

            // once we remove an element from the stack
            // we want to show the next error that moved to index 0
            // so we set it in the errorState
            if (this.size > 0) {
                setErrorState(errorState = this[0])
            }
            // in case the size of the stack is less than 0 we want to do nothing
            else {
                setErrorState(null)
            }
            // if the operation goes fine
            // return the ErrorState removed from the stack
            return transaction
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        // in case the it fails removing the item from the stack, return this errorState
        return ErrorState("does nothing")

    }


    private fun setErrorState(errorState: ErrorState?) {
        _errorState.value = errorState
    }


}