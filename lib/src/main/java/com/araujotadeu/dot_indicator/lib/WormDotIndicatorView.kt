package com.araujotadeu.dot_indicator.lib

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.abs

class WormDotIndicatorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), DotIndicatorListener {

    var dotColor: Int = Color.BLACK
    var dotSize: Int = 0
    var dotSpace: Int = 0
    var dotAlphaSelected: Float = 1.0f
    var dotAlphaNotSelected: Float = 0.5f

    var selectedView: View = View(context)

    private val helper = DotIndicatorHelper(resources, this)

    var currentPosition = -1
        private set

    init {
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
                    background = helper.createRoundRectDrawable(dotColor, dotSize)
                    layoutParams = LayoutParams(dotSize, dotSize).apply {
                        marginStart = dotSpace * i + dotSize * i
                    }
                })
            }

            selectedView = View(context).apply {
                alpha = dotAlphaSelected
                background = helper.createRoundRectDrawable(dotColor, dotSize)
                layoutParams = LayoutParams(dotSize, dotSize)
                visibility = INVISIBLE
            }
            addView(selectedView)


        }
    }

    override fun setCurrent(position: Int) {
        if (position >= childCount && position >= 0) return

        takeIf { currentPosition != position }?.apply {

            if (currentPosition < 0) {
                val view = getChildAt(position)
                selectedView.updateLayoutParams<LayoutParams> {
                    marginStart = (view.layoutParams as LayoutParams).marginStart
                }
                selectedView.apply {
                    visibility = VISIBLE
                }
            } else {

                if (position > currentPosition) { // Move forward
                    val steps = kotlin.math.abs(currentPosition - position)

                    val finalSize = (dotSize * steps + dotSpace * steps) + dotSize

                    val anim = ValueAnimator.ofInt(dotSize, finalSize)
                    anim.addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int
                        selectedView.updateLayoutParams<LayoutParams> {
                            width = value
                        }
                    }
                    anim.duration = 200

                    val anim2 = ValueAnimator.ofInt(finalSize, dotSize)
                    anim2.addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int
                        selectedView.updateLayoutParams<LayoutParams> {
                            width = value
                            marginStart = abs(finalSize - value)
                        }
                    }
                    anim2.duration = 200

                    val set = AnimatorSet()
                    set.playSequentially(anim, anim2)
                    set.start()

                } else { // Move backward

                    val steps = kotlin.math.abs(currentPosition - position)

                    val finalSize = (dotSize * steps + dotSpace * steps) + dotSize

                    val anim = ValueAnimator.ofInt(dotSize, finalSize)
                    anim.addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int
                        selectedView.updateLayoutParams<LayoutParams> {
                            width = value
                            marginStart = abs(finalSize - value)
                        }
                    }
                    anim.duration = 200

                    val anim2 = ValueAnimator.ofInt(finalSize, dotSize)
                    anim2.addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int
                        selectedView.updateLayoutParams<LayoutParams> {
                            width = value
                        }
                    }
                    anim2.duration = 200

                    val set = AnimatorSet()
                    set.playSequentially(anim, anim2)
                    set.start()

                }
            }

            currentPosition = position

        }
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        helper.attachRecyclerView(recyclerView)
    }
}
