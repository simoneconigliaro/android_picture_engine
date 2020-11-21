package com.simoneconigliaro.pictureengine.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getColor
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
import kotlinx.android.synthetic.main.fragment_picture_list.*
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

    private lateinit var pictureAdapter: PictureAdapter

    private lateinit var uiController: UIController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        initSearchTextInputLayout()
        disableSwipeDownRefreshLayout()
        initRecyclerView()
        subscribeObservers()

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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            if (viewState != null) {
                viewState.searchFragmentViews.listPictures?.let {
                    Log.d(TAG, "subscribeObservers: $it")

                    if (it.isNotEmpty()) {

                        pictureAdapter.submitList(it)

                        layout_empty_list.visibility = View.GONE
                    } else {
                        layout_empty_list.visibility = View.VISIBLE
                    }
                }
            }


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
        })
    }

    private fun initToolBar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(search_toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true);
        }
    }

    private fun initSearchTextInputLayout() {

        search_text_input_layout.editText?.requestFocus()
        search_text_input_layout.editText?.showKeyboard()

        search_text_input_layout.editText?.setOnEditorActionListener { _, actionId, _ ->

            val query = search_text_input_layout.editText?.text.toString()

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (query.isNotBlank()) {
                    pictureAdapter.submitList(null)
                    viewModel.setStateEvent(MainStateEvent.GetListPicturesByQueryEvent(query))
                    search_text_input_layout.editText?.hideKeyboard()
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun disableSwipeDownRefreshLayout(){
        swipe_refresh_layout_search.isEnabled = false
    }

    private fun initRecyclerView() {
        recycler_view_search.apply {
            layoutManager = LinearLayoutManager(this@PictureSearchFragment.context)
            addItemDecoration(TopSpacingItemDecoration(48))
            pictureAdapter = PictureAdapter(requestManager, this@PictureSearchFragment)
            adapter = pictureAdapter
        }
    }

    override fun onItemSelected(id: String) {
        viewModel.setStateEvent(MainStateEvent.GetPictureDetailEvent(id))
        findNavController().navigate(R.id.action_pictureSearchFragment_to_pictureDetailFragment)
    }
}