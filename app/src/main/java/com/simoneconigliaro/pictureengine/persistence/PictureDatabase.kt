package com.simoneconigliaro.pictureengine.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.utils.Converters

@Database(entities = [Picture::class, PictureDetail::class], version = 3)
@TypeConverters(Converters::class)
abstract class PictureDatabase : RoomDatabase() {

    companion object {
        val DATABASE_NAME = "picture_db"
    }

    abstract fun pictureDao(): PictureDao


}