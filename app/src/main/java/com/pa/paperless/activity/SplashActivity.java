package com.pa.paperless.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import com.pa.paperless.R;
import com.wind.myapplication.NativeUtil;

public class SplashActivity extends BaseActivity {

    private Button over;
    private NativeUtil nativeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initController();
        initView();
//        nativeUtil.javaInitSys();
        initSplashAnim();
    }

    private void overTo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 5000);
    }

    private void initSplashAnim() {
        //动画实现,属性动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(over, "alpha", 0.0f, 1.0f);

        alpha.setDuration(500);
        //设置延迟
//        alpha.setStartDelay(500);

        //监听：当动画结束时启动新的界面
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                //跳转之后 关闭当前的页面
                finish();
                super.onAnimationEnd(animation);
            }
        });
        //启动动画
        alpha.start();
    }


    private void initView() {
        over = (Button) findViewById(R.id.over);
    }


}
