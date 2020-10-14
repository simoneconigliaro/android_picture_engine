package com.simoneconigliaro.pictureengine.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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

            val id = getString(jsonObjects, "id")

            val urls = jsonObjects.get("urls").asJsonObject
            val regularUrl = getString(urls, "regular")

            val user = jsonObjects.get("user").asJsonObject
            val username = getString(user, "name")

            val profileImage = user.get("profile_image").asJsonObject
            val mediumProfilePicture = getString(profileImage, "medium")

            return Picture(id, regularUrl, username, mediumProfilePicture)

        } else throw Exception("json is null")

    }

    private fun getString(jsonObject: JsonObject, key: String): String {
        val jsonElement = jsonObject.get(key)
        return if (!jsonElement.isJsonNull) jsonElement.asString else ""
    }
}
