package com.simoneconigliaro.pictureengine.ui.state

import com.simoneconigliaro.pictureengine.model.Picture


data class MainViewState(

    var listFragmentViews: ListFragmentViews = ListFragmentViews()

) {

    data class ListFragmentViews(
        var listPictures: List<Picture>? = null
    )
}