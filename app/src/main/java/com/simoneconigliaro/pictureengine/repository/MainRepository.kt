package com.simoneconigliaro.pictureengine.repository

import com.simoneconigliaro.pictureengine.ui.state.MainViewState
import com.simoneconigliaro.pictureengine.utils.DataState
import com.simoneconigliaro.pictureengine.utils.StateEvent
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun getListPictures(stateEvent: StateEvent): Flow<DataState<MainViewState>>

    fun getListPicturesByQuery(query: String, stateEvent: StateEvent): Flow<DataState<MainViewState>>

    fun getPictureById(id: String, stateEvent: StateEvent): Flow<DataState<MainViewState>>
}