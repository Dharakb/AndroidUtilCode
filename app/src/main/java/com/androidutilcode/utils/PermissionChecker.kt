package com.androidutilcode.utils

import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidutilcode.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class PermissionChecker {

    companion object {

        private const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123

        fun getPermissions(activity: Activity, requiredPermissions: List<Permissions>): Boolean {
            val permissionsNeeded = ArrayList<String>()

            val permissionsList = ArrayList<String>()
            for (permissions in requiredPermissions) {
                if (!addPermission(
                        activity,
                        permissionsList,
                        permissions.permissionName
                    )
                )
                    permissionsNeeded.add(permissions.permissionAction)
            }

            if (permissionsList.size > 0) {
                if (permissionsNeeded.size > 0) {
                    // Need Rationale
                    var message =
                        activity.getString(R.string.you_need_to_grant_access) + " " + permissionsNeeded[0]

                    for (i in 1 until permissionsNeeded.size)
                        message = message + ", " + permissionsNeeded[i] + "."
                    showMessageOKCancel(activity, message,
                        DialogInterface.OnClickListener { _, _ ->
                            ActivityCompat.requestPermissions(
                                activity,
                                permissionsList.toTypedArray(),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                            )
                        })
                    return false
                }
                ActivityCompat.requestPermissions(
                    activity,
                    permissionsList.toTypedArray(),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                )
                return false
            }
            return true
        }

        private fun addPermission(
            activity: Activity,
            permissionsList: MutableList<String>,
            permission: String
        ): Boolean {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsList.add(permission)
                // Check for Rationale Option
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                    return false
            }
            return true
        }

        private fun showMessageOKCancel(
            activity: Activity,
            message: String,
            okListener: DialogInterface.OnClickListener
        ) {
            MaterialAlertDialogBuilder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .create()
                .show()
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ): Boolean {
            when (requestCode) {
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                    val perms = HashMap<String, Int>()

                    // Fill with results
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]

                    for (permission in permissions)
                        if (perms[permission] != PackageManager.PERMISSION_GRANTED)
                            return false
                    return true
                }
                else -> {
                }
            }
            return false
        }
    }

}

class Permissions(val permissionName: String, val permissionAction: String)

interface PermissionsResultListener {
    fun onPermissionsResultReceived(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}