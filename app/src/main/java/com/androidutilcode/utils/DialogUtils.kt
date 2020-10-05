package com.androidutilcode.utils

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import com.androidutilcode.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogUtils {
    companion object {
        fun showDialog(
            context: Context,
            title: String = "",
            message: String = "",
            positiveButtonText: String = "",
            positiveClickListener: DialogInterface.OnClickListener? = null,
            negativeButtonText: String = "",
            negativeClickListener: DialogInterface.OnClickListener? = null,
            cancelable: Boolean = true,
            themeId: Int = 0
        ) {
            MaterialAlertDialogBuilder(context, themeId)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveClickListener)
                .setNegativeButton(negativeButtonText, negativeClickListener)
                .create().show()
        }

        fun showDialog(
            context: Context, title: Int, message: Int,
            positiveButtonText: Int, positiveClickListener: DialogInterface.OnClickListener? = null,
            negativeButtonText: Int, negativeClickListener: DialogInterface.OnClickListener? = null,
            cancelable: Boolean = true, themeId: Int = 0
        ) {
            MaterialAlertDialogBuilder(context, themeId)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveClickListener)
                .setNegativeButton(negativeButtonText, negativeClickListener)
                .create().show()
        }

        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun showUnauthorisedDialog(context: Context, message: String) {
            MaterialAlertDialogBuilder(context)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                    // TODO: Write your logic here
                }
                .create().show()
        }
    }
}