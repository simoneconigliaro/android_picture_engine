package com.simoneconigliaro.pictureengine.api.deserializers

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.simoneconigliaro.pictureengine.api.responses.SearchResponse
import com.simoneconigliaro.pictureengine.model.Picture
import java.lang.Exception
import java.lang.reflect.Type

class PictureSearchDeserializer : JsonDeserializer<SearchResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SearchResponse {

        if (json != null) {

            val jsonObjects = json.asJsonObject

            val results = jsonObjects.getAsJsonArray("results")

            val listPicture: ArrayList<Picture> = ArrayList()

            for(result in results) {

                val jsonObject = result.asJsonObject

                val id = getString(jsonObject, "id")

                val urls = jsonObject.get("urls").asJsonObject
                val regularUrl = getString(urls, "regular")

                val user = jsonObject.get("user").asJsonObject
                val username = getString(user, "name")

                val profileImage = user.get("profile_image").asJsonObject
                val mediumProfilePicture = getString(profileImage, "medium")

                listPicture.add(Picture(id, regularUrl, username, mediumProfilePicture))
            }

            return SearchResponse(listPicture)

        } else throw Exception("json is null")
    }

    private fun getString(jsonObject: JsonObject, key: String): String {
        val jsonElement = jsonObject.get(key)
        return if (!jsonElement.isJsonNull) jsonElement.asString else ""
    }
}
