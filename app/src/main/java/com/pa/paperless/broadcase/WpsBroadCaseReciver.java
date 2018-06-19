package com.pa.paperless.broadcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.WpsModel;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by Administrator on 2018/5/30.
 */

public class WpsBroadCaseReciver extends BroadcastReceiver {

    private final String TAG = "WpsBroadCaseReciver-->";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WpsModel.Reciver.ACTION_BACK://返回键
                Log.e(TAG, "WpsBroadCaseReciver.onReceive :  返回键 --> ");
                break;
            case WpsModel.Reciver.ACTION_CLOSE://关闭文件时候的广播
                Log.e(TAG, "WpsBroadCaseReciver.onReceive :  关闭文件时候的广播 --> ");
                Intent intent1 = new Intent(context,MeetingActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                break;
            case WpsModel.Reciver.ACTION_HOME://home键广播
                Log.e(TAG, "WpsBroadCaseReciver.onReceive :  home键广播 --> ");
                break;
            case WpsModel.Reciver.ACTION_SAVE://保存广播
                Log.e(TAG, "WpsBroadCaseReciver.onReceive :  保存键广播 --> ");
                File file = MyUtils.getmFile();
                if (file != null) {
                    EventBus.getDefault().post(new EventMessage(IDEventF.updata_to_postil));
                }
                break;
        }
    }
}
