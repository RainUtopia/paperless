package com.pa.paperless.fragment.meeting;


import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.controller.BaseController;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.IModelChangeListener;
import com.wind.myapplication.NativeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18 0018.
 */

public abstract class BaseFragment extends Fragment implements IModelChangeListener, CallListener {

    protected BaseController mController;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleUI(msg);
        }
    };
    protected NativeUtil nativeUtil  ;
    protected List<ReceiveMeetIMInfo> mReceiveMsg ;

    protected void handleUI(Message msg) {
        //因为有的要实现网络请求，有的子类不需要，所以只需要空实现，让子类去覆写该方法
    }

    @Override
    public void onModelChanged(int action, Object resultBean) {
//        mHandler.obtainMessage(action, resultBean).sendToTarget();
    }


    protected void initController() {
//        nativeUtil = NativeUtil.getInstance();
//        nativeUtil.setCallListener(this);
    }

    @Override
    public void callListener(int action, Object result) {
        mHandler.obtainMessage(action, result).sendToTarget();
    }
}