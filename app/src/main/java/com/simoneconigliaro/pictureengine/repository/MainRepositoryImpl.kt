package com.simoneconigliaro.pictureengine.repository

import com.simoneconigliaro.pictureengine.api.ApiService
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.persistence.PictureDao
import com.simoneconigliaro.pictureengine.ui.state.MainStateEvent
import com.simoneconigliaro.pictureengine.ui.state.MainViewState
import com.simoneconigliaro.pictureengine.utils.ApiResponseHandler
import com.simoneconigliaro.pictureengine.utils.Constants.API_KEY
import com.simoneconigliaro.pictureengine.utils.DataState
import com.simoneconigliaro.pictureengine.utils.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


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


}