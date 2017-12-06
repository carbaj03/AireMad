package com.acv.airmad.ui.behaviour

import android.content.res.Resources
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.Toolbar

fun AppBarLayout.getToolbar(): Toolbar =
        (0 until this.childCount).map { view ->
            this.getChildAt(view)
        }.find {
            it is Toolbar
        }.let { it as Toolbar }

fun Resources.getStatusBarHeight(): Int {
    val identifier = getIdentifier("status_bar_height", "dimen", "android")
    return when {
        identifier > 0 -> getDimensionPixelSize(identifier)
        else -> 0
    }
}