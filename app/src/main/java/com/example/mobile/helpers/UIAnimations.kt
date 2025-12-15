package com.example.mobile.helpers

import android.app.Activity
import android.view.View
import android.view.animation.AnimationUtils
import com.example.mobile.R

class UIAnimations(private val activity: Activity) {

    fun startFloatingCircles() {
        val floatAnimation = AnimationUtils.loadAnimation(activity, R.anim.float_animation)

        activity.findViewById<View>(R.id.circle1)?.startAnimation(floatAnimation)
        activity.findViewById<View>(R.id.circle2)?.startAnimation(floatAnimation)
        activity.findViewById<View>(R.id.circle3)?.startAnimation(floatAnimation)
    }
}