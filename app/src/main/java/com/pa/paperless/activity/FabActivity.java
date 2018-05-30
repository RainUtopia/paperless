package com.pa.paperless.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.utils.MyUtils;

public class FabActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mKeyboard;
    private TextView mHandwritten;
    private TextView mScreens;
    private TextView mOneScreen;
    private TextView mBackPop;
    private TextView mProjection;
    private TextView mNote;
    private TextView mCallService;
    private TextView mWhiteplatePop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        
        MyUtils.setBackgroundAlpha(this, 0.5f);
        initView();
    }

    private void initView() {
        mKeyboard = (TextView) findViewById(R.id.keyboard);
        mHandwritten = (TextView) findViewById(R.id.handwritten);
        mScreens = (TextView) findViewById(R.id.screens);
        mOneScreen = (TextView) findViewById(R.id.one_screen);
        mBackPop = (TextView) findViewById(R.id.back_pop);
        mProjection = (TextView) findViewById(R.id.projection);
        mNote = (TextView) findViewById(R.id.note);
        mCallService = (TextView) findViewById(R.id.call_service);
        mWhiteplatePop = (TextView) findViewById(R.id.whiteplate_pop);

        mKeyboard.setOnClickListener(this);
        mHandwritten.setOnClickListener(this);
        mScreens.setOnClickListener(this);
        mOneScreen.setOnClickListener(this);
        mBackPop.setOnClickListener(this);
        mProjection.setOnClickListener(this);
        mNote.setOnClickListener(this);
        mCallService.setOnClickListener(this);
        mWhiteplatePop.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
//        MyUtils.setBackgroundAlpha(this, 1.0f);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.keyboard:

                break;
            case R.id.handwritten:

                break;
            case R.id.screens:

                break;
            case R.id.one_screen:

                break;
            case R.id.back_pop:

                break;
            case R.id.projection:

                break;
            case R.id.note:

                break;
            case R.id.call_service:

                break;
            case R.id.whiteplate_pop:

                break;
        }
    }
}
