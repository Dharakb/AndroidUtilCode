package com.androidutilcode.location

import android.content.Intent

interface GPSEnableListener {
    fun onGPSStateReceived(requestCode: Int, resultCode: Int, data: Intent?)
}


