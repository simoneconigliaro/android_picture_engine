package com.simoneconigliaro.pictureengine.utils

import kotlin.math.pow

class UnitUtil {

    companion object {

        fun setUnitFocalLength(text: String?): String {
            return if (text.isNullOrBlank()) "Unknown" else "$text mm"
        }

        fun setUnitAperture(text: String?): String {
            return if (text.isNullOrBlank()) "Unknown" else "f/$text"
        }

        fun setUnitExposureTime(text: String?): String {
            return if (text.isNullOrBlank()) "Unknown" else "${text}s"
        }

        fun convertUnitNumber(text: String?): String {
            text?.let {
                val count = it.toLong()
                if (count < 1000) return "" + count
                val exp =
                    (Math.log(count.toDouble()) / Math.log(1000.0)).toInt()
                return String.format(
                    "%.1f %c",
                    count / 1000.0.pow(exp.toDouble()),
                    "kMGTPE"[exp - 1]
                )
            }
            return "0"
        }
    }
}

class TextUtil {

    companion object {

        fun isFieldBlank(text: String?): String {
            return if (text.isNullOrBlank()) "Unknown" else text
        }

        fun areFieldsBlank(text1: String?, text2: String?): String {
            return if (text1.isNullOrBlank() || text2.isNullOrBlank()) "Unknown" else "$text1 x $text2"
        }
    }
}