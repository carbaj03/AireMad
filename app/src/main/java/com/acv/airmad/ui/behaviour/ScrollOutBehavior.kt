package com.acv.airmad.ui.behaviour

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.acv.airmad.R.styleable.*

class ScrollOutBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.ScrollingViewBehavior(context, attrs) {
    var peekHeight = 300
    var anchorPointY = 600
    var anchorTopMargin = 0

    lateinit var toolbar: Toolbar
    private var statusHeight: Float = -1f

    init {
        Log.e("init", "out")
        (context.obtainStyledAttributes(attrs, GoogleMapLikeBehaviorParam)).apply {
            peekHeight = getDimensionPixelSize(GoogleMapLikeBehaviorParam_peekHeight, 0)
            anchorTopMargin = getDimensionPixelSize(GoogleMapLikeBehaviorParam_anchorPoint, 0)
            recycle()
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean =
            GoogleMapLikeBehavior.from(dependency) != null

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        Log.e("onLayoutChild", "out")
        anchorPointY = parent.height - anchorTopMargin
        toolbar = (child as AppBarLayout).getToolbar()
        if (statusHeight < 0f)
            statusHeight = parent.resources.getStatusBarHeight().toFloat()
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        super.onDependentViewChanged(parent, child, dependency)
        Log.e("dependentView", "out")
        child.apply {
            val rate = (parent.height - dependency.y - peekHeight) / (anchorTopMargin)
            val currentChildY = -((height + paddingTop + paddingBottom + top + bottom) * (rate)).toInt()

            y = if (currentChildY <= 0) currentChildY.toFloat() + statusHeight else statusHeight
        }
        toolbar.title = "sfas fasfa"
        return true
    }
}
