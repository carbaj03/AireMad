package com.acv.airmad.ui.behaviour

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class BottomNavigationBehavior<V : View>(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<V>(context, attrs) {
    companion object {
        val SCROLL_UP = 1
        val SCROLL_DOWN = -1
        private val ANIMATION_DURATION = 300L

        @SuppressWarnings("unchecked")
        fun <V : View> from(view: V?): BottomNavigationBehavior<V>? {
            if (view == null) return null
            val params = view.layoutParams as? CoordinatorLayout.LayoutParams ?: throw IllegalArgumentException(
                    "The view is not a child of CoordinatorLayout")
            return params.behavior as? BottomNavigationBehavior<V>
        }
    }

    private var animating = false
    private var animatingDirection = 0

    private val animationListener = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            animating = true
        }

        override fun onAnimationCancel(animation: Animator) {
            animating = true
        }

        override fun onAnimationEnd(animation: Animator) {
            animating = false
        }
    }

    private lateinit var scrollOutAnimator: ObjectAnimator
    private lateinit var scrollInAnimator: ObjectAnimator

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val parentHeight = parent.height
        parent.onLayoutChild(child, layoutDirection)
        ViewCompat.offsetTopAndBottom(child, parentHeight - child.height)
        scrollOutAnimator = ObjectAnimator.ofFloat(child, "translationY", 0f,
                child.height.toFloat()).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(animationListener)
        }
        scrollInAnimator = ObjectAnimator.ofFloat(child, "translationY",
                child.height.toFloat(), 0f).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addListener(animationListener)
        }
        return true
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean
            = nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        if (animating || dyConsumed == 0) return

        val direction = if (dyConsumed > 0) SCROLL_DOWN else SCROLL_UP

        if (animatingDirection == direction) return
        animatingDirection = direction
        if (direction == SCROLL_DOWN) scrollOutAnimator.start() else scrollInAnimator.start()
    }

}
