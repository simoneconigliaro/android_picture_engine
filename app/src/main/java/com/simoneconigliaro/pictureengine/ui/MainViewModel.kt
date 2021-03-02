package com.simoneconigliaro.pictureengine.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.repository.MainRepository
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent.GetListPicturesByQueryEvent
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent.GetListPicturesEvent
import com.simoneconigliaro.pictureengine.ui.state.MainViewState
import com.simoneconigliaro.pictureengine.utils.Constants.INVALID_STATE_EVENT
import com.simoneconigliaro.pictureengine.utils.DataChannelManager
import com.simoneconigliaro.pictureengine.utils.DataState
import com.simoneconigliaro.pictureengine.utils.ErrorState
import com.simoneconigliaro.pictureengine.utils.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Singleton

@Singleton
@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel
@ViewModelInject
constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val TAG = "MainViewModel"

    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()

    val viewState: LiveData<MainViewState>
        get() = _viewState

    private val _queries: MutableLiveData<ArrayList<String>> = MutableLiveData()

    val queries: LiveData<ArrayList<String>>
        get() = _queries


    val dataChannelManager: DataChannelManager<MainViewState> =
        object : DataChannelManager<MainViewState>() {

            override fun handleNewData(data: MainViewState) {
                this@MainViewModel.handleNewData(data)
            }
        }

    fun handleNewData(data: MainViewState) {
        data.let { viewState ->

            viewState.listFragmentViews.listPictures?.let { listPictures ->
                setListPictures(listPictures)
            }

            viewState.listFragmentViews.page?.let { page ->
                setPage(page)
            }

            viewState.searchFragmentViews.listPictures?.let { listPictures ->
                Log.d(TAG, "handleNewData: $listPictures")
                setSearchListPictures(listPictures)
            }

            viewState.detailFragmentViews.pictureDetail?.let { pictureDetail ->
                setPictureDetail(pictureDetail)
            }
        }
    }

    fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {
            val job: Flow<DataState<MainViewState>> =

                when (stateEvent) {

                    is GetListPicturesEvent -> {
                        Log.d(
                            TAG,
                            "is job already active: ${dataChannelManager.isJobAlreadyActive(
                                stateEvent
                            )}"
                        )
                        Log.d(TAG, "setStateEvent: GetListPicturesEvent: page ${getPage()}")
                        mainRepository.getListPictures(
                            isRefresh = stateEvent.isRefresh,
                            isNextPage = stateEvent.isNextPage,
                            page = getPage(),
                            orderBy = stateEvent.orderBy,
                            stateEvent = stateEvent
                        )
                    }

                    is GetListPicturesByQueryEvent -> {
                        Log.d(TAG, "viewstate setStateEvent: trigged ")
                        mainRepository.getListPicturesByQuery(
                            query = stateEvent.query,
                            page = getSearchPage(),
                            stateEvent = stateEvent
                        )
                    }

                    is MainStateEvent.GetPictureDetailEvent -> {
                        Log.d(TAG, "setStateEvent: GetPictureDetailEvent")
                        setPictureDetail(null)
                        mainRepository.getPictureById(stateEvent.id, stateEvent)
                    }
                    else -> {
                        emitInvalidStateEvent(stateEvent)
                    }
                }
            launchJob(stateEvent, job)
        }
    }

    fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    val shouldDisplayProgressBar: LiveData<Boolean> = dataChannelManager.shouldDisplayProgressBar

    val errorState: LiveData<ErrorState?>
        get() = dataChannelManager.errorStack.errorState

    fun setupChannel() = dataChannelManager.setupChannel()

    private fun emitInvalidStateEvent(stateEvent: StateEvent) = flow {
        emit(
            DataState.error<MainViewState>(
                errorMessage = INVALID_STATE_EVENT,
                stateEvent = stateEvent
            )
        )
    }

    private fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<MainViewState>?>
    ) = dataChannelManager.launchJob(stateEvent, jobFunction)

    fun clearErrorState(index: Int = 0) {
        dataChannelManager.clearErrorState(index)
        onCleared()
    }

    private fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        return dataChannelManager.isJobAlreadyActive(stateEvent)
    }

    private fun getCurrentViewStateOrNew(): MainViewState {
        return viewState.value ?: initNewViewState()
    }

    private fun setViewState(viewState: MainViewState) {
        _viewState.value = viewState
    }

    private fun getCurrentQueriesOrNew(): ArrayList<String> {
        return queries.value ?: ArrayList()
    }

    /**
     * ListFragmentViews
     */

    private fun setListPictures(listPictures: List<Picture>) {
        val update = getCurrentViewStateOrNew()
        update.listFragmentViews.listPictures = listPictures
        setViewState(update)
    }

    private fun getPage(): Int {
        return getCurrentViewStateOrNew().listFragmentViews.page ?: 1
    }

    private fun setPage(page: Int) {
        val update = getCurrentViewStateOrNew()
        update.listFragmentViews.page = page
        setViewState(update)
    }

    private fun incrementPageNumber() {
        val update = getCurrentViewStateOrNew()
        val page = update.copy().listFragmentViews.page ?: 1
        update.listFragmentViews.page = page.plus(1)
        Log.d(TAG, "incrementPageNumber: ${getPage()}")
        setViewState(update)
    }

    fun nextPage(orderBy: String) {
        if (!isJobAlreadyActive(GetListPicturesEvent(orderBy))) {
            Log.d(TAG, "BlogViewModel: Attempting to load next page...")
            incrementPageNumber()
            setStateEvent(GetListPicturesEvent(orderBy = orderBy, isNextPage = true))
        }
        Log.d(
            TAG,
            "is job already active: ${dataChannelManager.isJobAlreadyActive(GetListPicturesEvent(orderBy))}"
        )
    }

    private fun resetPage() {
        val update = getCurrentViewStateOrNew()
        update.listFragmentViews.page = 1
        setViewState(update)
    }

    fun refresh(orderBy: String, isRefresh: Boolean) {
        if (!isJobAlreadyActive(GetListPicturesEvent(orderBy = orderBy, isRefresh = isRefresh))) {
            resetPage()
            setStateEvent(GetListPicturesEvent(orderBy = orderBy, isRefresh = isRefresh))
        }
    }

    /**
     * DetailFragmentViews
     */

    private fun setPictureDetail(pictureDetail: PictureDetail?) {
        Log.d(TAG, "setPictureDetail: TRIGGERED")
        val update = getCurrentViewStateOrNew()
        update.detailFragmentViews.pictureDetail = pictureDetail
        setViewState(update)
    }


    /**
     * SearchFragmentViews
     */

    fun clearSearchFragmentViews() {
        val update = getCurrentViewStateOrNew()
        update.searchFragmentViews.listPictures = null
        update.searchFragmentViews.page = null
        setViewState(update)
    }
    private fun setSearchListPictures(newListPictures: List<Picture>?) {
        val update = getCurrentViewStateOrNew()
        val currentListPicture = update.searchFragmentViews.listPictures
        val page = getSearchPage()
        if (page > 1) {
            val mergedList = mergeListPictures(currentListPicture, newListPictures)
            update.searchFragmentViews.listPictures = mergedList
        } else {
            update.searchFragmentViews.listPictures = newListPictures
        }

        setViewState(update)
    }

    private fun mergeListPictures(
        currentListPictures: List<Picture>?,
        newListPictures: List<Picture>?
    ): ArrayList<Picture>? {
        val mergedList: ArrayList<Picture> = ArrayList()
        if (newListPictures != null) {
            if (currentListPictures != null) {
                mergedList.addAll(currentListPictures)
                mergedList.addAll(newListPictures)
            } else {
                mergedList.addAll(newListPictures)
            }
        }

        Log.d(TAG, "mergedlist: page: ${getSearchPage()}")
        Log.d(TAG, "mergedlist: ${mergedList.size}")
        for (item in mergedList) {
            Log.d(TAG, "mergedlist: ${item.id}")
        }
        return mergedList
    }

    fun restoreSearchFragmentViews(listPictures: List<Picture>, page: Int) {
        setSearchListPictures(listPictures)
        setSearchPage(page)
    }

    fun searchListPicturesByQuery(query: String) {
        replaceQuery(query)
        clearSearchFragmentViews()
        setStateEvent(GetListPicturesByQueryEvent(query))
    }

    fun searchListPicturesByTag(tag: String) {
        addQuery(tag)
        clearSearchFragmentViews()
        setStateEvent(GetListPicturesByQueryEvent(tag))
    }

    private fun addQuery(query: String) {
        val queries = getCurrentQueriesOrNew()
        queries.add(query)
        _queries.value = queries
    }

    private fun replaceQuery(query: String) {
        val queries = getCurrentQueriesOrNew()

        if (queries.size > 0) {
            queries[queries.lastIndex] = query
        } else {
            queries.add(query)
        }
        _queries.value = queries
    }

    // function to trigger the observer to observe queries even if they have not changed
    fun notifyQueriesHaveChanged() {
        if (_queries.value == null) {
            _queries.value = _queries.value
        }
    }

    @ExperimentalStdlibApi
    fun removeLastQuery() {
        val queries = getCurrentQueriesOrNew()
        if (queries.size > 0) {
            queries.removeLast()
            _queries.value = queries
        }
    }

    fun nextSearchPage(query: String) {
        if (!isJobAlreadyActive(GetListPicturesByQueryEvent(query))) {
            Log.d(TAG, "merged BlogViewModel: Attempting to load next page...")
            incrementSearchPageNumber()
            setStateEvent(GetListPicturesByQueryEvent(query))
        }
        Log.d(
            TAG,
            "is job already active: ${dataChannelManager.isJobAlreadyActive(
                GetListPicturesByQueryEvent(query)
            )}"
        )
    }

    private fun getSearchPage(): Int {
        return getCurrentViewStateOrNew().searchFragmentViews.page ?: 1
    }

    private fun setSearchPage(page: Int) {
        val update = getCurrentViewStateOrNew()
        update.searchFragmentViews.page = page
        setViewState(update)
    }

    private fun incrementSearchPageNumber() {
        val update = getCurrentViewStateOrNew()
        val page = update.copy().searchFragmentViews.page ?: 1
        update.searchFragmentViews.page = page.plus(1)
        setViewState(update)
    }

}