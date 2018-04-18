package com.pa.paperless.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.CameraDemo;
import com.wind.myapplication.NativeUtil;
import com.wind.myapplication.ScreenRecorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查看批注界面
 */
public class PostilActivity extends BaseActivity implements View.OnClickListener, CallListener {

    private ImageView mPostilImage;
    Button mPostilSaveLocal, mPostilSaveServer, mPostilPic, mPostilStartScreen, mPostilStopScreen,
            mPostilStartProjection, mPostilStopProjection, mPostilPre, mPostilNext;
    private byte[] screenshot;
    private PostilActivity context;
    private NativeUtil nativeUtil;
    private List<Integer> ids1;
    private List<Integer> ids2;
    private List<Integer> informIds;
    private List<Integer> collectIds;
    private ScreenRecorder recorder;
    private MediaProjectionManager manager;

    public static final int POSTIL_REQUEST_CODE = 2;
    private MediaProjection projection;
    public static int W = 0, H = 0;
    private int width, height, dpi, bitrate, VideoQuality = 1;
    private long fileName;//获取系统时间的文件名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postil);
        context = this;
        initNative();
        initView();
        Intent intent = getIntent();
        //获取会议界面截图产生的图片
        screenshot = intent.getByteArrayExtra("screenshot");
        Glide.with(this).load(screenshot).into(mPostilImage);
        EventBus.getDefault().register(this);
        initScreenParam();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initNative() {
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) {
        switch (message.getAction()) {
            case IDEventMessage.START_COLLECTION_STREAM_NOTIFY:
                switch (message.getType()) {
                    case 2://屏幕
                        if (stopRecord()) {
                            Toast.makeText(this, "屏幕录制已停止", Toast.LENGTH_LONG).show();
                        } else {
                            // 1: 拿到 MediaProjectionManager 实例
                            manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                            // 2: 发起屏幕捕捉请求
                            Intent intent = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                intent = manager.createScreenCaptureIntent();
                            }
                            startActivityForResult(intent, POSTIL_REQUEST_CODE);
                        }
                        break;
                    case 3://摄像头
                        startActivity(new Intent(PostilActivity.this, CameraDemo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                }
                break;
            case IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY:
                switch (message.getType()) {
                    case 2:
                        if (stopRecord()) {
                            Toast.makeText(this, "屏幕录制已停止", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "屏幕录制停止失败", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                break;
        }
    }

    private boolean stopRecord() {
        if (recorder != null) {
            recorder.quit();
            recorder = null;
            return true;
        } else return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POSTIL_REQUEST_CODE && resultCode == RESULT_OK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                projection = manager.getMediaProjection(resultCode, data);
            }
        if (projection == null) {
            return;
        }
        recorder = new ScreenRecorder(width, height, bitrate, dpi, projection, "");
        recorder.start();//�启动录屏线程
    }

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;// �屏幕宽度（像素）
        height = metric.heightPixels; // �屏幕高度（像素）
        if (width > 1920)
            width = 1920;
        if (height > 1080)
            height = 1080;
        W = width;
        H = height;
        float density = metric.density;// �屏幕密度（0.75 / 1.0 / 1.5）
        dpi = metric.densityDpi;// �屏幕密度DPI（120 / 160 / 240）
        bitrate = width * height * VideoQuality;//�比特率/码率
    }

    private void initView() {
        mPostilImage = (ImageView) findViewById(R.id.postil_image);
        mPostilSaveLocal = (Button) findViewById(R.id.postil_save_local);
        mPostilSaveServer = (Button) findViewById(R.id.postil_save_server);
        mPostilPic = (Button) findViewById(R.id.postil_pic);
        mPostilStartScreen = (Button) findViewById(R.id.postil_start_screen);
        mPostilStopScreen = (Button) findViewById(R.id.postil_stop_screen);
        mPostilStartProjection = (Button) findViewById(R.id.postil_start_projection);
        mPostilStopProjection = (Button) findViewById(R.id.postil_stop_projection);
        mPostilPre = (Button) findViewById(R.id.postil_pre);
        mPostilNext = (Button) findViewById(R.id.postil_next);

        mPostilSaveLocal.setOnClickListener(this);
        mPostilSaveServer.setOnClickListener(this);
        mPostilPic.setOnClickListener(this);
        mPostilStartScreen.setOnClickListener(this);
        mPostilStopScreen.setOnClickListener(this);
        mPostilStartProjection.setOnClickListener(this);
        mPostilStopProjection.setOnClickListener(this);
        mPostilPre.setOnClickListener(this);
        mPostilNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postil_save_local://保存本地
                showImportFileNamePop();
                break;
            case R.id.postil_save_server://保存到服务器

                break;
            case R.id.postil_pic://使用画板打开
                Intent intent = new Intent(PostilActivity.this, PeletteActivity.class);
                intent.putExtra("postilpic", screenshot);
                context.startActivity(intent);
                break;
            case R.id.postil_start_screen:
                ids1 = new ArrayList<>();
                ids2 = new ArrayList<>();
                ids1.add(0);
                ids2.add(0x1100003);//要播放的屏幕源  要同屏的人员
                /** ************ ******  流播放  ******0x1080004 ************ **/
                nativeUtil.streamPlay(MainActivity.getLocalInfo().getDeviceid(), 2, 0,
                        ids1, ids2);
                break;
            case R.id.postil_stop_screen:
                if (ids1 != null && ids2 != null) {
                    nativeUtil.stopResourceOperate(ids1, ids2);
                }
                break;
            case R.id.postil_start_projection://开始投影
                informIds = new ArrayList<Integer>();
                collectIds = new ArrayList<Integer>();
                collectIds.add(0);
                informIds.add(0x1080004);
                /** ************ ******  流播放  ****** ************ **/
                nativeUtil.streamPlay(MeetingActivity.getDevId(), 3, 0, collectIds, informIds);

                break;
            case R.id.postil_stop_projection:
                if (collectIds != null && informIds != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(collectIds, informIds);
                }
                break;
            case R.id.postil_pre:

                break;
            case R.id.postil_next:

                break;
        }
    }

    private void showImportFileNamePop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("输入文件名：");
        final EditText edt = new EditText(context);
        edt.setText(System.currentTimeMillis() + "");
        builder.setView(edt);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bitmap bitmap = FileUtil.bytes2Bitmap(screenshot);
                MyUtils.CreateFile(Macro.POSTILFILE);
                File file = new File(Macro.POSTILFILE + edt.getText().toString() + ".jpg");
                try {
                    file.createNewFile();
                    FileUtil.saveBitmap(bitmap, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @Override
    public void callListener(int action, Object result) {

    }
}
