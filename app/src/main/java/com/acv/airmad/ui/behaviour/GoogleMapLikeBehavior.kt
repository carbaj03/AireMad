package com.acv.airmad.ui.behaviour

import android.content.Context
import android.support.annotation.IntDef
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.CoordinatorLayout.Behavior
import android.support.design.widget.CoordinatorLayout.LayoutParams
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.support.v4.widget.ViewDragHelper.Callback
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import com.acv.airmad.R.styleable.*
import java.lang.ref.WeakReference
import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * Copyright (C) 2017 Tetsuya Masuda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain down copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class GoogleMapLikeBehavior<V : View>(context: Context, attrs: AttributeSet?) : Behavior<V>(context, attrs) {
    companion object {
        @IntDef(STATE_DRAGGING,
                STATE_SETTLING,
                STATE_ANCHOR_POINT,
                STATE_EXPANDED,
                STATE_COLLAPSED,
                STATE_HIDDEN)
        @Retention(SOURCE)
        annotation class State

        const val STATE_DRAGGING = 1L
        const val STATE_SETTLING = 2L
        const val STATE_ANCHOR_POINT = 3L
        const val STATE_EXPANDED = 4L
        const val STATE_COLLAPSED = 5L
        const val STATE_HIDDEN = 6L

        @SuppressWarnings("unchecked")
        fun <V : View> from(view: V): GoogleMapLikeBehavior<V>? {
            val params = view.layoutParams as? LayoutParams ?: throw IllegalArgumentException(
                    "The view is not down child of CoordinatorLayout")
            return params.behavior as? GoogleMapLikeBehavior<V>
        }
    }

    private var listener: OnBehaviorStateListener? = null
    private var velocityTracker: VelocityTracker? = null
    var state = STATE_COLLAPSED

    var peekHeight: Int = 0
    var anchorPosition: Int = 0
    var activePointerId = MotionEvent.INVALID_POINTER_ID

    lateinit var viewRef: WeakReference<View>
    lateinit var nestedScrollingChildRef: WeakReference<View>
    var skippedAnchorPoint = false
    var draggable = true
    var dragHelper: ViewDragHelper? = null
    var hideable = false
    var ignoreEvents = false
    var initialY = 0
    var touchingScrollingChild = false
    var lastNestedScrollDy = 0
    var nestedScrolled = false
    val dragCallback = DragCallback()
    var maxOffset = 0
    var minOffset = 0
    var parentHeight = 0
    var anchorTopMargin = 0

    init {
        context.obtainStyledAttributes(attrs, GoogleMapLikeBehaviorParam).apply {
            peekHeight = getDimensionPixelSize(GoogleMapLikeBehaviorParam_peekHeight, 0)
            anchorTopMargin = getDimensionPixelSize(GoogleMapLikeBehaviorParam_anchorPoint, 0)
            draggable = getBoolean(GoogleMapLikeBehaviorParam_draggable, false)
            skippedAnchorPoint = getBoolean(GoogleMapLikeBehaviorParam_skipAnchorPoint, false)
            recycle()
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (state != STATE_DRAGGING && state != STATE_SETTLING) {
            if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
                child.fitsSystemWindows = true
            }
            parent.onLayoutChild(child, layoutDirection)
        }
        parentHeight = parent.height
        minOffset = Math.max(0, parentHeight - child.height)
        maxOffset = Math.max(minOffset, parentHeight - peekHeight)
        anchorPosition = parentHeight - anchorTopMargin

        when (state) {
            STATE_ANCHOR_POINT -> {
                ViewCompat.offsetTopAndBottom(child, parentHeight - anchorPosition)
            }
            STATE_EXPANDED -> {
                ViewCompat.offsetTopAndBottom(child, minOffset)
            }
            STATE_HIDDEN -> {
                ViewCompat.offsetTopAndBottom(child, parentHeight)
            }
            STATE_COLLAPSED -> {
                ViewCompat.offsetTopAndBottom(child, maxOffset)
            }
            else -> {
            }
        }

        if (dragHelper == null) {
            dragHelper = ViewDragHelper.create(parent, dragCallback)
        }
        viewRef = WeakReference(child)
        val found: View = findScrollingChild(child)
        nestedScrollingChildRef = WeakReference(found)
        return true
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        if (!draggable) {
            return false
        }
        if (!child.isShown) {
            return false
        }

        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(ev)

        when (action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchingScrollingChild = false
                activePointerId = MotionEvent.INVALID_POINTER_ID
                if (ignoreEvents) {
                    ignoreEvents = false
                    return false
                }
            }
            MotionEvent.ACTION_DOWN -> {
                down(ev, parent, child)
            }
            else -> {
            }
        }

        if (!ignoreEvents && dragHelper?.shouldInterceptTouchEvent(ev)!!) {
            return true
        }

        val scroll = nestedScrollingChildRef.get()
        var touchSlop = 0
        dragHelper?.let {
            touchSlop = it.touchSlop
        }
        return action == MotionEvent.ACTION_MOVE
                && scroll != null
                && !ignoreEvents
                && state != STATE_DRAGGING
                && !parent.isPointInChildBounds(scroll, ev.x.toInt(), ev.y.toInt())
                && Math.abs(initialY - ev.y) > touchSlop
    }

    private fun down(ev: MotionEvent, parent: CoordinatorLayout, child: V) {
        val initialX = ev.x.toInt()
        initialY = ev.y.toInt()
        if (state == STATE_ANCHOR_POINT) {
            activePointerId = ev.getPointerId(ev.actionIndex)
            touchingScrollingChild = true
        } else {
            val scroll = nestedScrollingChildRef.get()
            if (scroll != null && parent.isPointInChildBounds(scroll, initialX, initialY)) {
                activePointerId = ev.getPointerId(ev.actionIndex)
                touchingScrollingChild = true
            }
        }
        ignoreEvents = activePointerId == MotionEvent.INVALID_POINTER_ID
                && !parent.isPointInChildBounds(child, initialX, initialY)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        if (!draggable) {
            return false
        }
        if (!child.isShown) {
            return false
        }
        val action = ev.action
        if (state == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true
        }

        dragHelper?.processTouchEvent(ev)
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(ev)

        if (action == MotionEvent.ACTION_MOVE && !ignoreEvents) {
            var touchSlop = 0
            dragHelper?.let {
                touchSlop = it.touchSlop
            }
            if (Math.abs(initialY - ev.y) > touchSlop.toFloat()) {
                dragHelper?.captureChildView(child, ev.getPointerId(ev.actionIndex))
            }
        }
        return !ignoreEvents
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        lastNestedScrollDy = 0
        nestedScrolled = false
        return ((axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val scrollChild = nestedScrollingChildRef.get() ?: return
        if (target != scrollChild) {
            return
        }
        val currentTop = child.top
        val newTop = currentTop - dy
        if (dy > 0) {
            if (newTop < minOffset) {
                consumed[1] = currentTop - minOffset
                ViewCompat.offsetTopAndBottom(child, -consumed[1])
                setStateInternal(STATE_EXPANDED)
            } else {
                consumed[1] = dy
                ViewCompat.offsetTopAndBottom(child, -dy)
                setStateInternal(STATE_DRAGGING)
            }
        } else if (dy < 0) {
            if (!target.canScrollVertically(-1)) {
                if (newTop <= maxOffset || hideable) {
                    consumed[1] = dy
                    ViewCompat.offsetTopAndBottom(child, -dy)
                    setStateInternal(STATE_DRAGGING)
                } else {
                    consumed[1] = currentTop - maxOffset
                    ViewCompat.offsetTopAndBottom(child, -consumed[1])
                    setStateInternal(STATE_COLLAPSED)
                }
            }
        }

        lastNestedScrollDy = dy
        nestedScrolled = true
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int) {
        if (child.top == minOffset) {
            setStateInternal(STATE_EXPANDED)
            return
        }
        if (target != nestedScrollingChildRef.get() || !nestedScrolled) {
            return
        }
        val top: Int
        val targetState: Long
        if (lastNestedScrollDy > 0) {
            val currentTop = child.top
            if (currentTop > parentHeight - anchorPosition) {
                if (skippedAnchorPoint) {
                    top = minOffset
                    targetState = STATE_EXPANDED
                } else {
                    top = parentHeight - anchorPosition
                    targetState = STATE_ANCHOR_POINT
                }
            } else {
                top = minOffset
                targetState = STATE_EXPANDED
            }
        } else if (hideable && shouldHide(child, getYvelocity())) {
            top = parentHeight
            targetState = STATE_HIDDEN
        } else if (lastNestedScrollDy == 0) {
            val currentTop = child.top
            if (Math.abs(currentTop - minOffset) < Math.abs(currentTop - maxOffset)) {
                top = minOffset
                targetState = STATE_EXPANDED
            } else {
                if (skippedAnchorPoint) {
                    top = minOffset
                    targetState = STATE_EXPANDED
                } else {
                    top = maxOffset
                    targetState = STATE_COLLAPSED
                }
            }
        } else {
            val currentTop = child.top
            if (currentTop > parentHeight - anchorPosition) {
                top = maxOffset
                targetState = STATE_COLLAPSED
            } else {
                if (skippedAnchorPoint) {
                    top = maxOffset
                    targetState = STATE_COLLAPSED
                } else {
                    top = parentHeight - anchorPosition
                    targetState = STATE_ANCHOR_POINT
                }
            }
        }
        if (dragHelper?.smoothSlideViewTo(child, child.left, top)!!) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(child, SettleRunnable(child, targetState))
        } else {
            setStateInternal(targetState)
        }
        nestedScrolled = false
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float): Boolean {
        return target == nestedScrollingChildRef.get()
                && (state != STATE_EXPANDED) ||
                super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }

    private fun getYvelocity(): Float {
        velocityTracker?.computeCurrentVelocity(1000, 2000.0f)
        return velocityTracker!!.getYVelocity(activePointerId)
    }

    private fun findScrollingChild(view: View): View = when (view) {
        is NestedScrollingChild -> view
        is ViewGroup -> {
            lateinit var result: View
            (0 until view.childCount)
                    .map { findScrollingChild(view.getChildAt(it)) }
                    .forEach {
                        result = it
                    }
            result
        }
        else -> view
    }

    private fun setStateInternal(@State state: Long) {
        if (this.state == state) {
            return
        }
        this.state = state
        if (!(this.state == STATE_DRAGGING || this.state == STATE_SETTLING)) {
            this.listener?.onBehaviorStateChanged(state)
        }
    }

    private fun reset() {
        activePointerId = ViewDragHelper.INVALID_POINTER
        velocityTracker?.let {
            it.recycle()
            velocityTracker = null
        }
    }

    private fun shouldHide(view: View, yvel: Float): Boolean {
        // TODO
        return false
    }

    interface OnBehaviorStateListener {
        fun onBehaviorStateChanged(newState: Long)
    }

    inner class SettleRunnable(val view: View, @State private val state: Long) : Runnable {
        override fun run() {
            if (dragHelper != null && dragHelper?.continueSettling(true)!!) {
                ViewCompat.postOnAnimation(view, this)
            } else {
                setStateInternal(state)
            }
        }
    }

    inner class DragCallback : Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (state == STATE_DRAGGING) {
                return false
            }
            if (touchingScrollingChild) {
                return false
            }
            if (state == STATE_EXPANDED && activePointerId == pointerId) {
                val scroll = nestedScrollingChildRef.get()
                if (scroll != null && scroll.canScrollVertically(-1)) {
                    return false
                }
            }
            return viewRef.get() != null
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING)
            }
        }

