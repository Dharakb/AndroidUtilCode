package com.androidutilcode.customViews

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.androidutilcode.R

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
class MyProgressDialog(context: Context) : Dialog(context, R.style.ProgressDialog) {

    init {
        val progressBar = ProgressBar(context)
        progressBar.indeterminateDrawable.setColorFilter(
            Color.WHITE,
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        addContentView(progressBar, RelativeLayout.LayoutParams(90, 90))
    }

    fun show(
        title: CharSequence = "",
        cancelable: Boolean = false,
        cancelListener: DialogInterface.OnCancelListener? = null
    ) {
        setTitle(title)
        setCancelable(cancelable)
        setOnCancelListener(cancelListener)
        show()
    }

    companion object {

        @JvmOverloads
        fun show(
            context: Context,
            title: CharSequence,
            cancelable: Boolean = false,
            cancelListener: DialogInterface.OnCancelListener? = null
        ): MyProgressDialog {
            val dialog = MyProgressDialog(context)
            dialog.setTitle(title)

            dialog.setCancelable(cancelable)
            dialog.setOnCancelListener(cancelListener)
            /* The next line will add the ProgressBar to the dialog. */
            val progressBar = ProgressBar(context)
            progressBar.indeterminateDrawable.setColorFilter(
                Color.WHITE,
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            dialog.addContentView(progressBar, RelativeLayout.LayoutParams(90, 90))
            dialog.show()

            return dialog
        }
    }
}
