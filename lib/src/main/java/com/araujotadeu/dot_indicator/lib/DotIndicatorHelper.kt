package com.araujotadeu.dot_indicator.lib

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.TransitionDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class DotIndicatorHelper(resources : Resources, val listener : DotIndicatorListener) {

    private val animationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    private var scrollListener: RecyclerView.OnScrollListener? = null

    fun createDrawable(color: Int, dotSize : Int): Drawable {
        val radius = dotSize / 2f
        val radiusArray = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val shapeDrawable = ShapeDrawable(RoundRectShape(radiusArray, null, null))
        shapeDrawable.paint.color = color
        return shapeDrawable
    }

    fun createResizeAnimation(view: View, from: Int, to: Int): ValueAnimator {
        val anim = ValueAnimator.ofInt(from, to)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.width = value
            view.layoutParams = layoutParams
        }
        anim.duration = animationDuration.toLong()
        return anim
    }

    fun createAlphaAnimation(view: View, from: Float, to: Float): ValueAnimator {
        val anim = ValueAnimator.ofFloat(from, to)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            view.alpha = value
        }
        anim.duration = animationDuration.toLong()
        return anim
    }

    fun executeColorTransitionAnimation(view: View, from: Drawable, to: Drawable) {
        val trans = TransitionDrawable(arrayOf(from, to))
        view.background = trans
        trans.startTransition(animationDuration)
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        takeIf { recyclerView.layoutManager is LinearLayoutManager }?.apply {

            scrollListener?.let {
                recyclerView.removeOnScrollListener(it)
            }

            scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstPos = layoutManager.findFirstCompletelyVisibleItemPosition()
                    listener.setCurrent(firstPos)
                }
            }

            recyclerView.adapter?.let {
                listener.setDots(it.itemCount)
            }
        }
    }

    fun createMoveXAnimation(view: View?, from: Int, to: Int): ValueAnimator {
        val anim = ValueAnimator.ofInt(from, to)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams: FrameLayout.LayoutParams = (view?.layoutParams as FrameLayout.LayoutParams)
            layoutParams.leftMargin = value
            view.layoutParams = layoutParams
        }
        anim.duration = animationDuration.toLong()
        return anim
    }

    // TODO attachViewPager
}
