package com.simoneconigliaro.pictureengine.ui.list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.api.ApiService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {

    private val TAG = "ListFragment"

    @Inject
    lateinit var apiService: ApiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // testing navigation
        button.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_detailFragment)
        })

        // testing hilt and apiService
        CoroutineScope(IO).launch {

            val listPictures =
                apiService.getListPictures("KwyUARTWbUVl9HiFyVGhLWRV7eJvx2Pv-FrV56brzs0")

            for(picture in listPictures){

                Log.d(TAG, "onViewCreated: id: ${picture.id}")
                Log.d(TAG, "onViewCreated: url: ${picture.url}")
                Log.d(TAG, "onViewCreated: username: ${picture.username}")
                Log.d(TAG, "onViewCreated: userPicture: ${picture.userPicture}")
            }
        }

    }
}
