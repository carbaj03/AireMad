package com.acv.airmad.ui.behaviour

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.Log
import android.view.View
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
class ScrollOutBehavior(context: Context, attrs: AttributeSet?) : AppBarLayout.ScrollingViewBehavior(context, attrs) {
    var peekHeight = 300
    var anchorPointY = 600
    var anchorTopMargin = 0

    lateinit var toolbar: Toolbar
    var statusHeight: Float = -1f

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
