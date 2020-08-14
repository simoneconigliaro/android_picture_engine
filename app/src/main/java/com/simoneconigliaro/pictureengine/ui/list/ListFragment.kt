package com.simoneconigliaro.pictureengine.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.simoneconigliaro.pictureengine.R
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment(R.layout.fragment_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_detailFragment)
        })

    }

}