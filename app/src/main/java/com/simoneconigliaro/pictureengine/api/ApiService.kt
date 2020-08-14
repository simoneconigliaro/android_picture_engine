package com.simoneconigliaro.pictureengine.api

import com.simoneconigliaro.pictureengine.model.Picture
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {

    companion object {

        val BASE_URL = "https://api.unsplash.com"
    }

    @GET("/photos")
    suspend fun getListPictures(@Header("client_id") apiKey: String) : List<Picture>
}