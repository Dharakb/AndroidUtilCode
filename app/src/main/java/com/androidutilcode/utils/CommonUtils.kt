package com.androidutilcode.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
class CommonUtils {
    companion object {
        private const val EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        private const val OTP_REGEX = "\\A[0-9]{4}\\z"
        private const val PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$"
        private const val MOBILE_NUMBER_REGEX = "([0-9]{10})"
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

        private var pattern: Pattern? = null
        private var matcher: Matcher? = null

        /**
         * Validate Email with regular expression
         *
         * @param email
         * @return true for Valid Email and false for Invalid Email
         */
        fun validateEmail(email: String?): Boolean {
            return validate(email, EMAIL_REGEX)
        }

        fun validatePassword(password: String?): Boolean {
            return validate(password, PASSWORD_REGEX)
        }

        fun validateOTP(otp: String?): Boolean {
            return validate(otp, OTP_REGEX)
        }

        fun validateMobileNumber(mobileNumber: String?): Boolean {
            return validate(mobileNumber, MOBILE_NUMBER_REGEX)
        }

        private fun validate(field: String?, regEx: String): Boolean {
            if (field.isNullOrBlank())
                return false
            pattern = Pattern.compile(regEx)
            matcher = pattern?.matcher(field)
            return matcher?.matches() ?: false
        }

        fun showSoftKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }

        fun hideSoftKeyboard(context: Context) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        // don't work for dialog
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun getFormattedPriceToShow(price: Double): String {
            val formatter: NumberFormat = DecimalFormat("##.##")
            val locale = Locale("en", "KW")
            /*val locale = Locale(
                getLanguage_code(),
                getCountry_code()
            )*/
            val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
            val formattedPrice = currencyFormatter.format(price)

            //formattedPrice = formatter.format(price) + " " + current_theme.getCurrency();
            return formattedPrice ?: price.toString()
        }

        fun resolveOrThrow(context: Context, @AttrRes attributeResId: Int): Int {
            val typedValue = TypedValue()
            if (context.theme.resolveAttribute(attributeResId, typedValue, true)) {
                return typedValue.data
            }
            throw IllegalArgumentException(
                context.resources.getResourceName(
                    attributeResId
                )
            )
        }

        fun getPrivateAlbumStorageDir(context: Context): String {
            val file =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Photos")
            if (!file.mkdirs()) {
                Log.e("Directory", "Root Directory not created")
            }
            return file.absolutePath
        }

        fun getPrivateDownloadsDir(context: Context): String {
            val file =
                File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Themes")
            if (!file.mkdirs()) {
                Log.e("Directory", "Root Directory not created")
            }
            return file.absolutePath
        }

        fun isColorDark(color: Int): Boolean {
            val darkness: Double =
                1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(
                    color
                )) / 255
            return darkness >= 0.5
        }

        fun isDateNearExpiryDate(date1: Date?): Boolean {
            return (date1?.time?.minus(Date().time))?.div((24 * 60 * 60 * 1000))!! <= 14
        }

        fun getDateFromString(dateFormat: String, date: String): Date? {
            val dateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            return dateFormat.parse(date)
        }

        fun getStringFromDate(dateFormat: String, date: Date): String {
            val dateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            return dateFormat.format(date)
        }

        fun getUTCDateTimeAsDate(): Date? { // note: doesn't check for null
            return stringDateToDate(getUTCDateTimeAsString())
        }

        fun getUTCDateTimeAsString(): String? {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        }

        fun stringDateToDate(StrDate: String?, dateFormat: String = DATE_FORMAT): Date? {
            var dateToReturn: Date? = null
            val dateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            try {
                dateToReturn = dateFormat.parse(StrDate) as Date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return dateToReturn
        }
    }
}