package com.androidutilcode.location

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidutilcode.utils.PermissionChecker
import com.androidutilcode.utils.Permissions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.util.*

open class LocationHelper(private var activity: Activity?) : GPSEnableListener {

    private val GPS_ENABLE_CODE = 500

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private var mLocationCallback: LocationCallback? = null
    private var onCancelButtonPressedListener: OnCancelButtonPressedListener? = null
    private fun displayLocationSettingsRequest() {
        val googleApiClient: GoogleApiClient? =
            activity?.let { GoogleApiClient.Builder(it).addApi(LocationServices.API).build() }
        googleApiClient?.connect()

        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2

        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setNeedBle(true)

        val result = activity?.let {
            LocationServices.getSettingsClient(it).checkLocationSettings(builder.build())
        }
        result?.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                onLocationResultReceive()
                checkLocationServiceStatus()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        try {
                            val resolvable = exception as ResolvableApiException
                            resolvable.startResolutionForResult(activity, GPS_ENABLE_CODE)
                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        } catch (e: ClassCastException) {
                            e.printStackTrace()
                        }
                    else -> exception.printStackTrace()
                }
            }
        }
    }

    private fun checkLocationServiceStatus() {
        try {
            mFusedLocationClient =
                activity?.let { LocationServices.getFusedLocationProviderClient(it) }

            mLocationRequest = LocationRequest.create()
            mLocationRequest.interval = 5000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.smallestDisplacement = 0.0f
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    locationResult?.let { LocationObserver.getInstance().notifyObserver(it) }
                    getCurrentLocation(locationResult)
                }
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback!!, null)
    }

    fun stopLocationUpdate() {
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requiredPermissions = ArrayList<Permissions>()

            activity?.getString(com.androidutilcode.R.string.location)?.let {
                Permissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    it
                )
            }?.let {
                requiredPermissions.add(
                    it
                )
            }

            if (activity?.let {
                    PermissionChecker.getPermissions(
                        it,
                        requiredPermissions
                    )
                } == true) {
                displayLocationSettingsRequest()
            }
        } else {
            displayLocationSettingsRequest()
        }
    }

    fun setCancelButtonListener(onCancelButtonPressedListener: OnCancelButtonPressedListener) {
        this.onCancelButtonPressedListener = onCancelButtonPressedListener
    }

    override fun onGPSStateReceived(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && GPS_ENABLE_CODE == 100) {
            checkLocationServiceStatus()
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == 100) {
            Log.d("dialog", "from LocationHelper")

            val builder = AlertDialog.Builder(activity)
            builder.setMessage(com.androidutilcode.R.string.enable_gps_required)
                .setPositiveButton(activity?.getString(com.androidutilcode.R.string.turn_on)) { dialog, which ->
                    dialog.dismiss()
                    displayLocationSettingsRequest()
                }
            builder.setCancelable(false)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            activity?.let {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        it,
                        com.androidutilcode.R.color.colorBlack
                    )
                )
            }
        }
    }

    open fun getCurrentLocation(locationResult: LocationResult?) {
    }

    open fun onLocationResultReceive() {

    }

}