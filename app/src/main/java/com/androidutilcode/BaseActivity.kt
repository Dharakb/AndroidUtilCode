package com.androidutilcode

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.androidutilcode.api.APIRepository
import com.androidutilcode.customViews.MyProgressDialog

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
abstract class BaseActivity : AppCompatActivity() {
    private var myProgressDialog: MyProgressDialog? = null
    lateinit var TAG: String
    val apiRepository: APIRepository by lazy {
        APIRepository()
    }

    abstract fun getTAGName(): String
    fun getProgressBar(): ProgressBar? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = getTAGName()
    }

    fun showProgressBar() {
        getProgressBar()?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        getProgressBar()?.visibility = View.GONE
    }

    fun showProgressDialog() {
        if (myProgressDialog != null) {
            myProgressDialog?.show()
        } else {
            myProgressDialog = MyProgressDialog.show(this, "", false, null)
        }
    }

    fun hideProgressDialog() {
        myProgressDialog?.dismiss()
    }

}