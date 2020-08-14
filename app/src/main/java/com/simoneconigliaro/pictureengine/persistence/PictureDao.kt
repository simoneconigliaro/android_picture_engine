package com.simoneconigliaro.pictureengine.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simoneconigliaro.pictureengine.model.Picture

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(picture: Picture): Long

    @Query("SELECT * FROM picture")
    suspend fun getAllPictures(): List<Picture>
}