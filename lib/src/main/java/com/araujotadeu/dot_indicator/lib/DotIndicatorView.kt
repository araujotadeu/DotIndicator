package com.araujotadeu.dot_indicator.lib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class DotIndicatorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), DotIndicatorListener {

    var selectedColor: Int = Color.BLACK
    var notSelectedColor: Int = Color.WHITE
    var dotSize: Int = 0
    var dotSpace: Int = 0

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

        selectedColor = typedArray.getColor(R.styleable.DotIndicatorView_selectedColor, Color.BLACK)
        notSelectedColor = typedArray.getColor(R.styleable.DotIndicatorView_notSelectedColor, Color.WHITE)
        dotSize = typedArray.getDimensionPixelSize(R.styleable.DotIndicatorView_dotSize, 0)
        dotSpace = typedArray.getDimensionPixelSize(R.styleable.DotIndicatorView_dotSpace, 0)

        val count = typedArray.getInt(R.styleable.DotIndicatorView_dotCount, 0)
        setDots(count)

        typedArray.recycle()
    }

    override fun setDots(count: Int) {
        removeAllViews()
        takeIf { count > 0 }?.apply {

            for (i in 0 until count) {
                addView(View(context).apply {
                    background = helper.createDrawable(notSelectedColor, dotSize)
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

            if (currentPosition >= 0) {
                helper.executeColorTransitionAnimation(getChildAt(currentPosition), helper.createDrawable(selectedColor, dotSize), helper.createDrawable(notSelectedColor, dotSize))
            }
            helper.executeColorTransitionAnimation(getChildAt(position), helper.createDrawable(notSelectedColor, dotSize), helper.createDrawable(selectedColor, dotSize))

            currentPosition = position

        }
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        helper.attachRecyclerView(recyclerView)
    }
}
