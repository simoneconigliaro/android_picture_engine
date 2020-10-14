package com.simoneconigliaro.pictureengine.repository

import android.util.Log
import com.simoneconigliaro.pictureengine.api.ApiService
import com.simoneconigliaro.pictureengine.api.responses.SearchResponse
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.persistence.PictureDao
import com.simoneconigliaro.pictureengine.ui.state.MainViewState
import com.simoneconigliaro.pictureengine.utils.ApiResponseHandler
import com.simoneconigliaro.pictureengine.utils.Constants.API_KEY
import com.simoneconigliaro.pictureengine.utils.DataState
import com.simoneconigliaro.pictureengine.utils.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MainRepositoryImpl
constructor(
    val apiService: ApiService,
    val pictureDao: PictureDao
) : MainRepository {
    override fun getListPictures(
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> = flow {
        val apiResult = safeApiCall(IO) {
            apiService.getListPictures(API_KEY)
        }
        emit(
            // this expression returns a dataState (contains data or error) which will be emitted as a flow
            // ApiResponseHandler will determine what kind of dataState to return depending on the apiResult from the safeApiCall
            object : ApiResponseHandler<MainViewState, List<Picture>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override fun handleSuccess(resultObj: List<Picture>): DataState<MainViewState> {
                    val viewState = MainViewState(
                        listFragmentViews = MainViewState.ListFragmentViews(
                            listPictures = resultObj
                        )
                    )
                    return DataState.data(
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }
            }.result
        )
    }

    override fun getListPicturesByQuery(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> = flow {
        val apiResult = safeApiCall(IO) {
            apiService.getListPictureByQuery(query, API_KEY)
        }
        emit(
            // this expression returns a dataState (contains data or error) which will be emitted as a flow
            // ApiResponseHandler will determine what kind of dataState to return depending on the apiResult from the safeApiCall
            object : ApiResponseHandler<MainViewState, SearchResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override fun handleSuccess(resultObj: SearchResponse): DataState<MainViewState> {
                    val viewState = MainViewState(
                        searchFragmentViews = MainViewState.SearchFragmentViews(
                            listPictures = resultObj.listPictures
                        )
                    )
                    return DataState.data(
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }
            }.result
        )
    }

    override fun getPictureById(
        id: String,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> = flow {
        val apiResult = safeApiCall(IO) {
            apiService.getPictureById(id, API_KEY)
        }
        emit(
            object : ApiResponseHandler<MainViewState, PictureDetail>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override fun handleSuccess(resultObj: PictureDetail): DataState<MainViewState> {
                    Log.d("", "handleSuccess: ${resultObj.id}")
                    val viewState = MainViewState(
                        detailFragmentViews = MainViewState.DetailFragmentViews(
                            pictureDetail = resultObj
                        )
                    )
                    return DataState.data(
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }
            }.result
        )
    }

}