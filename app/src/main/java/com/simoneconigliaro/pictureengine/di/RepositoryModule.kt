package com.simoneconigliaro.pictureengine.di

import com.simoneconigliaro.pictureengine.api.ApiService
import com.simoneconigliaro.pictureengine.persistence.PictureDao
import com.simoneconigliaro.pictureengine.repository.MainRepository
import com.simoneconigliaro.pictureengine.repository.MainRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideApiService(retrofitBuilder: Retrofit.Builder): ApiService {
        return retrofitBuilder
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMainRepository(apiService: ApiService, pictureDao: PictureDao): MainRepository {
        return MainRepositoryImpl(
            apiService,
            pictureDao
        )
    }

}