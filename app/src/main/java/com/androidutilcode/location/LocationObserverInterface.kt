package com.androidutilcode.location

import com.google.android.gms.location.LocationResult

interface LocationObserverInterface {
    fun addObserver(observer: LocationResultListener)
    fun removeObserver(observer: LocationResultListener)
    fun notifyObserver(locationResult: LocationResult)
}