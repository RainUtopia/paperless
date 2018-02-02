package com.pa.paperless.fragment.meeting;


import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.pa.paperless.controller.BaseController;
import com.pa.paperless.listener.IModelChangeListener;
import com.wind.myapplication.NativeUtil;

/**
 * Created by Administrator on 2017/5/18 0018.
 */

public abstract class BaseFragment extends Fragment implements IModelChangeListener {

    protected BaseController mController;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleUI(msg);
        }
    };
    protected NativeUtil nativeUtil ;

    protected void handleUI(Message msg) {
        //因为有的要实现网络请求，有的子类不需要，所以只需要空实现，让子类去覆写该方法
    }


    @Override
    public void onModelChanged(int action, Object resultBean) {
        mHandler.obtainMessage(action, resultBean).sendToTarget();
    }


    protected void initController() {
    }
}