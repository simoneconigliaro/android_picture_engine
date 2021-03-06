package com.simoneconigliaro.pictureengine.repository

import com.simoneconigliaro.pictureengine.ui.state.MainViewState
import com.simoneconigliaro.pictureengine.utils.DataState
import com.simoneconigliaro.pictureengine.utils.StateEvent
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun getListPictures(isRefresh: Boolean, isNextPage: Boolean, page: Int, orderBy: String, stateEvent: StateEvent): Flow<DataState<MainViewState>>

    fun getListPicturesByQuery(query: String, page: Int, stateEvent: StateEvent): Flow<DataState<MainViewState>>

    fun getPictureById(id: String, stateEvent: StateEvent): Flow<DataState<MainViewState>>
}