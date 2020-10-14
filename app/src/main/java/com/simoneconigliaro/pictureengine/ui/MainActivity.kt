package com.simoneconigliaro.pictureengine.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.utils.ErrorState
import com.simoneconigliaro.pictureengine.utils.ErrorStateCallback
import com.simoneconigliaro.pictureengine.utils.UIController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_picture_search.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UIController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback) {
        Toast.makeText(this, errorState.message, Toast.LENGTH_LONG).show()
        errorStateCallback.removeErrorFromStack()
    }
}