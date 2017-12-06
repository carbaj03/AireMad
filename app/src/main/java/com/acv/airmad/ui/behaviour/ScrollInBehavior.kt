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

/**
 * Copyright (C) 2017 Tetsuya Masuda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class ScrollInBehavior<V : View>(context: Context, attrs: AttributeSet) : Behavior<V>(context, attrs) {
    var peekHeight: Int = 300
    var anchorPointY: Int = 600
    var currentChildY: Float = 0f
    var anchorTopMargin: Int = 0
    var statusHeight: Float = -1f

    lateinit var toolbar: Toolbar
    lateinit var title: String

    init {
        attrs.apply {
            val googleMapLikeBehaviorParam = context.obtainStyledAttributes(this, GoogleMapLikeBehaviorParam)
            peekHeight = googleMapLikeBehaviorParam.getDimensionPixelSize(GoogleMapLikeBehaviorParam_peekHeight, 0)
            anchorTopMargin = googleMapLikeBehaviorParam.getDimensionPixelSize(GoogleMapLikeBehaviorParam_anchorPoint, 0)
            googleMapLikeBehaviorParam.recycle()

            val scrollInBehaviorParam = context.obtainStyledAttributes(this, ScrollInBehaviorParam)
            title = scrollInBehaviorParam.getString(ScrollInBehaviorParam_toolbarTitle)!!
            scrollInBehaviorParam.recycle()
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
