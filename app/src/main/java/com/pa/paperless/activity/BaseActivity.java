package com.pa.paperless.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.observer.Observer;
import com.pa.paperless.observer.Subject;
import com.pa.paperless.service.FabService;
import com.pa.paperless.service.ShotApplication;
import com.wind.myapplication.CameraDemo;
import com.wind.myapplication.ScreenRecorder;


/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class BaseActivity extends AppCompatActivity {

    private final String TAG = "BaseActivity-->";
    public static Subject subject;
    private int width, height, dpi, bitrate, VideoQuality = 1;//VideoQuality:1/2/3
    public static int W = 0, H = 0;
    private float density;
    private MediaProjectionManager manager;
    private MediaProjection projection;
    private int result = 0;
    private Intent intent = null;
    protected final int REQUEST_MEDIA_PROJECTION = 10086;
    private final int REQUEST_CODE = 10010;
    private ScreenRecorder recorder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScreenParam();
//        manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        manager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        subject = new Subject();
        Inform o = new Inform();
        subject.attach(o);
    }

    public class Inform extends Observer {
        @Override
        public void updata(int action, int type) {
            switch (action) {
                case IDEventMessage.START_COLLECTION_STREAM_NOTIFY://开始采集通知
                    Log.i("stream_log", "Inform.updata :  开始采集 --->>> ");
                    enentCollectionStream(type);
                    break;
                case IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY://停止采集通知
                    Log.i("stream_log", "Inform.updata :  停止采集 --->>> ");
                    stopStreamInform(type);
                    break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void startIntent(int action) {
//        manager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        if (intent != null && result != 0) {
//            Log.i("capture", "MeetingActivity.startIntent :  用户同意捕获屏幕 --->>> ");
//            ((ShotApplication) getApplication()).setResult(result);
//            ((ShotApplication) getApplication()).setIntent(intent);
//        } else {
            startActivityForResult(manager.createScreenCaptureIntent(), action);
            ((ShotApplication) getApplication()).setMediaProjectionManager(manager);
//        }
    }

    protected void initController() {
    }

    //停止采集屏幕
    private void stopStreamInform(int type) {
        switch (type) {
            case 2://屏幕
                if (stopRecord()) {
                    showTip("屏幕录制已停止");
                } else {
                    showTip("屏幕录制停止失败");
                }
                break;
        }
    }

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // �屏幕宽度（像素）
        height = metric.heightPixels; // �屏幕高度（像素）
        if (width > 1920)
            width = 1920;
        if (height > 1080)
            height = 1080;
        W = width;
        H = height;
        density = metric.density; // �屏幕密度（0.75 / 1.0 / 1.5）
        dpi = metric.densityDpi; // �屏幕密度DPI（120 / 160 / 240）
        bitrate = width * height * VideoQuality;//�比特率/码率
    }

    protected boolean stopRecord() {
        if (recorder != null) {
            recorder.quit();
            recorder = null;
            return true;
        } else return false;
    }

    //收到采集屏幕通知
    private void enentCollectionStream(int type) {
        switch (type) {
            case 2://屏幕
                if (stopRecord()) {
                    showTip("屏幕录制已停止");
                } else {
                    // 1: 拿到 MediaProjectionManager 实例
//                    manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                    // 2: 发起屏幕捕捉请求
//                    Intent intent = null;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        intent = manager.createScreenCaptureIntent();
//                    }
//                    startActivityForResult(intent, REQUEST_CODE);
//                    Log.i(TAG, "启动 startActivityForResult :  ");
                    startIntent(REQUEST_CODE);
                }
                break;
            case 3://摄像头
                startActivity(new Intent(this, CameraDemo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "BaseActivity.onActivityResult :  进入... ");
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (manager == null) {
                Log.i(TAG, "BaseActivity.onActivityResult :  manager为null --> ");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                projection = manager.getMediaProjection(resultCode, data);
            }
            if (projection == null) {
                Log.i(TAG, "media projection is null");
                return;
            }
            recorder = new ScreenRecorder(width, height, bitrate, dpi, projection, "");
            recorder.start();//启动录屏线程
            FabService.isBusy = true;
            showTip("屏幕录制...");
            Log.e(TAG, "BaseActivity.onActivityResult :  屏幕录制... --> ");
        } else if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) {
            if (data != null && resultCode != 0) {
                Log.i(TAG, "BaseActivity.onActivityResult :  用户同意");
                result = resultCode;
                intent = data;
                ((ShotApplication) getApplication()).setResult(resultCode);
                ((ShotApplication) getApplication()).setIntent(data);
                ((ShotApplication) getApplication()).att();
            }
        }
    }

    protected Toast mT;

    protected void showTip(String msg) {
        if (mT == null) {
            mT = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mT.setText(msg);
        }
        mT.show();
    }

}