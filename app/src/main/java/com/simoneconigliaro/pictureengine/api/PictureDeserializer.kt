package com.simoneconigliaro.pictureengine.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.simoneconigliaro.pictureengine.model.Picture
import java.lang.Exception
import java.lang.reflect.Type

class PictureDeserializer : JsonDeserializer<Picture> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Picture {

        if (json != null) {

            val jsonObjects = json.asJsonObject

            val id = jsonObjects.get("id").asString

            val urls = jsonObjects.get("urls").asJsonObject
            val regularUrl = urls.get("regular").asString

            val user = jsonObjects.get("user").asJsonObject
            val username = user.get("username").asString

            val profileImage = user.get("profile_image").asJsonObject
            val smallProfilePicture = profileImage.get("small").asString

            val picture = Picture(id, regularUrl, username, smallProfilePicture)

            return picture

        } else throw Exception("json is null")

    }


}