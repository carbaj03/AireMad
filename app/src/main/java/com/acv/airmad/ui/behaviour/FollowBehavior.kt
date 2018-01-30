package com.acv.airmad.ui.behaviour

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.CoordinatorLayout.Behavior
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
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
class FollowBehavior<V : View>(context: Context, attrs: AttributeSet?) : Behavior<V>(context, attrs) {
    var peekHeight = 300
    var anchorPointY = 600
    var currentChildY = 0
    var anchorTopMargin = 0

    init {
        context.obtainStyledAttributes(attrs, GoogleMapLikeBehaviorParam).apply {
            peekHeight = getDimensionPixelSize(GoogleMapLikeBehaviorParam_peekHeight, 0)
            anchorTopMargin = getDimensionPixelSize(GoogleMapLikeBehaviorParam_anchorPoint, 0)
            recycle()
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean =
            GoogleMapLikeBehavior.from(dependency) != null

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        ViewCompat.offsetTopAndBottom(child, 0)
        anchorPointY = parent.height - anchorTopMargin
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        super.onDependentViewChanged(parent, child, dependency)
        val rate = (parent.height - dependency.y - peekHeight) / (anchorPointY - peekHeight)
        currentChildY = ((parent.height + child.height) * (1f - rate)).let {
            if (it <= 0) 0 else it.toInt()
        }

        child.y = currentChildY.toFloat()
        return true
    }

}
