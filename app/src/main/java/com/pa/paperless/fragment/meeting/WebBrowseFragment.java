package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 * 打开网页
 */

public class WebBrowseFragment extends BaseFragment implements CallListener {

    private WebView mWebView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_NET:
                    ArrayList queryNet = msg.getData().getParcelableArrayList("queryNet");
                    InterfaceMain.pbui_meetUrl o = (InterfaceMain.pbui_meetUrl) queryNet.get(0);
                    String url = MyUtils.getBts(o.getUrl());
                    Log.e("MyLog", "WebBrowseFragment.handleMessage:  url --->>> " + url);
                    mWebView.loadUrl("http://"+url);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_webbrowse, container, false);
        initController();
        initView(inflate);
        try {
            nativeUtil.webQuery();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nativeUtil = NativeUtil.getInstance();
//        nativeUtil = new NativeUtil();

        nativeUtil.setCallListener(this);
        Log.e("MyLog","WebBrowseFragment.onAttach:   --->>> ");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.NETWEB_INFORM:
                Log.e("MyLog", "WebBrowseFragment.getEventMessage:  11111 --->>> ");
                nativeUtil.webQuery();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void initView(View inflate) {
        mWebView = (WebView) inflate.findViewById(R.id.web_view);
        mWebView = new WebView(getContext());
//        mWebView.loadUrl("http://wap.baidu.com");
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_NET:
                InterfaceMain.pbui_meetUrl result1 = (InterfaceMain.pbui_meetUrl) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryNet", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO:
                Log.e("MyLog","WebBrowseFragment.callListener:  收到会议消息 --->>> ");

                InterfaceMain2.pbui_Type_MeetIM receiveMsg = (InterfaceMain2.pbui_Type_MeetIM) result;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                }
        }
    }
}
