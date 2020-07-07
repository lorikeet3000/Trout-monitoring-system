package ru.arvata.pomor.util

import android.content.Intent
import android.graphics.Rect
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.view.ViewTreeObserver

val BROADCAST_KEYBOARD = App.appContext.packageName + "broadcast_keyboard_event"
const val BROADCAST_KEYBOARD_IS_OPEN_EXTRA = "broadcast_keyboard_event_is_open"

class KeyboardBroadcaster(private val rootView: View) {
    private val keyboardObserver = ViewTreeObserver.OnGlobalLayoutListener {
        val isKeyboard = isKeyboardShown()
        val intent = Intent(BROADCAST_KEYBOARD)
        intent.putExtra(BROADCAST_KEYBOARD_IS_OPEN_EXTRA, isKeyboard)
        LocalBroadcastManager.getInstance(rootView.context.applicationContext).sendBroadcast(intent)
    }

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardObserver)
    }

    // https://stackoverflow.com/a/26152562
    private fun isKeyboardShown(): Boolean {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        val SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128

        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        val heightDiff = rootView.bottom - r.bottom
        /* Threshold size: dp to pixels, multiply with display density */

        return heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density
    }
}

