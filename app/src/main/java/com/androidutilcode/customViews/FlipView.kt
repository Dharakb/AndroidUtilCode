package com.androidutilcode.customViews

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.androidutilcode.R

/**
 * A quick and easy flip view through which you can create views with two sides like credit cards,
 * poker cards, flash cards etc.
 *
 *
 * Add com.wajahatkarim3.easyflipview.EasyFlipView into your XML layouts with two direct children
 * views and you are done!
 * For more information, check http://github.com/wajahatkarim3/EasyFlipView
 *
 * @author Wajahat Karim (http://wajahatkarim.com)
 * @version 1.0.1 01/11/2017
 */
class FlipView : FrameLayout {
    private val animFlipHorizontalOutId = R.animator.animation_horizontal_flip_out
    private val animFlipHorizontalInId = R.animator.animation_horizontal_flip_in
    private val animFlipHorizontalRightOutId = R.animator.animation_horizontal_right_out
    private val animFlipHorizontalRightInId = R.animator.animation_horizontal_right_in
    private val animFlipVerticalOutId = R.animator.animation_vertical_flip_out
    private val animFlipVerticalInId = R.animator.animation_vertical_flip_in
    private val animFlipVerticalFrontOutId = R.animator.animation_vertical_front_out
    private val animFlipVerticalFrontInId = R.animator.animation_vertical_flip_front_in

    enum class FlipState {
        FRONT_SIDE, BACK_SIDE
    }

    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var mSetTopOut: AnimatorSet? = null
    private var mSetBottomIn: AnimatorSet? = null
    private var mIsBackVisible = false
    private var mCardFrontLayout: View? = null
    private var mCardBackLayout: View? = null
    private var flipType: String? = "vertical"

    /**
     * Returns the flip type from direction. For horizontal, it will be either right or left and for vertical, it will be front or back.
     */
    var flipTypeFrom: String? = "right"
        private set
    /**
     * Whether view is set to flip on touch or not.
     *
     * @return true or false
     */
    /**
     * Set whether view should be flipped on touch or not!
     *
     * @param flipOnTouch value (true or false)
     */
    var isFlipOnTouch = false
    private var flipDuration = 0
    /**
     * Returns whether flip is enabled or not!
     *
     * @return true or false
     */
    /**
     * Enable / Disable flip view.
     *
     * @param flipEnabled true or false
     */
    var isFlipEnabled = false
    /**
     * Returns whether view can be flipped only once!
     *
     * @return true or false
     */
    /**
     * Enable / Disable flip only once feature.
     *
     * @param flipOnceEnabled true or false
     */
    var isFlipOnceEnabled = false
    /**
     * Returns true if Auto Flip Back is enabled
     */
    /**
     * Set if the card should be flipped back to original front side.
     * @param autoFlipBack true if card should be flipped back to froont side
     */
    var isAutoFlipBack = false
    /**
     * Return the time in milliseconds to auto flip back to original front side.
     * @return
     */
    /**
     * Set the time in milliseconds to auto flip back the view to the original front side
     * @param autoFlipBackTime The time in milliseconds
     */
    var autoFlipBackTime = 0
    private var mContext: Context?
    private val x1 = 0f
    private val y1 = 0f

