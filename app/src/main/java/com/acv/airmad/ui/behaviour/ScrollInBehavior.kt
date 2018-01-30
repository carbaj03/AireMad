package com.acv.airmad.ui.behaviour

import android.content.Context
import android.content.res.Resources
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.CoordinatorLayout.Behavior
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import com.acv.airmad.R.attr.peekHeight
import com.acv.airmad.R.styleable.*

class ScrollInBehavior<V : View>(context: Context, attrs: AttributeSet) : Behavior<V>(context, attrs) {
    var peekHeight: Int = 300
    var anchorPointY: Int = 600
    var currentChildY: Float = 0f
    var anchorTopMargin: Int = 0
    var statusHeight: Float = -1f

    lateinit var toolbar: Toolbar
    lateinit var title: String

    init {
        (context.obtainStyledAttributes(attrs, GoogleMapLikeBehaviorParam)).apply {
            peekHeight = getDimensionPixelSize(GoogleMapLikeBehaviorParam_peekHeight, 0)
            anchorTopMargin = getDimensionPixelSize(GoogleMapLikeBehaviorParam_anchorPoint, 0)
            recycle()
        }

        (context.obtainStyledAttributes(attrs, ScrollInBehaviorParam)).apply {
            title = getString(ScrollInBehaviorParam_toolbarTitle)!!
            recycle()
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean =
            GoogleMapLikeBehavior.from(dependency) != null

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        anchorPointY = parent.height - anchorTopMargin
        toolbar = (child as AppBarLayout).getToolbar()
        if (statusHeight < 0f)
            statusHeight = parent.resources.getStatusBarHeight().toFloat()
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        super.onDependentViewChanged(parent, child, dependency)
        val rate = (dependency.y - anchorTopMargin) / (parent.height - anchorTopMargin - peekHeight)

        currentChildY = with(child) { -((height + paddingTop + paddingBottom + top + bottom) * (rate)) }
        if (currentChildY <= 0) {
            child.y = currentChildY + statusHeight
        } else {
            child.y = 0f + statusHeight
            currentChildY = 0 + statusHeight
        }

        val drawable = child.background.mutate()
        val bounds = drawable.bounds
        var heightRate = (bounds.bottom * 2 - dependency.y) / (bounds.bottom) - 1f

        heightRate = when {
            heightRate > 1f -> 1f
            heightRate < 0f -> 0f
            else -> heightRate
        }

        toolbar.title = if (heightRate >= 1f) title else ""

        drawable.setBounds(0, (bounds.bottom - bounds.bottom * heightRate).toInt(), bounds.right, bounds.bottom)
        child.background = drawable
        return true
    }
}
