package com.androidutilcode.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
class NonSwipeableViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private val enabled: Boolean = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (enabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return if (enabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

}