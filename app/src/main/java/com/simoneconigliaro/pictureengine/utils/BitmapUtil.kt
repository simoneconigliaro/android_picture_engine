package com.simoneconigliaro.pictureengine.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class BitmapUtil {

    companion object {

        suspend fun downloadBitmapFromUrl(imageUrl: String?) = withContext(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            imageUrl?.let {
                val url = URL(imageUrl)
                bitmap = BitmapFactory.decodeStream(url.openStream())
            }
            bitmap
        }
    }
}