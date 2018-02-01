package com.pa.paperless.utils;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.View;

/**
 * Created by Administrator on 2017/11/7.
 */

public class ClickViewAnimationUtil {

    public static void setAnimator(View iv) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 1.5f, 1f);
        animator.setDuration(300);
        animator.start();
    }
    public static void setCheckedColor(View view){
        
        view.setBackgroundColor(Color.BLUE);
    }
}
