package com.simoneconigliaro.pictureengine.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.utils.ErrorState
import com.simoneconigliaro.pictureengine.utils.ErrorStateCallback
import com.simoneconigliaro.pictureengine.utils.UIController
import com.simoneconigliaro.pictureengine.utils.showToast
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UIController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback) {
        showToast(errorState.message)
        errorStateCallback.removeErrorFromStack()
    }
}