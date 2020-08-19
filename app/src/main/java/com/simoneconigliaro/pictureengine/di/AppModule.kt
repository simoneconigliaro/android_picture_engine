package com.simoneconigliaro.pictureengine.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.simoneconigliaro.pictureengine.api.ApiService.Companion.BASE_URL
import com.simoneconigliaro.pictureengine.api.PictureDeserializer
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.persistence.PictureDao
import com.simoneconigliaro.pictureengine.persistence.PictureDatabase
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
            .registerTypeAdapter(Picture::class.java, PictureDeserializer())
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

}