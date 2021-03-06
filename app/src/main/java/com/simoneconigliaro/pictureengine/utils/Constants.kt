package com.simoneconigliaro.pictureengine.utils

import com.simoneconigliaro.pictureengine.BuildConfig

object Constants {

    const val API_KEY = BuildConfig.UNSPLASH_API_KEY

    const val UNKNOWN_ERROR = "Unknown error"

    const val NETWORK_ERROR = "Network error"

    const val NETWORK_TIMEOUT = 3000L // ms (request will timeout)

    const val NETWORK_ERROR_TIMEOUT = "Network timeout"

    const val CACHE_TIMEOUT = 3000L // ms (request will timeout)

    const val CACHE_ERROR_TIMEOUT = "Cache timeout"

    const val REFRESH_CACHE_TIME = 600L // 10 mins

    const val INVALID_STATE_EVENT = "Invalid state event"

    const val UNABLE_TO_RETRIEVE_PICTURES = "Unable to retrieve list pictures"

    const val UNABLE_TO_RETRIEVE_DETAILS_PICTURE = "Unable to retrieve details picture"

    const val REFRESH = "refresh"

    const val NEXT_PAGE = "next_page"

}