package com.cmpt362co.genericproject.animation

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class PinDetailAnimation(private var animatedView : View, private var endHeight : Int, private var type : Int) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        if(type == 0) {
            animatedView.layoutParams.height = (endHeight * interpolatedTime).toInt();
        } else {
            animatedView.layoutParams.height = endHeight - (endHeight * interpolatedTime).toInt();
        }
        animatedView.requestLayout();
        /*if (interpolatedTime < 1.0f) {
            if(type == 0) {
                animatedView.layoutParams.height = (endHeight * interpolatedTime).toInt();
            } else {
                animatedView.layoutParams.height = endHeight - (endHeight * interpolatedTime).toInt();
            }
            animatedView.requestLayout();
        } else {
            if(type == 0) {
                animatedView.layoutParams.height = endHeight;
                animatedView.requestLayout();
            } else {
                animatedView.layoutParams.height = 0;
                animatedView.visibility = View.GONE
                animatedView.requestLayout();
                animatedView.layoutParams.height = endHeight;
            }
        }*/
    }
}