package com.simoneconigliaro.pictureengine.repository

import android.util.Log
import com.project.simoneconigliaro.jetpackarchitecture.repository.NetworkBoundResource
import com.simoneconigliaro.pictureengine.BuildConfig
import com.simoneconigliaro.pictureengine.api.ApiService
import com.simoneconigliaro.pictureengine.api.responses.SearchResponse
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.persistence.PictureDao
import com.simoneconigliaro.pictureengine.ui.state.MainViewState
import com.simoneconigliaro.pictureengine.utils.ApiResponseHandler
import com.simoneconigliaro.pictureengine.utils.Constants.API_KEY
import com.simoneconigliaro.pictureengine.utils.Constants.REFRESH_CACHE_TIME
import com.simoneconigliaro.pictureengine.utils.DataState
import com.simoneconigliaro.pictureengine.utils.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.Exception



@FlowPreview
class MainRepositoryImpl
constructor(
    val apiService: ApiService,
    val pictureDao: PictureDao
) : MainRepository {


    private val TAG = "MainRepositoryImpl"

    override fun getListPictures(
        isRefresh: Boolean,
        isNextPage: Boolean,
        page: Int,
        orderBy: String,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> {
        return object : NetworkBoundResource<List<Picture>, List<Picture>, MainViewState>(
            dispatcher = IO,
            stateEvent = stateEvent,
            apiCall = { apiService.getListPictures(API_KEY, page, orderBy) },
            cacheCall = { pictureDao.getAllPictures() }
        ) {

            override suspend fun updateCache(networkObj: List<Picture>) {
                withContext(IO) {

                    val currentTime = System.currentTimeMillis() / 1000

                    Log.d(TAG, "MainRepositoryImpl: inserting pictures $networkObj")
                    for (picture in networkObj) {
                        try {
                            picture.timestamp = currentTime
                            picture.page = page
                            pictureDao.insert(picture)

                        } catch (e: Exception) {
                            Log.d(
                                TAG,
                                "updateCache: error updating cache data on picture with id: ${picture.id}"
                            )
                            Log.d(TAG, "updateCache: ${e.message}")
                        }
                    }
                }
            }

            override fun returnSuccessCache(
                resultObj: List<Picture>,
                stateEvent: StateEvent?
            ): DataState<MainViewState> {
                Log.d(TAG, "return updated cache size: ${resultObj.size}")
                for (item in resultObj) {
                    Log.d(TAG, "returnSuccessCache: ${item.id}")
                }

                val currentPage = getCurrentPage(resultObj)

                val viewState = MainViewState(
                    listFragmentViews = MainViewState.ListFragmentViews(
                        listPictures = resultObj,
                        page = currentPage
                    )
                )
                return DataState.data(
                    data = viewState,
                    stateEvent = stateEvent
                )
            }

            override suspend fun shouldReturnCache(cacheResult: List<Picture>?): Boolean {
                if (cacheResult == null || cacheResult.isEmpty() || isNextPage) {
                    Log.d(TAG, "Cache is empty or loading next page: Do not return cache.")
                    return false
                } else {

                    if (isRefresh) {
                        Log.d(TAG, "Cache is going to be refreshed, deleting current cache...")
                        withContext(IO) {
                            pictureDao.deleteAllPictures()
                        }
                        return false
                    } else {

                        val currentTime = System.currentTimeMillis() / 1000
                        val lastRefresh = cacheResult[0].timestamp
                        // check last refresh is not null
                        lastRefresh?.let { it ->
                            // check if cache is expired
                            if ((currentTime - it) > REFRESH_CACHE_TIME) {
                                Log.d(TAG, "Cache is expired: deleting current cache...")
                                withContext(IO) {
                                    pictureDao.deleteAllPictures()
                                }
                                return false
                            } else {
                                return true
                            }
                        } ?: return false
                    }
                }
            }
        }.result

    }

    override fun getListPicturesByQuery(
        query: String,
        page: Int,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> = flow {
        val apiResult = safeApiCall(IO) {
            apiService.getListPictureByQuery(API_KEY, query, page)
        }
        emit(
            // this expression returns a dataState (contains data or error) which will be emitted as a flow
            // ApiResponseHandler will determine what kind of dataState to return depending on the apiResult from the safeApiCall
            object : ApiResponseHandler<MainViewState, SearchResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override fun handleSuccess(resultObj: SearchResponse): DataState<MainViewState> {

                    Log.d(TAG, "handleSuccess: $resultObj")
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
    ): Flow<DataState<MainViewState>> {
        return object : NetworkBoundResource<PictureDetail, PictureDetail, MainViewState>(
            dispatcher = IO,
            stateEvent = stateEvent,
            apiCall = { apiService.getPictureById(id, API_KEY) },
            cacheCall = { pictureDao.getPictureDetailById(id) }
        ) {
            override suspend fun shouldReturnCache(cacheResult: PictureDetail?): Boolean {
                if (cacheResult!!.id == "") {
                    Log.d(TAG, "Cache is empty: Do Nothing.")
                    return false
                } else {

                    val currentTime = System.currentTimeMillis() / 1000
                    val lastRefresh = cacheResult.timestamp
                    // check last refresh is not null
                    lastRefresh?.let { it ->
                        // check if cache is expired
                        if ((currentTime - it) > REFRESH_CACHE_TIME) {
                            Log.d(TAG, "Cache is expired: deleting current cache...")
                            withContext(IO) {
                                pictureDao.deleteAllPictureDetail()
                            }
                            return false
                        } else {
                            return true
                        }
                    } ?: return false
                }
            }

            override suspend fun updateCache(networkObj: PictureDetail) {
                withContext(IO) {

                    val currentTime = System.currentTimeMillis() / 1000

                    Log.d(TAG, "MainRepositoryImpl: inserting detail picture $networkObj")

                    try {
                        networkObj.timestamp = currentTime
                        pictureDao.insertPictureDetail(networkObj)

                    } catch (e: Exception) {
                        Log.d(
                            TAG,
                            "updateCache: error updating cache data on picture detail with id: ${networkObj.id}"
                        )
                    }
                }
            }

            override fun returnSuccessCache(
                resultObj: PictureDetail,
                stateEvent: StateEvent?
            ): DataState<MainViewState> {
                Log.d(TAG, "return updated cache")
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
    }

    private fun getCurrentPage(listPictures: List<Picture>): Int {
        return if (listPictures.isNotEmpty()) listPictures.last().page else 1
    }
}