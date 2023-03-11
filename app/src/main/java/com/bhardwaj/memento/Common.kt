package com.bhardwaj.memento

import android.os.Build
import android.view.View
import android.view.Window

class Common {
    companion object {
        var adsCounter: Int = 1

        fun fullScreen(window: Window) {
            window.decorView.apply {
                systemUiVisibility = if (Build.VERSION.SDK_INT in 12..18) View.GONE
                else
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        }
    }
}