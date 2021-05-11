package com.simoneconigliaro.pictureengine.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.persistence.PreferencesManager
import com.simoneconigliaro.pictureengine.utils.ErrorState
import com.simoneconigliaro.pictureengine.utils.ErrorStateCallback
import com.simoneconigliaro.pictureengine.utils.UIController
import com.simoneconigliaro.pictureengine.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UIController {

    @Inject
    lateinit var preferencesManager: PreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager.getTheme.asLiveData().observe(this){theme ->
            AppCompatDelegate.setDefaultNightMode(theme)
        }
        setContentView(R.layout.activity_main)
    }

    override fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback) {
        showToast(errorState.message)
        errorStateCallback.removeErrorFromStack()
    }
}