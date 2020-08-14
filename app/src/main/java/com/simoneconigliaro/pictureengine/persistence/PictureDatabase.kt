package com.simoneconigliaro.pictureengine.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simoneconigliaro.pictureengine.model.Picture

@Database(entities = [Picture::class], version = 1)
abstract class PictureDatabase : RoomDatabase() {

    companion object {
        val DATABASE_NAME = "picture_db"
    }

    abstract fun pictureDao(): PictureDao


}