package com.simoneconigliaro.pictureengine.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.persistence.PreferencesManager
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
    private val requestManager: RequestManager,
    private val preferencesManager: PreferencesManager
) : Fragment(R.layout.fragment_picture_list), PictureAdapter.Interaction {

    private val TAG = "ListFragment"

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var uiController: UIController

    private lateinit var pictureAdapter: PictureAdapter

    lateinit var sortOrder: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.setupChannel()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomAppBar()
        initData()
        initRecyclerView()
        subscribeObservers()

        swipe_refresh_layout.setOnRefreshListener {
            checkNetworkAndSetEvent(REFRESH, sortOrder)
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
                if (lastPosition == pictureAdapter.itemCount.minus(1) && lastPosition != -1) {
                    Log.d(TAG, "Attempting to load next page...")
                    checkNetworkAndSetEvent(NEXT_PAGE, sortOrder)
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
        lifecycleScope.launch {
            sortOrder = preferencesManager.getSortOrder()
            list_toolbar_title.text = sortOrder
            viewModel.setStateEvent(MainStateEvent.GetListPicturesEvent(sortOrder))
        }
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@PictureListFragment.context)
            addItemDecoration(TopSpacingItemDecoration(48))
            pictureAdapter = PictureAdapter(requestManager, this@PictureListFragment)
            adapter = pictureAdapter
        }
    }

    private fun initBottomAppBar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(bottom_app_bar)
        }
    }

    override fun onItemSelected(id: String) {
        viewModel.setStateEvent(MainStateEvent.GetPictureDetailEvent(id))
        findNavController().navigate(R.id.action_pictureListFragment_to_pictureDetailFragment)
    }

    private fun checkNetworkAndSetEvent(event: String, orderBy: String) {

        if (!NetworkUtil.isNetworkAvailable(context)) {
            showNoNetworkToast()
            return
        }

        when (event) {
            REFRESH -> {
                viewModel.refresh(orderBy = orderBy, isRefresh = true)
            }
            NEXT_PAGE -> {
                viewModel.nextPage(orderBy = orderBy)
            }
        }
    }

    private fun showMaterialDialog() {

        val myItems = listOf("Popular", "Latest", "Oldest")
        var initialSelection = 0

        lifecycleScope.launch {
            val orderBy = preferencesManager.getSortOrder()
            for (i in myItems.indices) {
                if (myItems[i] == orderBy) {
                    initialSelection = i
                }
            }
        }

        MaterialDialog(requireContext()).show {
            cornerRadius(16f)
            title(res = R.string.sort_by)
            listItemsSingleChoice(
                items = myItems,
                initialSelection = initialSelection
            ) { dialog, index, text ->

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { preferencesManager.saveSortOrder(text.toString()) }
                    sortOrder = preferencesManager.getSortOrder()
                    checkNetworkAndSetEvent(REFRESH, sortOrder)
                    this@PictureListFragment.list_toolbar_title.text = sortOrder
                }
            }
        }
    }

    private fun showNoNetworkToast() {
        requireContext().showToast(getString(R.string.unable_to_load_next_page))
        if (swipe_refresh_layout.isRefreshing) swipe_refresh_layout.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_order -> {
                showMaterialDialog()
            }
            android.R.id.home -> {
                findNavController().navigate(R.id.action_pictureListFragment_to_bottomSheetFragment)
            }
            else -> { }
        }

        return super.onOptionsItemSelected(item)
    }
}
