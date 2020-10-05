package com.androidutilcode.customViews

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 * Created by Dharak Bhatt on 12/12/19.
 * @author Dharak Bhatt
 */
class CarouselLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {
    private val mShrinkAmount = 0.15f
    private val mShrinkDistance = 0.9f

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: RecyclerView.State?): Int {
        val orientation = orientation
        return if (orientation == VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
            val midpoint = height / 2f
            val d0 = 0f
            val d1 = mShrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - mShrinkAmount
            for (i in 0 until childCount) {
                val child: View? = getChildAt(i)
                child?.let { child ->
                    val childMidpoint =
                        (getDecoratedBottom(child) + getDecoratedTop(child)) / 2f
                    val d =
                        Math.min(d1, Math.abs(midpoint - childMidpoint))
                    val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                    child.scaleX = scale
                    child.scaleY = scale
                }
            }
            scrolled
        } else {
            0
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: Recycler?,
        state: RecyclerView.State?
    ): Int {
        val orientation = orientation
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            val midpoint = width / 2f
            val d0 = 0f
            val d1 = mShrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - mShrinkAmount

//            Log.d("CarouselLAyoutManager","List count "+childCount)
//            Log.d("CarouselLAyoutManager","isHorizontalLayoutInit "+ isHorizontalLayoutInit)

//                if (childCount == 1) {
//                    if(!isHorizontalLayoutInit) {
//                        isHorizontalLayoutInit = true
//                    val child: View? = getChildAt(0)
//                    getOriginalViewWidth = child?.width?:0
//                    val params = child?.layoutParams
//                    params?.width = ConstraintLayout.LayoutParams.MATCH_PARENT
//                    child?.layoutParams = params
//                    }
//                }

            for (i in 0 until childCount) {
                val child: View? = getChildAt(i)

                child?.let { child ->
//                    if(childCount >1 && isHorizontalLayoutInit){
//                        if(child.width>getOriginalViewWidth) {
//                            val params = child.layoutParams
//                            params?.width = getOriginalViewWidth
//                            child.layoutParams = params
////                            Log.d(
////                                "CarouselLAyoutManager",
////                                "Width changed to " + getOriginalViewWidth
////                            )
//                            isHorizontalLayoutInit =false
//                        }
//                    }
                    val childMidpoint =
                        (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f
                    val d =
                        Math.min(d1, Math.abs(midpoint - childMidpoint))
                    val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                    child.scaleX = scale
                    child.scaleY = scale
                }
            }
            scrolled
        } else {
            0
        }
    }

    override fun onLayoutChildren(recycler: Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        scrollHorizontallyBy(0, recycler, state)
    }
}