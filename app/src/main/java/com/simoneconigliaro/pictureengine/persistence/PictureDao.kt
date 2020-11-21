package com.simoneconigliaro.pictureengine.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simoneconigliaro.pictureengine.model.Picture
import com.simoneconigliaro.pictureengine.model.PictureDetail

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(picture: Picture): Long

    @Query("SELECT * FROM picture")
    suspend fun getAllPictures(): List<Picture>

    @Query("DELETE FROM picture")
    suspend fun deleteAllPictures()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictureDetail(pictureDetail: PictureDetail): Long

    @Query("SELECT * FROM picture_detail WHERE id = :id")
    suspend fun getPictureDetailById(id: String): PictureDetail

    @Query("DELETE FROM picture_detail")
    suspend fun deleteAllPictureDetail()
}