//        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
//            Log.e("0", "safsa")
//
//            @State val targetState: Long
//            val top: Int
//
//            when {
//                yvel < 0 -> {
//                    Log.e("1", "safsa")
//                    val currentTop = releasedChild.top
//                    if (Math.abs(currentTop - minOffset) < Math.abs(currentTop - parentHeight + anchorPosition)) {
//                        top = minOffset
//                        targetState = STATE_EXPANDED
//                    } else {
//                        top = parentHeight - anchorPosition
//                        targetState = STATE_ANCHOR_POINT
//                    }
//                }
//                hideable && shouldHide(releasedChild, yvel) -> {
//                    Log.e("2", "safsa")
//                    top = parentHeight
//                    targetState = STATE_HIDDEN
//                }
//                yvel == 0.0f -> {
//                    Log.e("3", "safsa")
//                    val currentTop = releasedChild.top
//                    if (Math.abs(currentTop - minOffset) < Math.abs(currentTop - parentHeight + anchorPosition)) {
//                        top = minOffset
//                        targetState = STATE_EXPANDED
//                    } else if (Math.abs(currentTop - parentHeight + anchorPosition) < Math.abs(currentTop - maxOffset)) {
//                        if (skippedAnchorPoint) {
//                            top = maxOffset
//                            targetState = STATE_COLLAPSED
//                        } else {
//                            top = parentHeight - anchorPosition
//                            targetState = STATE_ANCHOR_POINT
//                        }
//                    } else {
//                        top = maxOffset
//                        targetState = STATE_COLLAPSED
//                    }
//                }
//                else -> {
//                    Log.e("4", "safsa")
//                    val currentTop = releasedChild.top
//                    if (Math.abs(currentTop - parentHeight + anchorPosition) < Math.abs(currentTop - maxOffset)) {
//                        if (skippedAnchorPoint) {
//                            top = maxOffset
//                            targetState = STATE_COLLAPSED
//                        } else {
//                            top = parentHeight - anchorPosition
//                            targetState = STATE_ANCHOR_POINT
//                        }
//                    } else {
//                        top = maxOffset
//                        targetState = STATE_COLLAPSED
//                    }
//                }
//            }
//
//            setStateInternal(releasedChild, top, targetState)
//        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int =
                constrain(top, minOffset, getOffset())

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int =
                child.left

        private fun constrain(amount: Int, low: Int, high: Int): Int = when {
            amount < low -> low
            amount > high -> high
            else -> amount
        }

        override fun getViewVerticalDragRange(child: View): Int = if (hideable) {
            parentHeight - minOffset
        } else {
            maxOffset - minOffset
        }
    }

    private fun setStateInternal(releasedChild: View, top: Int, targetState: Long) {
        val settleCaptureViewAt = dragHelper?.settleCapturedViewAt(releasedChild.left, top)!!
        if (settleCaptureViewAt) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(releasedChild, SettleRunnable(releasedChild, targetState))
        } else {
            setStateInternal(targetState)
        }
    }

    private fun getOffset(): Int =
            if (hideable) parentHeight else maxOffset

}
