package com.simoneconigliaro.pictureengine.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture_detail")
data class PictureDetail(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "width")
    var width: String?,

    @ColumnInfo(name = "height")
    var height: String?,

    @ColumnInfo(name = "regular_url")
    var regularUrl: String?,

    @ColumnInfo(name = "share_url")
    var shareUrl: String?,

    @ColumnInfo(name = "download_url")
    var downloadUrl: String?,

    @ColumnInfo(name = "likes")
    var likes: String?,

    @ColumnInfo(name = "views")
    var views: String?,

    @ColumnInfo(name = "downloads")
    var downloads: String?,

    @ColumnInfo(name = "username")
    var username: String?,

    @ColumnInfo(name = "user_picture")
    var userPicture: String?,

    @ColumnInfo(name = "location")
    var location: String?,

    @ColumnInfo(name = "camera_model")
    var cameraModel: String?,

    @ColumnInfo(name = "camera_exposure_time")
    var cameraExposureTime: String?,

    @ColumnInfo(name = "camera_aperture")
    var cameraAperture: String?,

    @ColumnInfo(name = "camera_focal_length")
    var cameraFocalLength: String?,

    @ColumnInfo(name = "camera_iso")
    var cameraIso: String?,

    @ColumnInfo(name = "tags")
    var tags: ArrayList<String>?,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long?

) {


}