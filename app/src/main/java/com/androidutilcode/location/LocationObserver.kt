package com.androidutilcode.location

import com.google.android.gms.location.LocationResult

class LocationObserver : LocationObserverInterface {

    private var observerList: ArrayList<LocationResultListener> = ArrayList()

    override fun addObserver(observer: LocationResultListener) {
        observerList.add(observer)
    }

    override fun removeObserver(observer: LocationResultListener) {
        observerList.remove(observer)
    }

    override fun notifyObserver(locationResult: LocationResult) {
        for (observer in observerList)
            observer.onLocationResultReceived(locationResult)
    }

    companion object {
        private var locationObserverInstance: LocationObserver? = null

        fun getInstance(): LocationObserver {
            if (locationObserverInstance == null)
                locationObserverInstance = LocationObserver()

            return locationObserverInstance as LocationObserver
        }
    }
}