package com.androidutilcode.location

import com.google.android.gms.location.LocationResult

interface LocationResultListener {
    fun onLocationResultReceived(locationResult: LocationResult)
}