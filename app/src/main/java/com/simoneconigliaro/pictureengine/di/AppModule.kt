package com.simoneconigliaro.pictureengine.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.api.ApiService.Companion.BASE_URL
import com.simoneconigliaro.pictureengine.api.deserializers.PictureDeserializer
import com.simoneconigliaro.pictureengine.api.deserializers.PictureDetailDeserializer
import com.simoneconigliaro.pictureengine.api.deserializers.PictureSearchDeserializer
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.api.responses.SearchResponse
import com.simoneconigliaro.pictureengine.persistence.PictureDatabase
import com.simoneconigliaro.pictureengine.persistence.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Picture::class.java,
                PictureDeserializer()
            )
            .registerTypeAdapter(SearchResponse::class.java,
                PictureSearchDeserializer()
            )
            .registerTypeAdapter(PictureDetail::class.java,
                PictureDetailDeserializer()
            )
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun providePictureDatabase(@ApplicationContext context: Context): PictureDatabase {
        return Room.databaseBuilder(
            context,
            PictureDatabase::class.java,
            PictureDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context) : PreferencesManager {
        return PreferencesManager(context)
    }

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .errorOf(R.drawable.default_image)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        application: Application,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }
}