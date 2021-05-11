package com.simoneconigliaro.pictureengine.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent
import com.simoneconigliaro.pictureengine.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_search.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PictureSearchFragment
constructor(private val requestManager: RequestManager) :
    Fragment(R.layout.fragment_picture_search), PictureAdapter.Interaction {

    private val TAG = "PictureSearchFragment"

    private val viewModel: MainViewModel by activityViewModels()

    private val pictureAdapter: PictureAdapter =
        PictureAdapter(requestManager, this@PictureSearchFragment)

    private lateinit var uiController: UIController

    private var query: String? = null

    private var page: Int = 1

    private var focus: Boolean = true

    private var listPictures: List<Picture> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        disableSwipeDownRefreshLayout()
        initRecyclerView()
        subscribeObservers()
        checkIfListIsEmpty()
        viewModel.notifyQueriesHaveChanged()
        restoreQuerySearchText()

        recycler_view_search.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == pictureAdapter.itemCount.minus(1) && pictureAdapter.itemCount != 0) {
                    Log.d(TAG, "onScrollStateChanged: $query")
                    query?.let {
                        viewModel.nextSearchPage(it)
                    }
                }
            }
        })

        search_text_input_layout.editText?.setOnEditorActionListener { _, actionId, _ ->

            query = search_text_input_layout.editText?.text.toString()

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                query?.let {
                    if (it.isNotBlank()) {
                        viewModel.searchListPicturesByQuery(it)
                        clearFocusAndHideKeyboard()
                        recycler_view_search.scrollToPosition(0)
                    }
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
            R.id.action_nav_main_page -> {
                findNavController().navigate(R.id.action_pictureSearchFragment_to_pictureListFragment)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->


            if (viewState != null) {

                // Our view model doesn't keep track of all the list searched, it can only save the most recent one
                // Since we are not saving all the search list pictures and pages while navigating through the fragments
                // we need to restore them in the view model once we the user starts navigating back
                // so if the user scrolls down for new pages they will get the correct ones (page and query) added to the ones already shown
                if (listPictures.isNotEmpty() && viewState.searchFragmentViews.listPictures == null) {
                    viewModel.restoreSearchFragmentViews(listPictures, page)
                }

                viewState.searchFragmentViews.listPictures?.let {

                    Log.d(TAG, "from viewmodel subscribeObservers: $it")
                    if (it.isNotEmpty() && it != listPictures) {
                        listPictures = it
                        pictureAdapter.submitList(listPictures)
                        layout_empty_list.visibility = View.GONE
                    }

                    // if the list is empty, the search didn't show any result
                    if (it.isEmpty()){
                        pictureAdapter.clear()
                        showNoResults()
                    }
                }

                viewState.searchFragmentViews.page?.let {
                    if (it != page) {
                        page = it
                    }
                }

            }
        })

        viewModel.queries.observe(viewLifecycleOwner, Observer { queries ->
            Log.d(TAG, "subscribeObservers queries: $queries")

            if (queries != null) {

                if (queries.size > 0) {
                    if (queries.last() != query) {
                        query = queries.last()
                        search_text_input_layout.editText!!.setText(query)
                    }
                } else {
                    requestFocusAndShowKeyboard()
                }
            } else {
                requestFocusAndShowKeyboard()
            }
        })

        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner, Observer {
            swipe_refresh_layout_search.isRefreshing = it
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

    private fun initToolBar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(search_toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true);
        }
    }

    @ExperimentalStdlibApi
    override fun onDetach() {
        super.onDetach()
        viewModel.removeLastQuery()
        viewModel.clearSearchFragmentViews()
    }

    override fun onStop() {
        super.onStop()
        clearFocusAndHideKeyboard()
    }

    private fun requestFocusAndShowKeyboard() {
        if (focus) {
            search_text_input_layout.editText?.requestFocus()
            search_text_input_layout.editText?.showKeyboard()
            focus = false
        }
    }

    private fun clearFocusAndHideKeyboard() {
        search_text_input_layout.editText?.clearFocus()
        search_text_input_layout.editText?.hideKeyboard()
    }

    private fun disableSwipeDownRefreshLayout() {
        swipe_refresh_layout_search.isEnabled = false
    }

    private fun checkIfListIsEmpty() {
        if (pictureAdapter.isEmpty()) {
            layout_empty_list.visibility = View.VISIBLE
        } else {
            layout_empty_list.visibility = View.GONE
        }
    }

    private fun showNoResults() {
        layout_empty_list.visibility = View.VISIBLE
        tv_bold_info.text = getString(R.string.no_results)
        tv_regular_info.text = getString(R.string.search_something_else)
    }

    private fun initRecyclerView() {
        recycler_view_search.apply {
            layoutManager = LinearLayoutManager(this@PictureSearchFragment.context)
            addItemDecoration(TopSpacingItemDecoration(48))
            adapter = pictureAdapter
        }
    }

    private fun restoreQuerySearchText() {
        query?.let {
            search_text_input_layout.editText!!.setText(query)
        }
    }

    override fun onItemSelected(id: String) {
        viewModel.setStateEvent(MainStateEvent.GetPictureDetailEvent(id))
        findNavController().navigate(R.id.action_pictureSearchFragment_to_pictureDetailFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}