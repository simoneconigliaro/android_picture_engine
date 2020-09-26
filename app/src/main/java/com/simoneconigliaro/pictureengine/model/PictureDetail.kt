package com.simoneconigliaro.pictureengine.model

data class PictureDetail(

    var id: String,
    var width: String?,
    var height: String?,
    var regularUrl: String?,
    var shareUrl: String?,
    var downloadUrl: String?,
    var likes: String?,
    var views: String?,
    var downloads: String?,
    var username: String?,
    var userPicture: String?,
    var location: String?,
    var cameraModel: String?,
    var cameraExposureTime: String?,
    var cameraAperture: String?,
    var cameraFocalLength: String?,
    var cameraIso: String?,
    var tags: ArrayList<String>?

){



}