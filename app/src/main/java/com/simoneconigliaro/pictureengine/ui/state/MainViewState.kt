package com.simoneconigliaro.pictureengine.ui.state

import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.model.Picture


data class MainViewState(
    var listFragmentViews: ListFragmentViews = ListFragmentViews(),
    var detailFragmentViews: DetailFragmentViews = DetailFragmentViews()
) {

    data class ListFragmentViews(
        var listPictures: List<Picture>? = null
    )

    data class DetailFragmentViews(
        var pictureDetail: PictureDetail? = null
    )
}