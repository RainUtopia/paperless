package com.wind.myapplication;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class CameraDemo extends Activity implements SurfaceHolder.Callback,
        PreviewCallback {

    private final String TAG = "CameraDemo -->";
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Parameters parameters;

    public static int width = 1920;//2048
    public static int height = 1080;//1536
    int framerate = 25;
    int biterate = width * height * 3;
    private int imageFormat = -1;
    private static int yuvqueuesize = 10;

    public static final ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(
            yuvqueuesize); // ԭʼ֡����

    private AvcEncoder avcCodec;

//    private JNIHandler jni = new JNIHandler();


    private NativeUtil nativeUtil;
    private ArrayList allRes = new ArrayList(), devIds = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        nativeUtil = NativeUtil.getInstance();
        EventBus.getDefault().register(this);
//        jni.InitAndCapture(0,  2);
//        Log.i(TAG, "---------------------------------------support avcCodec:" + SupportAvcCodec());
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = getBackCamera();
        startcamera(camera);
        try {
            avcCodec = new AvcEncoder(width, height, framerate, biterate, imageFormat);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        avcCodec.StartEncoderThread();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            avcCodec.StopThread();
            /** ************ ******  停止资源操作  ****** ************ **/
            nativeUtil.stopResourceOperate(allRes, devIds);
            /** ************ ******  释放播放资源  ****** ************ **/
            nativeUtil.mediaDestroy(0);
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY:
                if (message.getType() == 3) {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        Log.v(TAG, "rawDataLen=" + data.length);
        putYUVData(data, data.length);
    }


    // �����ݼ������
    public void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 5) {
            YUVQueue.poll();
        }

        YUVQueue.add(buffer);
    }


    // �Ƿ�֧�ָ߼��������
    @SuppressLint("NewApi")
    private boolean SupportAvcCodec() {
        if (Build.VERSION.SDK_INT >= 18) {
            for (int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);

                String[] types = codecInfo.getSupportedTypes();
                for (String type : types) {
                    Log.i(TAG, "SupportAvcCodec:" + type);
                }
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase("video/avc")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void loopSupportSize() {
        for (int num = 0; num < parameters.getSupportedPreviewSizes().size(); num++) {
            Log.i(TAG, parameters.getSupportedPreviewSizes().get(num).width
                    + "*"
                    + parameters.getSupportedPreviewSizes().get(num).height);
        }
    }

    private void startcamera(Camera mCamera) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(this);
//				 mCamera.setDisplayOrientation(90);
                if (parameters == null) {
                    parameters = mCamera.getParameters();
                }

                loopSupportSize();

                parameters = mCamera.getParameters();
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);// �����Խ�
                //parameters.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);// ��ƽ�⣺�Զ�
                //parameters.setFlashMode(Parameters.FLASH_MODE_ON);// �����ģʽ���Զ�
                //parameters.setSceneMode(Parameters.SCENE_MODE_AUTO);// ����ģʽ���Զ�
                parameters.setPreviewFrameRate(30);
                checkSupportColorFormat();
                Log.i(TAG, "Camera PreviewFormat=" + (imageFormat == ImageFormat.NV21 ? "NV21" : imageFormat == ImageFormat.YV12 ? "YV12" : imageFormat));
                parameters.setPreviewFormat(imageFormat);//NV21 YV12
                parameters.setPreviewSize(width, height);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkSupportColorFormat() {
        List<Integer> previewFormatsSizes = parameters.getSupportedPreviewFormats();

        if (-1 != previewFormatsSizes.indexOf(ImageFormat.YV12)) {
            imageFormat = ImageFormat.YV12;
        } else if (-1 != previewFormatsSizes.indexOf(ImageFormat.NV21)) {
            imageFormat = ImageFormat.NV21;
        } else {
            imageFormat = -1;
            return;
        }
    }

    // ��ȡcamera����ʵ���İ�ȫ����
    @TargetApi(9)
    private Camera getBackCamera() {
        int cameras = Camera.getNumberOfCameras();// ��ȡ����ͷ������һ����������0��ǰ��1
        Log.i(TAG, "camera num:" + cameras);
        Camera c = null;
        try {
            c = Camera.open(0); // ��ȡcameraʵ��
            setCameraOrientation(c);
            c.cancelAutoFocus();// �Զ��Խ�
        } catch (Exception e) {// ��������ã�����ʹ�û򲻴��ڣ�
            e.printStackTrace();
        }

        return c;
    }


    /* �������Ԥ������ת���� */
    public void setCameraOrientation(Camera camera) {

        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = this.getWindowManager().getDefaultDisplay()
                .getRotation();
        Log.w(TAG, "rotation : " + rotation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }


        Log.w(TAG, "degrees : " + degrees);
        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }


}
