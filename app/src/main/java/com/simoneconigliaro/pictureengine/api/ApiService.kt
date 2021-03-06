package com.simoneconigliaro.pictureengine.api

import com.simoneconigliaro.pictureengine.api.responses.SearchResponse
import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.model.Picture
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
    }

    @GET("photos/")
    suspend fun getListPictures(
        @Query("client_id") apiKey: String,
        @Query("page") page: Int,
        @Query("order_by") orderBy: String
    ): List<Picture>

    @GET("search/photos/")
    suspend fun getListPictureByQuery(
        @Query("client_id") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ) : SearchResponse

    @GET("photos/{id}/")
    suspend fun getPictureById(
        @Path("id") id: String,
        @Query("client_id") apiKey: String
    ): PictureDetail

}