package com.simoneconigliaro.pictureengine.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.utils.ErrorState
import com.simoneconigliaro.pictureengine.utils.ErrorStateCallback
import com.simoneconigliaro.pictureengine.utils.UIController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_picture_list.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UIController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun displayProgressBar(isDisplayed: Boolean) {
        if (isDisplayed) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    override fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback) {
        Toast.makeText(this, errorState.message, Toast.LENGTH_LONG).show()
        errorStateCallback.removeErrorFromStack()
    }
}