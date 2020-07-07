package ru.arvata.pomor.util

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class LockableBottomSheetBehavior<V : View>(context: Context?, attrs: AttributeSet?) :
    BottomSheetBehavior<V>(context, attrs) {

    private var locked = false

    fun setLocked(locked: Boolean) {
        this.locked = locked
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        var handled = false
        if(!locked) {
            handled = super.onInterceptTouchEvent(parent, child, event)
        }
        return handled
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        var handled = false
        if(!locked) {
            handled = super.onTouchEvent(parent, child, event)
        }
        return handled
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        var handled = false
        if(!locked) {
            handled = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
        }
        return handled
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if(!locked) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int) {
        if(!locked) {
            super.onStopNestedScroll(coordinatorLayout, child, target, type)
        }
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        var handled = false
        if(!locked) {
            handled = super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
        }
        return handled
    }
}