package com.pa.paperless.activity;

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

import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.IModelChangeListener;
import com.pa.paperless.service.FabService;
import com.wind.myapplication.CameraDemo;
import com.wind.myapplication.ScreenRecorder;


/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class BaseActivity extends AppCompatActivity implements IModelChangeListener {

    private final String TAG = "BaseActivity-->";
    private final int REQUEST_CODE = 456;
    private MediaProjectionManager manager;
    private MediaProjection projection;
    private ScreenRecorder recorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScreenParam();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleUI(msg);
        }
    };

    protected void handleUI(Message msg) {
        //因为有的要实现网络请求，有的子类不需要，所以只需要空实现，让子类去覆写该方法
    }

    @Override
    public void onModelChanged(int action, Object resultBean) {
        mHandler.obtainMessage(action, resultBean).sendToTarget();
    }

    protected void initController() {

    }

    private int width, height, dpi, bitrate, VideoQuality = 1;//VideoQuality:1/2/3
    public static int W = 0, H = 0;
    private float density;

    protected void stopStreamInform(EventMessage message) {
        switch (message.getType()) {
            case 2:
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

    private boolean stopRecord() {
        if (recorder != null) {
            /** ************ ******  停止资源操作  ****** ************ **/
//            nativeUtil.stopResourceOperate(allRes, devIds);
            /** ************ ******  释放播放资源  ****** ************ **/
//            nativeUtil.mediaDestroy(0);
            recorder.quit();
            recorder = null;
            return true;
        } else return false;
    }

    //收到采集屏幕通知
    protected void enentCollectionStream(EventMessage message) {
        switch (message.getType()) {
            case 2://屏幕
                if (stopRecord()) {
                    showTip("屏幕录制已停止");
                } else {
                    // 1: 拿到 MediaProjectionManager 实例
                    manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                    // 2: 发起屏幕捕捉请求
                    Intent intent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent = manager.createScreenCaptureIntent();
                    }
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
            case 3://摄像头
                startActivity(new Intent(this, CameraDemo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (manager == null) {
                Log.i(TAG, "MeetingActivity.onActivityResult :  manager为null --> ");
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
            recorder.start();//�启动录屏线程
            FabService.isBusy = true;
            showTip("屏幕录制中...");
        }
    }

    protected void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}