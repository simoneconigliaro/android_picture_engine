package com.simoneconigliaro.pictureengine.api

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
        @Query("client_id") apiKey: String
    ): List<Picture>

    @GET("photos/{id}/")
    suspend fun getPictureById(
        @Path("id") id: String,
        @Query("client_id") apiKey: String
    ): PictureDetail

}