package com.simoneconigliaro.pictureengine.api.responses

import com.simoneconigliaro.pictureengine.model.Picture


data class SearchResponse(

    var listPictures: ArrayList<Picture>? = null

)