package com.simoneconigliaro.pictureengine.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.api.ApiService
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent
import com.simoneconigliaro.pictureengine.utils.ErrorStateCallback
import com.simoneconigliaro.pictureengine.utils.TopSpacingItemDecoration
import com.simoneconigliaro.pictureengine.utils.UIController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import java.lang.ClassCastException
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PictureListFragment
constructor(
    private val requestManager: RequestManager
) : Fragment(R.layout.fragment_picture_list), PictureAdapter.Interaction {

    private val TAG = "ListFragment"

    private val viewModel: MainViewModel by viewModels()

    lateinit var uiController: UIController

    lateinit var pictureAdapter: PictureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeObservers()
        initData()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            if (activity is MainActivity) {
                try {
                    uiController = context as UIController
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            viewState.listFragmentViews.listPictures?.let {
                pictureAdapter.submitList(it)
            }
        })

        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner, Observer {
            uiController.displayProgressBar(it)
        })

        viewModel.errorState.observe(viewLifecycleOwner, Observer { errorState ->
            errorState?.let {
                uiController.onErrorReceived(
                    errorState = it,
                    errorStateCallback = object : ErrorStateCallback {
                        override fun removeErrorFromStack() {
                            viewModel.clearErrorState(0)
                        }
                    }
                )
            }
        })
    }

    private fun initData() {
        viewModel.setStateEvent(MainStateEvent.GetListPicturesEvent)
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@PictureListFragment.context)
            addItemDecoration(TopSpacingItemDecoration(30))
            pictureAdapter = PictureAdapter(requestManager, this@PictureListFragment)
            adapter = pictureAdapter
        }
    }

    override fun onItemSelected(item: Picture) {
        Toast.makeText(context, "" + item.id, Toast.LENGTH_LONG).show()
    }
}
