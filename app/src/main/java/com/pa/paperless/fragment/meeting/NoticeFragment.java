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
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.pa.paperless.R;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.event.EventNotice;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;

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
                InterfaceBullet.pbui_BigBulletDetailInfo o = (InterfaceBullet.pbui_BigBulletDetailInfo) notice.get(0);
                String text = new String(o.getText().toByteArray());
                setContent(text);
            }
        }
    };

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_LONG_NOTICE:
                InterfaceBullet.pbui_BigBulletDetailInfo notice = (InterfaceBullet.pbui_BigBulletDetailInfo) result;
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
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                InterfaceIM.pbui_Type_MeetIM receiveMsg = (InterfaceIM.pbui_Type_MeetIM) result;
                //获取之前的未读消息个数
                int badgeNumber1 = mBadge.getBadgeNumber();
                int all =  badgeNumber1 + 1;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    receiveMeetIMInfos.get(0).setType(true);
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                }
                List<EventBadge> num = new ArrayList<>();
                num.add(new EventBadge(all));
                EventBus.getDefault().post(new EventMessage(IDEventMessage.UpDate_BadgeNumber, num));
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
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }
    private void initView(View inflate) {
        mNoticeText = (TextView) inflate.findViewById(R.id.notice_text);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            nativeUtil = NativeUtil.getInstance();
            nativeUtil.setCallListener(this);
        }
    }

}
