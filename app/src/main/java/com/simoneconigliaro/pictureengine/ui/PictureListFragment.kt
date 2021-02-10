package com.simoneconigliaro.pictureengine.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent
import com.simoneconigliaro.pictureengine.utils.*
import com.simoneconigliaro.pictureengine.utils.Constants.NEXT_PAGE
import com.simoneconigliaro.pictureengine.utils.Constants.REFRESH
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_list.*
import kotlinx.coroutines.*

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PictureListFragment
constructor(
    private val requestManager: RequestManager
) : Fragment(R.layout.fragment_picture_list), PictureAdapter.Interaction {

    private val TAG = "ListFragment"

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var uiController: UIController

    private lateinit var pictureAdapter: PictureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
        initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeObservers()

        swipe_refresh_layout.setOnRefreshListener {
            checkNetworkAndSetEvent(REFRESH)
        }

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) fab_search.hide() else if (dy < 0) fab_search.show()
            }
        })

        fab_search.setOnClickListener(View.OnClickListener {
            viewModel.clearSearchFragmentViews()
            findNavController().navigate(R.id.action_pictureListFragment_to_pictureSearchFragment)
        })


        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                // if last position is -1 means the recyclerView is empty
                if (lastPosition == pictureAdapter.itemCount.minus(1) && lastPosition != -1 ) {
                    Log.d(TAG, "Attempting to load next page...")
                    checkNetworkAndSetEvent(NEXT_PAGE)
                }
            }
        })
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

            if (viewState != null) {
                viewState.listFragmentViews.listPictures?.let {

                    if (it != pictureAdapter.getList()) {
                        Log.d(TAG, "subscribeObservers: adapter size ${pictureAdapter.itemCount}")
                        Log.d(TAG, "subscribeObservers: observer size: ${it.size}")
                        pictureAdapter.submitList(it)
                    }
                }
            }
        })

        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "subscribeObservers: refreshing: $it")
            swipe_refresh_layout.isRefreshing = it

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
        viewModel.setStateEvent(MainStateEvent.GetListPicturesEvent())
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@PictureListFragment.context)
            addItemDecoration(TopSpacingItemDecoration(48))
            pictureAdapter = PictureAdapter(requestManager, this@PictureListFragment)
            adapter = pictureAdapter
        }
    }

    override fun onItemSelected(id: String) {
        viewModel.setStateEvent(MainStateEvent.GetPictureDetailEvent(id))
        findNavController().navigate(R.id.action_pictureListFragment_to_pictureDetailFragment)
    }

    private fun checkNetworkAndSetEvent(event: String) {

        if (!NetworkUtil.isNetworkAvailable(context)) {
            requireContext().showToast(getString(R.string.unable_to_load_next_page))
            Log.d(TAG, "Unable to load next page: No internet connection.")
            if (swipe_refresh_layout.isRefreshing) swipe_refresh_layout.isRefreshing = false
            return
        }

        when (event) {
            REFRESH -> {
                viewModel.loadFirstPage(isRefresh = true)
            }
            NEXT_PAGE -> {
                viewModel.nextPage()
            }
        }
    }
}
