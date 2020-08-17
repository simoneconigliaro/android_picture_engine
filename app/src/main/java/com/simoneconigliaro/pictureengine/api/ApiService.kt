package com.simoneconigliaro.pictureengine.api

import com.simoneconigliaro.pictureengine.model.Picture
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    companion object {

        val BASE_URL = "https://api.unsplash.com/"
    }

    @GET("photos")
    suspend fun getListPictures(@Query("client_id") apiKey: String) : List<Picture>
}