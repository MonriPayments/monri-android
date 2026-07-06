package com.monri.android.example

import android.app.Activity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

fun Activity.enableEdgeToEdge(rootView: View, consumeInsets: Boolean = false) {

    WindowCompat.setDecorFitsSystemWindows(window, false)

    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, insets: WindowInsetsCompat ->
        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        val params = rootView.layoutParams as MarginLayoutParams
        params.topMargin = systemInsets.top
        rootView.setLayoutParams(params)

        v.setPadding(
            systemInsets.left,
            v.paddingTop,
            systemInsets.right,
            systemInsets.bottom
        )

        if (consumeInsets) WindowInsetsCompat.CONSUMED else insets
    }
}