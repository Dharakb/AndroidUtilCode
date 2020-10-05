package com.androidutilcode.utils

import android.graphics.Bitmap
import com.squareup.picasso.Transformation

class BitmapUtils {

    companion object {
        const val SIZE_THUMBNAIL = 300

        fun getTransformation(targetWidth: Int): Transformation {
            return object : Transformation {

                override fun transform(source: Bitmap): Bitmap {
                    val aspectRatio = source.height.toDouble() / source.width.toDouble()
                    val targetHeight = (targetWidth * aspectRatio).toInt()
                    val result: Bitmap
                    if (source.width <= targetWidth)
                        result = source
                    else
                        result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle()
                    }
                    return result
                }

                override fun key(): String {
                    return "cropPosterTransformation $targetWidth"
                }
            }
        }
    }
}