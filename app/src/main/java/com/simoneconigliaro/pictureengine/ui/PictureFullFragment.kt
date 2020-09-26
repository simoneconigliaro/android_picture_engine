package com.simoneconigliaro.pictureengine.ui

import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_full.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class PictureFullFragment
constructor(
    private val requestManager: RequestManager
) : Fragment(R.layout.fragment_picture_full) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.detailFragmentViews.pictureDetail?.let { pictureDetail ->
                    requestManager.load(pictureDetail.regularUrl).into(iv_picture_full)
                }
            }
        })
    }
}