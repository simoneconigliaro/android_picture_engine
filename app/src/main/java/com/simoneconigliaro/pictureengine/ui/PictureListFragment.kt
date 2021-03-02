package com.simoneconigliaro.pictureengine.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent
import com.simoneconigliaro.pictureengine.utils.*
import com.simoneconigliaro.pictureengine.utils.Constants.NEXT_PAGE
import com.simoneconigliaro.pictureengine.utils.Constants.REFRESH
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_crop.*
import kotlinx.android.synthetic.main.fragment_picture_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

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

    private lateinit var dataStore: DataStore<Preferences>

    private val ORDER_BY_KEY = "order_by"

    lateinit var orderBy: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
        dataStore = requireContext().createDataStore(name = "sort_list")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        initData()
        initRecyclerView()
        subscribeObservers()



        swipe_refresh_layout.setOnRefreshListener {
            checkNetworkAndSetEvent(REFRESH, orderBy)
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
                    checkNetworkAndSetEvent(NEXT_PAGE, orderBy)
                }
            }
        })

        bottom_app_bar.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {

                R.id.action_order -> {
                    showMaterialDialog()
                    true
                }
                else -> {
                    false
                }
            }
        }
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
            orderBy = readPreferences(ORDER_BY_KEY)
            list_toolbar_title.text = orderBy
            viewModel.setStateEvent(MainStateEvent.GetListPicturesEvent(orderBy))
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

    private fun initToolBar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(list_toolbar)

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
            val orderBy = readPreferences(ORDER_BY_KEY)
            for(i in myItems.indices){
                if(myItems[i] == orderBy){
                    initialSelection = i
                }
            }
        }

        MaterialDialog(requireContext()).show {
            cornerRadius(16f)
            title(text = "Order by")
            listItemsSingleChoice(items = myItems, initialSelection = initialSelection) { dialog, index, text ->

                lifecycleScope.launch {
                    withContext(Dispatchers.IO){savePreferences(ORDER_BY_KEY, text.toString())}
                    orderBy = readPreferences(ORDER_BY_KEY)
                    checkNetworkAndSetEvent(REFRESH, orderBy)
                    this@PictureListFragment.list_toolbar_title.text = orderBy
                }
            }
        }
    }

    private fun showNoNetworkToast(){
        requireContext().showToast(getString(R.string.unable_to_load_next_page))
        if (swipe_refresh_layout.isRefreshing) swipe_refresh_layout.isRefreshing = false
    }

    private suspend fun savePreferences(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { sortList ->
            sortList[dataStoreKey] = value
        }
    }

    private suspend fun readPreferences(key: String): String {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey] ?: "latest"
    }
}
