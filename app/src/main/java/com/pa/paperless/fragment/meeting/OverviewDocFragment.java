package com.pa.paperless.fragment.meeting;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;


/**
 * Created by Administrator on 2017/10/31.
 * 外部文档
 */

public class OverviewDocFragment extends BaseFragment implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView mCameraView;
    private CameraManager mCameraManager;
    private Context context;
    private CameraDevice mCameraDevice;
    private ImageReader mImageReader;
    private CaptureRequest.Builder mRequestBuilder;
    private SurfaceHolder mSurfaceHolder;
    private CameraCaptureSession mCameraSession;
    public static long now_time;
    private boolean isClosed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("F_life", "OverviewDocFragment.onCreateView :   --> ");
        View inflate = inflater.inflate(R.layout.overviewdoc_fragment, container, false);
        initView(inflate);
        context = getContext();
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        mSurfaceHolder = mCameraView.getHolder();
        mSurfaceHolder.addCallback(this);
        return inflate;
    }

    @Override
    public void onDestroy() {
        Log.e("F_life", "OverviewDocFragment.onDestroy :   --> ");
        super.onDestroy();
    }

    private void initView(View inflate) {
        mCameraView = inflate.findViewById(R.id.camera_surfaceView);
        inflate.findViewById(R.id.take_photo).setOnClickListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            //0是后摄 1是前摄  通过 mCameraManager.getCameraIdList()获取
            //第三个参数如果希望直接在当前线程中执行callback，则可将handler参数设为null。
            mCameraManager.openCamera("0", new CameraCallBack(), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * 照相按钮事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
            try {
                final CaptureRequest.Builder mBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                mBuilder.addTarget(mImageReader.getSurface());
                mBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                mBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                mBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
//            mImageReader.setOnImageAvailableListener(mSaveListener,null);
                mCameraSession.stopRepeating();
                mCameraSession.capture((mBuilder.build()), new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                        super.onCaptureProgressed(session, request, partialResult);
                        Log.e("MyLog", "OverviewDocFragment.onCaptureProgressed 100行:   --->>> ");
                    }

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        Log.e("MyLog", "OverviewDocFragment.onCaptureCompleted 106行:   --->>> ");
                        mCameraSession = session;
                        try {
                            mCameraSession.setRepeatingRequest(mRequestBuilder.build(), null, null);
                        } catch (CameraAccessException e) {
                            Log.e("MyLog", "OverviewDocFragment.onCaptureCompleted 112行:   --->>> " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
    }

    public class CameraCallBack extends CameraDevice.StateCallback implements ImageReader.OnImageAvailableListener {

        /**
         * *** **  相机打开  ** ****
         **/
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG, "CameraCallBack.onOpened :  相机打开 --> ");
            mCameraDevice = camera;
            mImageReader = ImageReader.newInstance(mCameraView.getWidth(), mCameraView.getHeight(), ImageFormat.JPEG, 7);
            Log.e("mCameraView", "com.pa.paperless.fragment.meeting_CameraCallBack.onOpened :  mCameraView.getWidth() --->>> "
                    + mCameraView.getWidth() + "mCameraView.getHeight()：" + mCameraView.getHeight());
            mImageReader.setOnImageAvailableListener(this, null);
            try {
                mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mRequestBuilder.addTarget(mSurfaceHolder.getSurface());
                mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mCameraSession = session;
                        mRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        mRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        mRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
                        try {
                            //控制预览方法
                            mCameraSession.setRepeatingRequest(mRequestBuilder.build(), null, null);
                            //控制拍照方法
//                            mCameraSession.capture(mRequestBuilder.build(),new ,null);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.e(TAG, "CameraCallBack.onConfigureFailed :   --> ");
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        /**
         * *** **  相机断开  ** ****
         **/
        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(TAG, "CameraCallBack.onDisconnected :  相机断开 --> ");
            camera.close();
            mCameraDevice = null;
        }

        /**
         * *** **  相机关闭  ** ****
         **/
        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            Log.e(TAG, "CameraCallBack.onClosed :  相机关闭 --> ");
            super.onClosed(camera);
            camera.close();
            mCameraDevice = null;
        }

        /**
         * *** **  相机出错  ** ****
         **/
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "CameraCallBack.onError :  相机出错 --> ");
            if (camera != null) {
                camera.close();
            }
        }

        @Override
        public void onImageAvailable(ImageReader reader) {
            File file = getTakePhotoFile(reader);
            String takePhotoFilePath = file.getAbsolutePath();
            Log.e(TAG, "CameraCallBack.onImageAvailable :  图片的临时缓存路径 --->>> " + takePhotoFilePath);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            EventBus.getDefault().post(new EventMessage(IDEventF.take_photo, bitmap));
        }

        /**
         * 获取图片并保存到文件目录中
         *
         * @param reader
         * @return
         */
        private File getTakePhotoFile(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];

            now_time = System.currentTimeMillis();
            //检查 MEETFILE 目录是否存在
            File file1 = new File(Macro.TAKE_PHOTO);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            // 使用IO流将照片写入指定文件
            File file = new File(Macro.TAKE_PHOTO + now_time + ".jpg");
            buffer.get(bytes);
            try (
                    FileOutputStream output = new FileOutputStream(file)
            ) {
                output.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                image.close();
            }
            return file;
        }

    }

    private final String TAG = "OverviewDocFragment-->";

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e(TAG, "OverviewDocFragment.onHiddenChanged :  是否隐藏 --> " + hidden);
//        if (hidden) {
//            if (mCameraDevice != null) {
//                mCameraDevice.close();
//            }
//        } else {
//            try {
//                if (mCameraManager != null) {
//                    mCameraManager.openCamera("0", new CameraCallBack(), null);
//                }
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onResume() {
        Log.e("F_life", "OverviewDocFragment.onResume :   --> ");
//        if (mCameraManager != null) {
//            try {
//                mCameraManager.openCamera("0", new CameraCallBack(), null);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.e("F_life", "OverviewDocFragment.onStop :   --> ");
//        if(mCameraDevice != null){
//            mCameraDevice.close();
//            isClosed = true;
//        }
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        Log.i("F_life", "OverviewDocFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "OverviewDocFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "OverviewDocFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "OverviewDocFragment.onStart :   --> ");
        super.onStart();
    }


    @Override
    public void onPause() {
        Log.i("F_life", "OverviewDocFragment.onPause :   --> ");
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        Log.i("F_life", "OverviewDocFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        Log.i("F_life", "OverviewDocFragment.onDetach :   --> ");
        super.onDetach();
    }
}
