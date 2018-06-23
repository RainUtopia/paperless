package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.pa.paperless.service.NativeService.nativeUtil;

/**
 * Created by Administrator on 2017/11/1.
 * 公告议程-公告
 */

public class NoticeFragment extends BaseFragment{

    private final String TAG = "NoticeFragment-->";
    private TextView mNoticeText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.notice, container, false);
        initView(inflate);
        try {
            //查询长文本公告
            nativeUtil.queryLongNotice();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return inflate;
    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.notice://收到公告信息
                InterfaceBullet.pbui_BigBulletDetailInfo object = (InterfaceBullet.pbui_BigBulletDetailInfo) message.getObject();
                String content = MyUtils.getBts(object.getText());
                mNoticeText.setText(content);
                break;
            case IDEventMessage.NOTICE_CHANGE_INFO:
                nativeUtil.queryLongNotice();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initView(View inflate) {
        mNoticeText = (TextView) inflate.findViewById(R.id.notice_text);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e(TAG, "NoticeFragment.onHiddenChanged :  hidden --> "+hidden);
        if (!hidden) {
            //查询长文本公告
            try {
                nativeUtil.queryLongNotice();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

}
