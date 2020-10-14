package com.simoneconigliaro.pictureengine.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.simoneconigliaro.pictureengine.model.PictureDetail
import java.lang.Exception
import java.lang.reflect.Type
import kotlin.collections.ArrayList

class PictureDetailDeserializer : JsonDeserializer<PictureDetail> {


    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PictureDetail {

        if (json != null) {


            val jsonObjects = json.asJsonObject

            val id = getString(jsonObjects, "id")

            val width = getString(jsonObjects, "width")
            val height = getString(jsonObjects, "height")

            val urls = jsonObjects.get("urls").asJsonObject
            val regularUrl = getString(urls, "regular")

            val links = jsonObjects.get("links").asJsonObject
            val shareUrl = getString(links, "html")
            val downloadUrl = getString(links, "download")

            val likes = getString(jsonObjects, "likes")
            val views = getString(jsonObjects, "views")
            val downloads = getString(jsonObjects, "downloads")

            val user = jsonObjects.get("user").asJsonObject
            val username = getString(user, "name")

            val profileImage = user.get("profile_image").asJsonObject
            val mediumProfilePicture = getString(profileImage, "medium")

            val location = jsonObjects.get("location").asJsonObject
            val locationName = getString(location, "name")

            val cameraInfo = jsonObjects.get("exif").asJsonObject
            val cameraBrand = getString(cameraInfo, "make")
            val cameraModel = getString(cameraInfo, "model")
            val cameraExposureTime = getString(cameraInfo, "exposure_time")
            val cameraAperture = getString(cameraInfo, "aperture")
            val cameraFocalLength = getString(cameraInfo, "focal_length")
            val cameraIso = getString(cameraInfo, "iso")

            val tagsJson = jsonObjects.get("tags").asJsonArray

            val tags: ArrayList<String> = ArrayList()

            for (i in 0 until tagsJson.size()) {
                val tagJson = tagsJson[i].asJsonObject
                val title = getString(tagJson, "title")
                tags.add(title)
            }

            return PictureDetail(
                id = id,
                width = width,
                height = height,
                regularUrl = regularUrl,
                shareUrl = shareUrl,
                downloadUrl = downloadUrl,
                likes = likes,
                views = views,
                downloads = downloads,
                username = username,
                userPicture = mediumProfilePicture,
                location = locationName,
                cameraModel = cameraModel,
                cameraExposureTime = cameraExposureTime,
                cameraAperture = cameraAperture,
                cameraFocalLength = cameraFocalLength,
                cameraIso = cameraIso,
                tags = tags
            )
        } else throw Exception("json is null")

    }

    private fun getString(jsonObject: JsonObject, key: String): String {
        val jsonElement = jsonObject.get(key)
        return if (!jsonElement.isJsonNull) jsonElement.asString else ""
    }
}