    /**
     * Returns which flip state is currently on of the flip view.
     *
     * @return current state of flip view
     */
    var currentFlipState = FlipState.FRONT_SIDE
        private set
    var onFlipListener: OnFlipAnimationListener? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        this.mContext = context
        init(context, attrs)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) { // Setting Default Values
        isFlipOnTouch = true
        flipDuration = DEFAULT_FLIP_DURATION
        isFlipEnabled = true
        isFlipOnceEnabled = false
        isAutoFlipBack = false
        autoFlipBackTime = DEFAULT_AUTO_FLIP_BACK_TIME
        // Check for the attributes
        if (attrs != null) { // Attribute initialization
            val attrArray =
                context.obtainStyledAttributes(attrs, R.styleable.easy_flip_view, 0, 0)
            try {
                isFlipOnTouch = attrArray.getBoolean(R.styleable.easy_flip_view_flipOnTouch, true)
                flipDuration = attrArray.getInt(
                    R.styleable.easy_flip_view_flipDuration,
                    DEFAULT_FLIP_DURATION
                )
                isFlipEnabled = attrArray.getBoolean(R.styleable.easy_flip_view_flipEnabled, true)
                isFlipOnceEnabled =
                    attrArray.getBoolean(R.styleable.easy_flip_view_flipOnceEnabled, false)
                isAutoFlipBack =
                    attrArray.getBoolean(R.styleable.easy_flip_view_autoFlipBack, false)
                autoFlipBackTime = attrArray.getInt(
                    R.styleable.easy_flip_view_autoFlipBackTime,
                    DEFAULT_AUTO_FLIP_BACK_TIME
                )
                flipType = attrArray.getString(R.styleable.easy_flip_view_flipType)
                flipTypeFrom = attrArray.getString(R.styleable.easy_flip_view_flipFrom)
                if (TextUtils.isEmpty(flipType)) {
                    flipType = "vertical"
                }
                if (TextUtils.isEmpty(flipTypeFrom)) {
                    flipTypeFrom = "left"
                }
                //animFlipInId = attrArray.getResourceId(R.styleable.easy_flip_view_animFlipInId, R.animator.animation_horizontal_flip_in);
//animFlipOutId = attrArray.getResourceId(R.styleable.easy_flip_view_animFlipOutId, R.animator.animation_horizontal_flip_out);
            } finally {
                attrArray.recycle()
            }
        }
        loadAnimations()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        check(childCount <= 2) { "EasyFlipView can host only two direct children!" }
        findViews()
        changeCameraDistance()
    }

    override fun addView(
        v: View,
        pos: Int,
        params: ViewGroup.LayoutParams
    ) {
        check(childCount != 2) { "EasyFlipView can host only two direct children!" }
        super.addView(v, pos, params)
        findViews()
        changeCameraDistance()
    }

    override fun removeView(v: View) {
        super.removeView(v)
        findViews()
    }

    override fun removeAllViewsInLayout() {
        super.removeAllViewsInLayout()
        // Reset the state
        currentFlipState = FlipState.FRONT_SIDE
        findViews()
    }

    private fun findViews() { // Invalidation since we use this also on removeView
        mCardBackLayout = null
        mCardFrontLayout = null
        val childs = childCount
        if (childs < 1) {
            return
        }
        if (childs < 2) { // Only invalidate flip state if we have a single child
            currentFlipState = FlipState.FRONT_SIDE
            mCardFrontLayout = getChildAt(0)
        } else if (childs == 2) {
            mCardFrontLayout = getChildAt(1)
            mCardBackLayout = getChildAt(0)
        }
        if (!isFlipOnTouch) {
            mCardFrontLayout!!.visibility = View.VISIBLE
            if (mCardBackLayout != null) {
                mCardBackLayout!!.visibility = View.GONE
            }
        }
    }

    private fun loadAnimations() {
        if (flipType.equals("horizontal", ignoreCase = true)) {
            if (flipTypeFrom.equals("left", ignoreCase = true)) {
                mSetRightOut = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipHorizontalOutId
                ) as AnimatorSet
                mSetLeftIn = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipHorizontalInId
                ) as AnimatorSet
            } else {
                mSetRightOut = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipHorizontalRightOutId
                ) as AnimatorSet
                mSetLeftIn = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipHorizontalRightInId
                ) as AnimatorSet
            }
            if (mSetRightOut == null || mSetLeftIn == null) {
                throw RuntimeException(
                    "No Animations Found! Please set Flip in and Flip out animation Ids."
                )
            }
            mSetRightOut!!.removeAllListeners()
            mSetRightOut!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (currentFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout!!.visibility = View.GONE
                        mCardFrontLayout!!.visibility = View.VISIBLE
                        if (onFlipListener != null) onFlipListener!!.onViewFlipCompleted(
                            this@FlipView,
                            FlipState.FRONT_SIDE
                        )
                    } else {
                        mCardBackLayout!!.visibility = View.VISIBLE
                        mCardFrontLayout!!.visibility = View.GONE
                        if (onFlipListener != null) onFlipListener!!.onViewFlipCompleted(
                            this@FlipView,
                            FlipState.BACK_SIDE
                        )
                        // Auto Flip Back
                        if (isAutoFlipBack == true) {
                            Handler().postDelayed({ flipTheView() }, autoFlipBackTime.toLong())
                        }
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            setFlipDuration(flipDuration)
        } else {
            if (!TextUtils.isEmpty(flipTypeFrom) && flipTypeFrom.equals(
                    "front",
                    ignoreCase = true
                )
            ) {
                mSetTopOut = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipVerticalFrontOutId
                ) as AnimatorSet
                mSetBottomIn = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipVerticalFrontInId
                ) as AnimatorSet
            } else {
                mSetTopOut = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipVerticalOutId
                ) as AnimatorSet
                mSetBottomIn = AnimatorInflater.loadAnimator(
                    mContext,
                    animFlipVerticalInId
                ) as AnimatorSet
            }
            if (mSetTopOut == null || mSetBottomIn == null) {
                throw RuntimeException(
                    "No Animations Found! Please set Flip in and Flip out animation Ids."
                )
            }
            mSetTopOut!!.removeAllListeners()
            mSetTopOut!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (currentFlipState == FlipState.FRONT_SIDE) {
                        mCardBackLayout!!.visibility = View.GONE
                        mCardFrontLayout!!.visibility = View.VISIBLE
                        if (onFlipListener != null) onFlipListener!!.onViewFlipCompleted(
                            this@FlipView,
                            FlipState.FRONT_SIDE
                        )
                    } else {
                        mCardBackLayout!!.visibility = View.VISIBLE
                        mCardFrontLayout!!.visibility = View.GONE
                        if (onFlipListener != null) onFlipListener!!.onViewFlipCompleted(
                            this@FlipView,
                            FlipState.BACK_SIDE
                        )
                        // Auto Flip Back
                        if (isAutoFlipBack == true) {
                            Handler().postDelayed({ flipTheView() }, autoFlipBackTime.toLong())
                        }
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            setFlipDuration(flipDuration)
        }
    }

    private fun changeCameraDistance() {
        val distance = 8000
        val scale = resources.displayMetrics.density * distance
        if (mCardFrontLayout != null) {
            mCardFrontLayout!!.cameraDistance = scale
        }
        if (mCardBackLayout != null) {
            mCardBackLayout!!.cameraDistance = scale
        }
    }

    /**
     * Play the animation of flipping and flip the view for one side!
     */
    fun flipTheView() {
        if (!isFlipEnabled || childCount < 2) return
        if (isFlipOnceEnabled && currentFlipState == FlipState.BACK_SIDE) return
        if (flipType.equals("horizontal", ignoreCase = true)) {
            if (mSetRightOut!!.isRunning || mSetLeftIn!!.isRunning) return
            mCardBackLayout!!.visibility = View.VISIBLE
            mCardFrontLayout!!.visibility = View.VISIBLE
            if (currentFlipState == FlipState.FRONT_SIDE) { // From front to back
                mSetRightOut!!.setTarget(mCardFrontLayout)
                mSetLeftIn!!.setTarget(mCardBackLayout)
                mSetRightOut!!.start()
                mSetLeftIn!!.start()
                mIsBackVisible = true
                currentFlipState = FlipState.BACK_SIDE
            } else { // from back to front
                mSetRightOut!!.setTarget(mCardBackLayout)
                mSetLeftIn!!.setTarget(mCardFrontLayout)
                mSetRightOut!!.start()
                mSetLeftIn!!.start()
                mIsBackVisible = false
                currentFlipState = FlipState.FRONT_SIDE
            }
        } else {
            if (mSetTopOut!!.isRunning || mSetBottomIn!!.isRunning) return
            mCardBackLayout!!.visibility = View.VISIBLE
            mCardFrontLayout!!.visibility = View.VISIBLE
            if (currentFlipState == FlipState.FRONT_SIDE) { // From front to back
                mSetTopOut!!.setTarget(mCardFrontLayout)
                mSetBottomIn!!.setTarget(mCardBackLayout)
                mSetTopOut!!.start()
                mSetBottomIn!!.start()
                mIsBackVisible = true
                currentFlipState = FlipState.BACK_SIDE
            } else { // from back to front
                mSetTopOut!!.setTarget(mCardBackLayout)
                mSetBottomIn!!.setTarget(mCardFrontLayout)
                mSetTopOut!!.start()
                mSetBottomIn!!.start()
                mIsBackVisible = false
                currentFlipState = FlipState.FRONT_SIDE
            }
        }
    }

    /**
     * Flip the view for one side with or without animation.
     *
     * @param withAnimation true means flip view with animation otherwise without animation.
     */
    fun flipTheView(withAnimation: Boolean) {
        if (childCount < 2) return
        if (flipType.equals("horizontal", ignoreCase = true)) {
            if (!withAnimation) {
                mSetLeftIn!!.duration = 0
                mSetRightOut!!.duration = 0
                val oldFlipEnabled = isFlipEnabled
                isFlipEnabled = true
                flipTheView()
                mSetLeftIn!!.duration = flipDuration.toLong()
                mSetRightOut!!.duration = flipDuration.toLong()
                isFlipEnabled = oldFlipEnabled
            } else {
                flipTheView()
            }
        } else {
            if (!withAnimation) {
                mSetBottomIn!!.duration = 0
                mSetTopOut!!.duration = 0
                val oldFlipEnabled = isFlipEnabled
                isFlipEnabled = true
                flipTheView()
                mSetBottomIn!!.duration = flipDuration.toLong()
                mSetTopOut!!.duration = flipDuration.toLong()
                isFlipEnabled = oldFlipEnabled
            } else {
                flipTheView()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        flipTheView()
        return true
    }

    /**
     * Returns duration of flip in milliseconds!
     *
     * @return duration in milliseconds
     */
    fun getFlipDuration(): Int {
        return flipDuration
    }

    /**
     * Sets the flip duration (in milliseconds)
     *
     * @param flipDuration duration in milliseconds
     */
    fun setFlipDuration(flipDuration: Int) {
        this.flipDuration = flipDuration
        if (flipType.equals(
                "horizontal",
                ignoreCase = true
            )
        ) { //mSetRightOut.setDuration(flipDuration);
            mSetRightOut!!.childAnimations[0].duration = flipDuration.toLong()
            mSetRightOut!!.childAnimations[1].startDelay = flipDuration / 2.toLong()
            //mSetLeftIn.setDuration(flipDuration);
            mSetLeftIn!!.childAnimations[1].duration = flipDuration.toLong()
            mSetLeftIn!!.childAnimations[2].startDelay = flipDuration / 2.toLong()
        } else {
            mSetTopOut!!.childAnimations[0].duration = flipDuration.toLong()
            mSetTopOut!!.childAnimations[1].startDelay = flipDuration / 2.toLong()
            mSetBottomIn!!.childAnimations[1].duration = flipDuration.toLong()
            mSetBottomIn!!.childAnimations[2].startDelay = flipDuration / 2.toLong()
        }
    }

    /**
     * Returns true if the front side of flip view is visible.
     *
     * @return true if the front side of flip view is visible.
     */
    val isFrontSide: Boolean
        get() = currentFlipState == FlipState.FRONT_SIDE

    /**
     * Returns true if the back side of flip view is visible.
     *
     * @return true if the back side of flip view is visible.
     */
    val isBackSide: Boolean
        get() = currentFlipState == FlipState.BACK_SIDE

    /*
    public @AnimatorRes int getAnimFlipOutId() {
        return animFlipOutId;
    }

    public void setAnimFlipOutId(@AnimatorRes int animFlipOutId) {
        this.animFlipOutId = animFlipOutId;
        loadAnimations();
    }

    public @AnimatorRes int getAnimFlipInId() {
        return animFlipInId;
    }

    public void setAnimFlipInId(@AnimatorRes int animFlipInId) {
        this.animFlipInId = animFlipInId;
        loadAnimations();
    }
    */
    /**
     * Returns true if the Flip Type of animation is Horizontal?
     */
    val isHorizontalType: Boolean
        get() = flipType == "horizontal"

    /**
     * Returns true if the Flip Type of animation is Vertical?
     */
    val isVerticalType: Boolean
        get() = flipType == "vertical"

    /**
     * Sets the Flip Type of animation to Horizontal
     */
    fun setToHorizontalType() {
        flipType = "horizontal"
        loadAnimations()
    }

    /**
     * Sets the Flip Type of animation to Vertical
     */
    fun setToVerticalType() {
        flipType = "vertical"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to right
     */
    fun setFlipTypeFromRight() {
        flipTypeFrom = if (flipType == "horizontal") "right" else "front"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to left
     */
    fun setFlipTypeFromLeft() {
        flipTypeFrom = if (flipType == "horizontal") "left" else "back"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to front
     */
    fun setFlipTypeFromFront() {
        flipTypeFrom = if (flipType == "vertical") "front" else "right"
        loadAnimations()
    }

    /**
     * Sets the flip type from direction to back
     */
    fun setFlipTypeFromBack() {
        flipTypeFrom = if (flipType == "vertical") "back" else "left"
        loadAnimations()
    }

    /**
     * The Flip Animation Listener for animations and flipping complete listeners
     */
    interface OnFlipAnimationListener {
        /**
         * Called when flip animation is completed.
         *
         * @param newCurrentSide After animation, the new side of the view. Either can be
         * FlipState.FRONT_SIDE or FlipState.BACK_SIDE
         */
        fun onViewFlipCompleted(
            flipView: FlipView?,
            newCurrentSide: FlipState?
        )
    }

    companion object {
        val TAG = FlipView::class.java.simpleName
        const val DEFAULT_FLIP_DURATION = 400
        const val DEFAULT_AUTO_FLIP_BACK_TIME = 1000
    }
}