package com.pa.paperless.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.pa.paperless.controller.BaseController;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.IModelChangeListener;
import com.pa.paperless.utils.Convert;
import com.wind.myapplication.NativeUtil;


/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class BaseActivity extends AppCompatActivity implements IModelChangeListener {

    protected BaseController mController;
//    protected NativeUtil nativeUtil ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        nativeUtil = NativeUtil.getInstance();
//        nativeUtil.setCallListener(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleUI(msg);
        }
    };

    protected  void handleUI(Message msg){
        //因为有的要实现网络请求，有的子类不需要，所以只需要空实现，让子类去覆写该方法
    }
    @Override
    public void onModelChanged(int action, Object resultBean) {
        mHandler.obtainMessage(action,resultBean).sendToTarget();
    }

    protected void initController(){

    }

}