package com.androidutilcode.utils

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Created by Dharak Bhatt on 17/12/19.
 * @author Dharak Bhatt
 */

class RVDividerItemDecorator(
    divider: Drawable?,
    removeLeftRightPadding: Boolean = false,
    private val showDividerForLastItem: Boolean = false
) : ItemDecoration() {
    private val mDivider: Drawable? = divider
    private val removePadding = removeLeftRightPadding
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mDivider == null)
            return
        val dividerLeft = if (!removePadding)
            parent.paddingLeft
        else
            0

        val dividerRight = if (!removePadding)
            parent.width - parent.paddingRight
        else
            parent.width

        val childCount = if (!showDividerForLastItem)
            parent.childCount - 2
        else
            parent.childCount - 1

        for (i in 0..childCount) {
            val child: View = parent.getChildAt(i)
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val dividerTop: Int = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + mDivider.intrinsicHeight
            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider.draw(canvas)
        }
    }

}