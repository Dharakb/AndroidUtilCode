package com.androidutilcode.customViews

import android.content.Context
import android.widget.Scroller

class FixedViewPagerSpeedScroller(context: Context?) : Scroller(context) {

    private val mDuration = 2000

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration)
    }
}