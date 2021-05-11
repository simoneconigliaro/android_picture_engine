package com.simoneconigliaro.pictureengine.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture")
data class Picture(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "userpicture")
    var userPicture: String,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long?,

    @ColumnInfo(name = "page")
    var page: Int = 1

)