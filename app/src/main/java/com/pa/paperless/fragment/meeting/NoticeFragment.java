package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.event.EventNotice;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.controller.MeetController;
import com.pa.paperless.listener.CallListener;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/1.
 * 公告议程-公告
 */

public class NoticeFragment extends BaseFragment implements CallListener {

    private TextView mNoticeText;
    private NativeUtil nativeUtil;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == IDivMessage.QUERY_LONG_NOTICE) {
                Log.e("MyLog", "NoticeFragment.handleMessage:  长文本公告 --->>> ");
                ArrayList notice = msg.getData().getParcelableArrayList("notice");
                InterfaceMain.pbui_BigBulletDetailInfo o = (InterfaceMain.pbui_BigBulletDetailInfo) notice.get(0);
                String text = new String(o.getText().toByteArray());
                setContent(text);
            }
        }
    };

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_LONG_NOTICE:
                Log.e("MyLog", "NoticeFragment.callListener:  长文本公告 --->>> ");
                InterfaceMain.pbui_BigBulletDetailInfo notice = (InterfaceMain.pbui_BigBulletDetailInfo) result;
                if (notice != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(notice);
                    bundle.putParcelableArrayList("notice", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }

    //设置展示的公告
    private void setContent(String content) {
        mNoticeText.setText(content);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.notice, container, false);
        initController();
        initView(inflate);
        try {
            //查询长文本公告
            nativeUtil.queryLongNotice();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventNotice(EventNotice eventNotice) {
        Log.e("MyLog", "NoticeFragment.getEventNotice:  查询长文本公告 EventBus --->>> ");
        try {
            nativeUtil.queryLongNotice();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initController() {
        mController = new MeetController(getContext());
        mController.setIModelChangeListener(this);
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mNoticeText = (TextView) inflate.findViewById(R.id.notice_text);
    }


}
