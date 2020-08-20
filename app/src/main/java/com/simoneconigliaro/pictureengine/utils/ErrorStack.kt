package com.simoneconigliaro.pictureengine.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.parcel.IgnoredOnParcel
import java.lang.IndexOutOfBoundsException

class ErrorStack : ArrayList<ErrorState>() {

    private val TAG = "ErrorStack"


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

        // prevent duplicate errors added to stack
        if (this.contains(element)) {
            return false
        }

        // if it's the first element added, it gets set straight in errorState
        super.add(element)
        Log.d(TAG, "element added to the stack: size ${this.size}")
        if (this.size == 1) {
            setErrorState(errorState = element)
            return true
        }

        // add element to the stack
        return true
    }

    // remove and element from the stack at the given index
    override fun removeAt(index: Int): ErrorState {
        if(this.size > 0) {
            try {
                val transaction = super.removeAt(index)
                Log.d(TAG, "element removed from the stack: size ${this.size}")

                // once we remove an element from the stack
                // we want to show the next error that moved to index 0
                // so we set it in the errorState
                if (this.size > 0) {
                    setErrorState(errorState = this[0])
                }

                // if the operation goes fine
                // return the ErrorState removed from the stack
                return transaction

            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }

        } else {
            setErrorState(null)

        }
        return ErrorState("does nothing")

    }


    private fun setErrorState(errorState: ErrorState?) {
        _errorState.value = errorState
    }


}