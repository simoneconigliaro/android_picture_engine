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
                            stateEvent = stateEvent
                        )
                    }

                    is MainStateEvent.GetListPicturesByQueryEvent -> {
                        Log.d(TAG, "setStateEvent: GetListPicturesByQueryEvent")
                        mainRepository.getListPicturesByQuery(
                            query = stateEvent.query,
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

    fun getCurrentViewStateOrNew(): MainViewState {
        return viewState.value ?: initNewViewState()
    }

    private fun getPage(): Int {
        return getCurrentViewStateOrNew().listFragmentViews.page ?: 1
    }


    private fun setViewState(viewState: MainViewState) {
        _viewState.value = viewState
    }

    private fun setListPictures(listPictures: List<Picture>) {
        val update = getCurrentViewStateOrNew()
        update.listFragmentViews.listPictures = listPictures
        setViewState(update)
    }

    private fun setPage(page: Int) {
        val update = getCurrentViewStateOrNew()
        update.listFragmentViews.page = page
        setViewState(update)
    }

    private fun setSearchListPictures(listPictures: List<Picture>) {
        Log.d(TAG, "setSearchListPictures: $listPictures")
        val update = getCurrentViewStateOrNew()
        update.searchFragmentViews.listPictures = listPictures
        setViewState(update)
    }

    private fun setPictureDetail(pictureDetail: PictureDetail?) {
        val update = getCurrentViewStateOrNew()
        update.detailFragmentViews.pictureDetail = pictureDetail
        setViewState(update)
    }


    fun nextPage() {
        if (!isJobAlreadyActive(GetListPicturesEvent())) {
            Log.d(TAG, "BlogViewModel: Attempting to load next page...")
            incrementPageNumber()
            setStateEvent(GetListPicturesEvent(isNextPage = true))
        }
        Log.d(
            TAG,
            "is job already active: ${dataChannelManager.isJobAlreadyActive(GetListPicturesEvent())}"
        )
    }

    private fun incrementPageNumber() {
        val update = getCurrentViewStateOrNew()
        val page = update.copy().listFragmentViews.page ?: 1
        update.listFragmentViews.page = page.plus(1)
        Log.d(TAG, "incrementPageNumber: ${getPage()}")
        setViewState(update)
    }

    fun clearErrorState(index: Int = 0) {
        dataChannelManager.clearErrorState(index)
        onCleared()
    }

    private fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        return dataChannelManager.isJobAlreadyActive(stateEvent)
    }

    private fun resetPage() {
        val update = getCurrentViewStateOrNew()
        update.listFragmentViews.page = 1
        setViewState(update)
    }

    fun loadFirstPage(isRefresh: Boolean) {
        if (!isJobAlreadyActive(GetListPicturesEvent(isRefresh = isRefresh))) {
            resetPage()
            setStateEvent(GetListPicturesEvent(isRefresh = isRefresh))
        }
    }
}