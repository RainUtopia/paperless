package com.pa.paperless.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.libsdl.app.SDLActivity;

/**
 * Created by Administrator on 2018/3/12.
 */

public class PlayService extends Service {

    private final String TAG = "PlayService-->";

    private boolean SDLIsShow = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        if (message.getAction() == 0 && message.getType() == 0) {
            SDLIsShow = false;
        } else if (message.getAction() == 1 && message.getType() == 1) {
            SDLIsShow = true;
        }
        switch (message.getAction()) {
            case IDEventMessage.MEDIA_PLAY_INFORM:
                if (!SDLIsShow) {
                    Log.e("MyLog", "SigninFragment.getEventMessage:  收到媒体播放通知 打开播放界面 EventBus --->>> ");
                    InterfacePlaymedia.pbui_Type_MeetMediaPlay data = (InterfacePlaymedia.pbui_Type_MeetMediaPlay) message.getObject();
                    startActivity(new Intent(this, SDLActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("action", IDEventMessage.MEDIA_PLAY_INFORM)
                            .putExtra("data", data.toByteArray()));
                }
                break;
            case IDEventMessage.PLAY_STREAM_NOTIFY:
                if (!SDLIsShow) {
                    Log.e("MyLog", "SigninFragment.getEventMessage:  收到流播放通知 打开播放界面 EventBus --->>> ");
                    InterfaceStream.pbui_Type_MeetStreamPlay data = (InterfaceStream.pbui_Type_MeetStreamPlay) message.getObject();
                    startActivity(new Intent(this, SDLActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("action", IDEventMessage.PLAY_STREAM_NOTIFY)
                            .putExtra("data", data.toByteArray()));
                }
                break;
            case IDEventMessage.START_COLLECTION_STREAM_NOTIFY:
                break;
            case IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY:
                break;
        }
    }
}
