package com.araujotadeu.dot_indicator.lib

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class ExpandedDotIndicatorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), DotIndicatorListener {

    var dotColor: Int = Color.BLACK
    var dotSize: Int = 0
    var dotSpace: Int = 0
    var dotAlphaSelected: Float = 1.0f
    var dotAlphaNotSelected: Float = 0.5f
    var dotExpandFraction: Float = 1.5f
    // TODO expand X and Y, or only Y, or only X

    private val helper = DotIndicatorHelper(resources, this)

    var currentPosition = -1
        private set

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        if (isInEditMode) {
            dotSize = 100
            dotSize = 20
            setDots(3)
        }

        attrs?.let {
            applyAttrs(it)
        }
    }

    private fun applyAttrs(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotIndicatorView)

        dotColor = typedArray.getColor(R.styleable.DotIndicatorView_dotColor, Color.BLACK)
        dotSize = typedArray.getDimensionPixelSize(R.styleable.DotIndicatorView_dotSize, 0)
        dotSpace = typedArray.getDimensionPixelSize(R.styleable.DotIndicatorView_dotSpace, 0)
        dotAlphaSelected = typedArray.getFloat(R.styleable.DotIndicatorView_dotAlphaSelected, 1.0f)
        dotAlphaNotSelected = typedArray.getFloat(R.styleable.DotIndicatorView_dotAlphaNotSelected, 0.5f)
        dotExpandFraction = typedArray.getFloat(R.styleable.DotIndicatorView_dotExpandFraction, 1.5f)

        val count = typedArray.getInt(R.styleable.DotIndicatorView_dotCount, 0)
        setDots(count)

        typedArray.recycle()
    }

    override fun setDots(count: Int) {
        removeAllViews()
        takeIf { count > 0 }?.apply {

            for (i in 0 until count) {
                addView(View(context).apply {
                    alpha = dotAlphaNotSelected
                    //background = helper.createRoundRectDrawable(dotColor, dotSize)
                    background = helper.createOvalDrawable(dotColor)
                    layoutParams = LayoutParams(dotSize, dotSize).apply {
                        setMargins(dotSpace, 0, dotSpace, 0)
                    }
                })
            }
        }
    }

    override fun setCurrent(position: Int) {
        if (position >= childCount && position >= 0) return

        takeIf { currentPosition != position }?.apply {

            val list: MutableList<ValueAnimator> = mutableListOf()
            val expandedSize = (dotSize * dotExpandFraction).roundToInt()
            if (currentPosition >= 0) {
                list.add(helper.createResizeAnimation(getChildAt(currentPosition), expandedSize, dotSize))
                list.add(helper.createAlphaAnimation(getChildAt(currentPosition), dotAlphaSelected, dotAlphaNotSelected))
            }
            list.add(helper.createResizeAnimation(getChildAt(position), dotSize, expandedSize))
            list.add(helper.createAlphaAnimation(getChildAt(position), dotAlphaNotSelected, dotAlphaSelected))
            val set = AnimatorSet()
            set.playTogether(list as Collection<Animator>?)
            set.start()

            currentPosition = position

        }
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        helper.attachRecyclerView(recyclerView)
    }
}
