@file:Suppress("UNCHECKED_CAST")

package com.androidutilcode.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Dharak Bhatt on 26/4/18.
 */
object PreferenceManager {
    private var pref: SharedPreferences? = null

    private val editor: SharedPreferences.Editor
        get() = pref!!.edit()

    fun init(context: Context) {
        if (pref == null) {
            pref = context.getSharedPreferences("CommonPreferences", Context.MODE_PRIVATE)
        } else {
            throw RuntimeException("Preference already initialized")
        }
    }

    fun delete(key: String) {
        if (pref!!.contains(key)) {
            editor.remove(key).apply()
        }
    }

    fun clear() {
        editor.clear().apply()
    }

    fun <T> getPref(key: String): T? {
        return pref!!.all[key] as T?
    }

    fun <T> getPref(key: String, defValue: T): T {
        val returnValue = pref!!.all[key] as T?
        return returnValue ?: defValue
    }

    fun savePref(key: String, value: Any?) {
        val editor = editor

        if (value is Boolean) {
            editor.putBoolean(key, (value as Boolean?)!!)
        } else if (value is Int) {
            editor.putInt(key, (value as Int?)!!)
        } else if (value is Float) {
            editor.putFloat(key, (value as Float?)!!)
        } else if (value is Long) {
            editor.putLong(key, (value as Long?)!!)
        } else if (value is String) {
            editor.putString(key, value as String?)
        } else if (value is Enum<*>) {
            editor.putString(key, value.toString())
        } else if (value != null) {
            throw RuntimeException("Attempting to save non-primitive preference")
        }

        editor.apply()
    }
}
