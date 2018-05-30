package com.pa.paperless.fragment.meeting;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.pa.paperless.R;
import com.pa.paperless.activity.PeletteActivity;
import com.pa.paperless.constant.Macro;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * Created by Administrator on 2017/10/31.
 * 外部文档
 */

public class OverviewDocFragment extends BaseFragment implements  SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView mCameraView;
    private CameraManager mCameraManager;
    private Context context;
    private CameraDevice mCameraDevice;
    private ImageReader mImageReader;
    private CaptureRequest.Builder mRequestBuilder;
    private SurfaceHolder mSurfaceHolder;
    private CameraCaptureSession mCameraSession;
    private ImageView imageView;
    private RelativeLayout boottom_linear;
    private Button open_drawboard;
    private Button no_save;
    public static long now_time;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
        super.onDestroy();
    }


    private void initView(View inflate) {
        mCameraView = inflate.findViewById(R.id.camera_surfaceView);
        inflate.findViewById(R.id.take_photo).setOnClickListener(this);
        imageView = inflate.findViewById(R.id.iv);
        boottom_linear = inflate.findViewById(R.id.boottom_linear);
        boottom_linear.setVisibility(View.GONE);
        open_drawboard = inflate.findViewById(R.id.open_drawboard);
        no_save = inflate.findViewById(R.id.no_save);
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
            mCameraDevice = camera;
            mImageReader = ImageReader.newInstance(mCameraView.getWidth(), mCameraView.getHeight(), ImageFormat.JPEG, 7);
            Log.e("mCameraView", "com.pa.paperless.fragment.meeting_CameraCallBack.onOpened :  mCameraView.getWidth() --->>> "+mCameraView.getWidth()
            +"mCameraView.getHeight()："+mCameraView.getHeight());
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
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        /**
         * *** **  相机关闭  ** ****
         **/
        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
            camera.close();
            mCameraDevice = null;
        }

        /**
         * *** **  相机出错  ** ****
         **/
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            if (camera != null) {
                camera.close();
            }
        }

        @Override
        public void onImageAvailable(ImageReader reader) {
            File file = getTakePhotoFile(reader);
            String takePhotoFilePath = file.getAbsolutePath();
            Log.e("MyLog", "CameraCallBack.onImageAvailable 295行:  图片的路径 --->>> " + takePhotoFilePath);
            boottom_linear.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            setImgAnimatorEvent(file, bitmap);
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
            File file1 = new File(Macro.MEETFILE);
            if (!file1.exists()) {
                file1.mkdir();
            }
            // 使用IO流将照片写入指定文件
            File file = new File(Macro.MEETFILE + now_time + ".jpg");
            buffer.get(bytes);
            try (
                    FileOutputStream output = new FileOutputStream(file)) {
                output.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                image.close();
            }
            return file;
        }

        /**
         * 预览图片的点击事件处理
         *
         * @param file
         */
        private void setImgAnimatorEvent(final File file, final Bitmap bitmap) {
            boottom_linear.setVisibility(View.VISIBLE);
            final ObjectAnimator animator = ObjectAnimator.ofFloat(boottom_linear, "alpha", 1.0f, 0.0f);
            animator.setDuration(5000);
            animator.start();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    boottom_linear.setVisibility(View.GONE);
                }
            });
            open_drawboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PeletteActivity.class);
                    PeletteActivity.ISFROMDOCUMENTFRAGMENT = true;//设置是从外部文档打开
                    startActivity(intent);
                }
            });
            no_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (file.exists()) {
                        file.delete();
                    }
                    boottom_linear.setVisibility(View.GONE);
                }
            });
        }
    }

}
