package com.wind.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pa.paperless.R;

import java.io.File;

/**
 * @author Gowcage
 */
public class Main extends Activity implements OnClickListener {

    final String TAG = "Main-->";
    final int REQUEST_CODE = 1;// >=0

    int width, height, dpi, bitrate, VideoQuality = 1;//VideoQuality:1/2/3
    float density;

    MediaProjectionManager manager;
    MediaProjection projection;
    ScreenRecorder recorder;
    File file;

    Button recordbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_main);

        if (Build.VERSION.SDK_INT >= 23) {//API>6.0
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
//            for (String str : permissions) {
//                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
//                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
//                }
//            }
        }

        recordbtn = (Button) findViewById(R.id.recordbtn);
        recordbtn.setOnClickListener(this);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // ��Ļ��ȣ����أ�
        height = metric.heightPixels; // ��Ļ�߶ȣ����أ�
        Log.i(TAG,"w:"+width+"/h:"+height);
        density = metric.density; // ��Ļ�ܶȣ�0.75 / 1.0 / 1.5��
        dpi = metric.densityDpi; // ��Ļ�ܶ�DPI��120 / 160 / 240��
        bitrate = width * height * VideoQuality;//������/����

        // 1: �õ� MediaProjectionManager ʵ��
        manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (recorder != null) {
            Log.w(TAG, "quit");
            recorder.quit();
            recorder = null;
            recordbtn.setText("���¿�ʼ¼��");
        } else {
            // 2: ������Ļ��׽����
            Intent intent = manager.createScreenCaptureIntent();
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    // 3:ͨ�� onActivityResult ���ؽ����ȡ MediaProjection ʵ��
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
            projection = manager.getMediaProjection(resultCode, data);

        if (projection == null) {
            Log.e(TAG, "media projection is null");
            return;
        }

        file = new File(Environment.getExternalStorageDirectory(), "record_"
                + width + "x" + height + "_" + System.currentTimeMillis() + ".mp4");
        recorder = new ScreenRecorder(width, height, bitrate, 1, projection, file.getAbsolutePath());
        recorder.start();//����¼���߳�
        recordbtn.setText("����¼��");
        Toast.makeText(this, "��Ļ¼����...", Toast.LENGTH_LONG).show();
        moveTaskToBack(true);//�������Ƶ���̨
//        }
    }

    /**
     * ��ʼ¼��
     */
    public void startRecordScreen() {
        if (recorder == null) {
            // 2: ����intent����startActivityForResult
            Intent intent = manager.createScreenCaptureIntent();
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /**
     * ��ȡ����
     */
    /*public ByteBuffer getScreenRawData() {
        ByteBuffer rawData = null;
        rawData = recorder.getByteBufferData();
        return rawData;
    }*/

    /**
     * ����¼��
     */
    public void stopRecordScreen() {
        if (recorder != null) {
            Log.w(TAG, "quit");
            recorder.quit();
            recorder = null;
            recordbtn.setText("���¿�ʼ¼��");
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (recorder != null) {
            Log.w(TAG, "quit");
            recorder.quit();
            recorder = null;
        }
    }

}
