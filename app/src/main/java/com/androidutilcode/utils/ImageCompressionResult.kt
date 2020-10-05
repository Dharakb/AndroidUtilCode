package com.androidutilcode.utils

import android.graphics.Bitmap

interface ImageCompressionResult {
    fun onImageCompressionReady(path: String, bitmap: Bitmap)
}