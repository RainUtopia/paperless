package org.libsdl.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fab.FabInfo;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.service.FabService;
import com.pa.paperless.service.NativeService;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.Dispose;
import com.wind.myapplication.CameraDemo;
import com.wind.myapplication.NativeUtil;
import com.wind.myapplication.ScreenRecorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY;
import static com.pa.paperless.service.NativeService.nativeUtil;

/**
 * SDL Activity
 */
public class SDLActivity extends Activity {

    //    NativeUtil nativeUtil;
    public static InterfacePlaymedia.pbui_Type_MeetMediaPlay meetMediaPlay;
    public static InterfaceStream.pbui_Type_MeetStreamPlay meetStreamPlay;
    private MediaProjectionManager manager;
    private MediaProjection projection;
    private int width, height, dpi, bitrate, VideoQuality = 1;//VideoQuality:1/2/3
    private ScreenRecorder recorder;

    public static int getLineNumber(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0)
            return -1; //
        return trace[0].getLineNumber();
    }

    private static final String TAG = "SDL";

    // Keep track of the paused state
    public static boolean mIsPaused, mIsSurfaceReady, mHasFocus;
    public static boolean mExitCalledFromJava;

    public static int screenw, screenh;
    /**
     * If shared libraries (e.g. SDL or the native application) could not be loaded.
     */
    public static boolean mBrokenLibraries;

    // If we want to separate mouse and touch events.
    //  This is only toggled in native code when a hint is set!
    public static boolean mSeparateMouseAndTouch;

    // Main components
    protected static SDLActivity mSingleton;
    protected static SDLSurface mSurface;
    protected static View mTextEdit;
    protected static ViewGroup mLayout;
    protected static SDLJoystickHandler mJoystickHandler;

    // This is what SDL runs in. It invokes SDL_main(), eventually
    protected static Thread mSDLThread;

    // Audio
    protected static AudioTrack mAudioTrack;

    /**
     * This method is called by SDL before starting the native application thread.
     * It can be overridden to provide the arguments after the application name.
     * The default implementation returns an empty array. It never returns null.
     *
     * @return arguments for the native application.
     */
    protected String[] getArguments() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "back_main.bmp";
        return new String[]{path};
    }


    public static void initialize() {
        // The static nature of the singleton and Android quirkyness force us to initialize everything here
        // Otherwise, when exiting the app and returning to it, these variables *keep* their pre exit values
        mSingleton = null;
        mSurface = null;
        mTextEdit = null;
        mLayout = null;
        mJoystickHandler = null;
        mSDLThread = null;
        mAudioTrack = null;
        mExitCalledFromJava = false;
        mBrokenLibraries = false;
        mIsPaused = false;
        mIsSurfaceReady = false;
        mHasFocus = true;
    }


    @Override
    protected void onStart() {
        Log.e("SDL_life", "onStart :   --->>> ");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.e("SDL_life", "onStop :   --->>> ");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.e("SDL_life", "onRestart :   --->>> ");
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseMediaRes();
        Log.e("SDL_life", "SDLActivity.onBackPressed:   --->>> ");
    }

    public void releaseMediaRes() {
        Log.v(TAG, "releaseMediaRes: ");
        List<Integer> a = new ArrayList<Integer>();
        List<Integer> b = new ArrayList<Integer>();
        a.add(0);
        b.add(0);
        /** ************ ******  停止资源操作  ****** ************ **/
        nativeUtil.stopResourceOperate(a, b);
        /** ************ ******  释放播放资源  ****** ************ **/
//        nativeUtil.mediaDestroy(0);
        moveTaskToBack(true);
    }

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // �屏幕宽度（像素）
        height = metric.heightPixels; // �屏幕高度（像素）
        Log.v(TAG, "ScreenSize: width=" + width + " height=" + height);
        if (width > 1920)
            width = 1920;
        if (height > 1080)
            height = 1080;
        Log.i(TAG, "w:" + width + "/h:" + height);
        dpi = metric.densityDpi; // �屏幕密度DPI（120 / 160 / 240）
        bitrate = width * height * VideoQuality;//�比特率/码率
    }

    // Setup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Device: " + Build.DEVICE);
        Log.v(TAG, "Model: " + Build.MODEL);
        Log.v(TAG, "onCreate(): " + mSingleton);
        super.onCreate(savedInstanceState);
        initScreenParam();
        EventBus.getDefault().register(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenw = metric.widthPixels; // �屏幕宽度（像素）
        screenh = metric.heightPixels; // �屏幕高度（像素）
        Log.e("SDL", "Screen  --->>> w: " + screenw + ", h: " + screenh);

        screenw = 1920;//screenw * 2 / 2;
        screenh = 1080;//screenh * 2 / 2;

        EventBus.getDefault().post(new EventMessage(1, 1));
        try {
            int action = getIntent().getIntExtra("action", 0);
            byte[] data = getIntent().getByteArrayExtra("data");
            if (data != null && data.length > 0) {
                switch (action) {
                    case IDEventMessage.MEDIA_PLAY_INFORM:
                        Log.e(TAG, "org.libsdl.app_SDLActivity.onCreate :  媒体播放通知EventBus --->>> ");

                        meetMediaPlay = InterfacePlaymedia.pbui_Type_MeetMediaPlay.parseFrom(data);
                        break;
                    case IDEventMessage.PLAY_STREAM_NOTIFY:
                        Log.i("stream_log", "SDLActivity.onCreate :  流播放通知 --->>> ");
                        meetStreamPlay = InterfaceStream.pbui_Type_MeetStreamPlay.parseFrom(data);
                        break;
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

//        nativeUtil = NativeUtil.getInstance();
        SDLActivity.initialize();
        // So we can call stuff from static callbacks
        mSingleton = this;

        // Load shared libraries
        String errorMsgBrokenLib = "";

        if (mBrokenLibraries) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("An error occurred while trying to start the application. Please try again and/or reinstall."
                    + System.getProperty("line.separator")
                    + System.getProperty("line.separator")
                    + "Error: " + errorMsgBrokenLib);
            dlgAlert.setTitle("SDL Error");
            dlgAlert.setPositiveButton("Exit",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close current activity
                            SDLActivity.mSingleton.finish();
                        }
                    });
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();

            return;
        }

        // Set up the surface
        mSurface = new SDLSurface(getApplication());


        if (Build.VERSION.SDK_INT >= 12) {
            mJoystickHandler = new SDLJoystickHandler_API12();
        } else {
            mJoystickHandler = new SDLJoystickHandler();
        }

        mLayout = new AbsoluteLayout(this);
        mLayout.addView(mSurface);

        setContentView(mLayout);
        Button btn = new Button(this);

        btn.setText("go Camera");
        btn.setTextSize(30);
//        mLayout.addView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseMediaRes();
            }
        });
        // Get filename from "Open with" of another application
        Intent intent = getIntent();

        if (intent != null && intent.getData() != null) {
            String filename = intent.getData().getPath();
            if (filename != null) {
                Log.v(TAG, "Got filename: " + filename);
                SDLActivity.onNativeDropFile(filename);
            }
        }
        Log.i("onCreate", "onCreate finish!");

    }

    // Events
    @Override
    protected void onPause() {
        Log.v("SDL_life", "onPause()");
        super.onPause();
        if (SDLActivity.mBrokenLibraries) {
            return;
        }
        SDLActivity.handlePause();
    }

    @Override
    protected void onResume() {
        Log.e("SDL_life", "onResume :   --> ");
        super.onResume();

        if (SDLActivity.mBrokenLibraries) {
            return;
        }

        SDLActivity.handleResume();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        Log.i("onWindowFocusChanged", "onWindowFocusChanged start!");
        super.onWindowFocusChanged(hasFocus);
        Log.i("onWindowFocusChanged", "onWindowFocusChanged finish!");
        //return;

        //super.onWindowFocusChanged(hasFocus);
        Log.v(TAG, "onWindowFocusChanged(): " + hasFocus);


        if (SDLActivity.mBrokenLibraries) {
            return;
        }

        SDLActivity.mHasFocus = hasFocus;
        if (hasFocus) {
            SDLActivity.handleResume();
        }
    }

    @Override
    public void onLowMemory() {
        Log.v(TAG, "onLowMemory()");
        super.onLowMemory();

        if (SDLActivity.mBrokenLibraries) {
            return;
        }

        SDLActivity.nativeLowMemory();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new EventMessage(0, 0));
        releaseMediaRes();
        Log.v("SDL_life", "onDestroy()");
        if (SDLActivity.mBrokenLibraries) {
            super.onDestroy();
            // Reset everything in case the user re opens the app
            onNativeSurfaceDestroyed();
            SDLActivity.initialize();
            return;
        }

        // Send a quit message to the application
        SDLActivity.mExitCalledFromJava = true;
//        SDLActivity.nativeQuit();
        onNativeSurfaceDestroyed();

        // Now wait for the SDL thread to quit
        if (SDLActivity.mSDLThread != null) {
            try {
                SDLActivity.mSDLThread.join();
            } catch (Exception e) {
                Log.v(TAG, "Problem stopping thread: " + e);
            }
            SDLActivity.mSDLThread = null;

            //Log.v(TAG, "Finished waiting for SDL thread");
        }
        super.onDestroy();
        // Reset everything in case the user re opens the app
        SDLActivity.initialize();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("dispatchKeyEvent", "dispatchKeyEvent finish!");
        if (SDLActivity.mBrokenLibraries) {
            return false;
        }

        int keyCode = event.getKeyCode();
        // Ignore certain special keys so they're handled by Android
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_CAMERA ||
                keyCode == 168 || /* API 11: KeyEvent.KEYCODE_ZOOM_IN */
                keyCode == 169 /* API 11: KeyEvent.KEYCODE_ZOOM_OUT */
                ) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Called by onPause or surfaceDestroyed. Even if surfaceDestroyed
     * is the first to be called, mIsSurfaceReady should still be set
     * to 'true' during the call to onPause (in a usual scenario).
     */
    public static void handlePause() {
        Log.i("handlePause", "handlePause finish!");
        if (!SDLActivity.mIsPaused && SDLActivity.mIsSurfaceReady) {
            SDLActivity.mIsPaused = true;
            SDLActivity.nativePause();
            mSurface.enableSensor(Sensor.TYPE_ACCELEROMETER, false);
        }
    }

    /**
     * Called by onResume or surfaceCreated. An actual resume should be done only when the surface is ready.
     * Note: Some Android variants may send multiple surfaceChanged events, so we don't need to resume
     * every time we get one of those events, only if it comes after surfaceDestroyed
     */
    public static void handleResume() {
        Log.i(TAG, "SDLActivity.handleResume :  finish!  ");
        if (SDLActivity.mIsPaused && SDLActivity.mIsSurfaceReady && SDLActivity.mHasFocus) {
            SDLActivity.mIsPaused = false;
            SDLActivity.nativeResume();
            mSurface.handleResume();
        }
    }

    /* The native thread has finished */
    /*public static void handleNativeExit() {
        SDLActivity.mSDLThread = null;
        mSingleton.finish();
    }*/


    // Messages from the SDLMain thread
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_UNUSED = 2;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    static final int COMMAND_SET_KEEP_SCREEN_ON = 5;

    protected static final int COMMAND_USER = 0x8000;

    /**
     * This method is called by SDL if SDL did not handle a message itself.
     * This happens if a received message contains an unsupported command.
     * Method can be overwritten to handle Messages in a different class.
     *
     * @param command the command of the message.
     * @param param   the parameter of the message. May be null.
     * @return if the message was handled in overridden method.
     */
    protected boolean onUnhandledMessage(int command, Object param) {
        return false;
    }

    /**
     * A Handler class for Messages from native SDL applications.
     * It uses current Activities as target (e.g. for the title).
     * static to prevent implicit references to enclosing object.
     */
    protected static class SDLCommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Log.i("handleMessage", "handleMessage start !");
            Context context = getContext();
            Log.i("handleMessage", "handleMessage 2!");
            if (context == null) {
                Log.e(TAG, "error handling message, getContext() returned null");
                return;
            }
            Log.i("handleMessage", "handleMessage 3!" + getLineNumber(new Exception()));
            switch (msg.arg1) {
                case COMMAND_CHANGE_TITLE:
                    Log.i("handleMessage", "handleMessage 3!" + getLineNumber(new Exception()));
                    if (context instanceof Activity) {
                        ((Activity) context).setTitle((String) msg.obj);
                    } else {
                        Log.e(TAG, "error handling message, getContext() returned no Activity");
                    }
                    break;
                case COMMAND_TEXTEDIT_HIDE:
                    Log.i("handleMessage", "handleMessage 3!" + getLineNumber(new Exception()));
                    if (mTextEdit != null) {
                        mTextEdit.setVisibility(View.GONE);

                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mTextEdit.getWindowToken(), 0);
                    }
                    break;
                case COMMAND_SET_KEEP_SCREEN_ON: {
                    Log.i("handleMessage", "handleMessage 3!" + getLineNumber(new Exception()));
                    Window window = ((Activity) context).getWindow();
                    if (window != null) {
                        if ((msg.obj instanceof Integer) && (((Integer) msg.obj).intValue() != 0)) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                    }
                    break;
                }
                default:
                    Log.i("handleMessage", "handleMessage 3!" + getLineNumber(new Exception()));
                    if ((context instanceof SDLActivity) && !((SDLActivity) context).onUnhandledMessage(msg.arg1, msg.obj)) {
                        Log.e(TAG, "error handling message, command is " + msg.arg1);
                    }
            }
        }
    }

    // Handler for the messages
    Handler commandHandler = new SDLCommandHandler();

    // Send a message from the SDLMain thread
    boolean sendCommand(int command, Object data) {
        Log.i("sendCommand", "sendCommand 3!");
        Message msg = commandHandler.obtainMessage();
        msg.arg1 = command;
        msg.obj = data;
        return commandHandler.sendMessage(msg);
    }

    // C functions we call
    public static native int nativeInit(Object arguments);

    public static native void nativeLowMemory();

    public static native void nativeQuit();

    public static native void nativePause();

    public static native void nativeResume();

    public static native void onNativeDropFile(String filename);

    public static native void onNativeResize(int x, int y, int format, float rate);

    public static native int onNativePadDown(int device_id, int keycode);

    public static native int onNativePadUp(int device_id, int keycode);

    public static native void onNativeJoy(int device_id, int axis,
                                          float value);

    public static native void onNativeHat(int device_id, int hat_id,
                                          int x, int y);

    public static native void onNativeKeyDown(int keycode);

    public static native void onNativeKeyUp(int keycode);

    public static native void onNativeKeyboardFocusLost();

    public static native void onNativeMouse(int button, int action, float x, float y);

    public static native void onNativeTouch(int touchDevId, int pointerFingerId,
                                            int action, float x,
                                            float y, float p);

    public static native void onNativeAccel(float x, float y, float z);

    public static native void onNativeSurfaceChanged();

    public static native void onNativeSurfaceDestroyed();

    public static native void nativeFlipBuffers();

    public static native int nativeAddJoystick(int device_id, String name,
                                               int is_accelerometer, int nbuttons,
                                               int naxes, int nhats, int nballs);

    public static native int nativeRemoveJoystick(int device_id);

    public static native String nativeGetHint(String name);

    /**
     * This method is called by SDL using JNI.
     */
    public static void flipBuffers() {
        SDLActivity.nativeFlipBuffers();
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static boolean setActivityTitle(String title) {
        Log.i("setActivityTitle", "setActivityTitle 3!");
        return true;
        // Called from SDLMain() thread and can't directly affect the view
        //return mSingleton.sendCommand(COMMAND_CHANGE_TITLE, title);
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static boolean sendMessage(int command, int param) {
        Log.i("sendMessage", "sendMessage 3!");
        return true;
        //return mSingleton.sendCommand(command, Integer.valueOf(param));
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static Context getContext() {
        Log.i("getContext", "getContext 3!" + mSingleton);
        return mSingleton;
    }

    /**
     * This method is called by SDL using JNI.
     *
     * @return result of getSystemService(name) but executed on UI thread.
     */
    public Object getSystemServiceFromUiThread(final String name) {

//    	Log.i( "getSystemServiceFromUiThread", "getSystemServiceFromUiThread 3!" + name);

        final Object lock = new Object();
        final Object[] results = new Object[2]; // array for writable variables
        synchronized (lock) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock) {
                        results[0] = getSystemService(name);
                        results[1] = Boolean.TRUE;
                        lock.notify();
                    }
                }
            });
            if (results[1] == null) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return results[0];
    }

    static class ShowTextInputTask implements Runnable {
        /*
         * This is used to regulate the pan&scan method to have some offset from
         * the bottom edge of the input region and the top edge of an input
         * method (soft keyboard)
         */
        static final int HEIGHT_PADDING = 15;

        public int x, y, w, h;

        public ShowTextInputTask(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @Override
        public void run() {
            Log.i("ShowTextInputTask", "ShowTextInputTask 3!");
            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                    w, h + HEIGHT_PADDING, x, y);

            Log.i("ShowTextInputTask", "ShowTextInputTask 4!");
            if (mTextEdit == null) {
                mTextEdit = new DummyEdit(getContext());

                mLayout.addView(mTextEdit, params);
            } else {
                mTextEdit.setLayoutParams(params);
            }

            mTextEdit.setVisibility(View.VISIBLE);
            mTextEdit.requestFocus();

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mTextEdit, 0);
        }
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static boolean showTextInput(int x, int y, int w, int h) {
        // Transfer the task to the main thread as a Runnable
        return mSingleton.commandHandler.post(new ShowTextInputTask(x, y, w, h));
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static Surface getNativeSurface() {
        return SDLActivity.mSurface.getNativeSurface();
    }

    // Audio

    /**
     * This method is called by SDL using JNI.
     */
    public static int audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
        int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);

        Log.i("audioInit", "audioInit 3!");

        Log.v(TAG, "SDL audio: wanted " + (isStereo ? "stereo" : "mono") + " " + (is16Bit ? "16-bit" : "8-bit") + " " + (sampleRate / 1000f) + "kHz, " + desiredFrames + " frames buffer");

        // Let the user pick a larger buffer if they really want -- but ye
        // gods they probably shouldn't, the minimums are horrifyingly high
        // latency already
        desiredFrames = Math.max(desiredFrames, (AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize - 1) / frameSize);

        if (mAudioTrack == null) {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                    channelConfig, audioFormat, desiredFrames * frameSize, AudioTrack.MODE_STREAM);

            // Instantiating AudioTrack can "succeed" without an exception and the track may still be invalid
            // Ref: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/AudioTrack.java
            // Ref: http://developer.android.com/reference/android/media/AudioTrack.html#getState()

            if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
                Log.e(TAG, "Failed during initialization of Audio Track");
                mAudioTrack = null;
                return -1;
            }

//            mAudioTrack.play();
        }

        Log.v(TAG, "SDL audio: got " + ((mAudioTrack.getChannelCount() >= 2) ? "stereo" : "mono") + " " + ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit" : "8-bit") + " " + (mAudioTrack.getSampleRate() / 1000f) + "kHz, " + desiredFrames + " frames buffer");

        return 0;
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioWriteShortBuffer(short[] buffer) {
        Log.i("audioWriteShortBuffer", "audioWriteShortBuffer 3!");
        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioWriteByteBuffer(byte[] buffer) {
        Log.i("audioWriteByteBuffer", "audioWriteByteBuffer 3!");
        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioQuit() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack = null;
        }
    }

    // Input

    /**
     * This method is called by SDL using JNI.
     *
     * @return an array which may be empty but is never null.
     */
    public static int[] inputGetInputDeviceIds(int sources) {

        Log.i("inputGetInputDeviceIds", "inputGetInputDeviceIds 3!");

        int[] ids = InputDevice.getDeviceIds();
        int[] filtered = new int[ids.length];
        int used = 0;
        for (int i = 0; i < ids.length; ++i) {
            InputDevice device = InputDevice.getDevice(ids[i]);
            if ((device != null) && ((device.getSources() & sources) != 0)) {
                filtered[used++] = device.getId();
            }
        }
        return Arrays.copyOf(filtered, used);
    }

    // Joystick glue code, just a series of stubs that redirect to the SDLJoystickHandler instance
    public static boolean handleJoystickMotionEvent(MotionEvent event) {
        return mJoystickHandler.handleMotionEvent(event);
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void pollInputDevices() {
        if (SDLActivity.mSDLThread != null) {
            mJoystickHandler.pollInputDevices();
        }
    }

    // APK extension files support

    /**
     * com.android.vending.expansion.zipfile.ZipResourceFile object or null.
     */
    private Object expansionFile;

    /**
     * com.android.vending.expansion.zipfile.ZipResourceFile's getInputStream() or null.
     */
    private Method expansionFileMethod;

    /**
     * This method is called by SDL using JNI.
     */
    public InputStream openAPKExtensionInputStream(String fileName) throws IOException {
        Log.e("MyLog", "SDLActivity.openAPKExtensionInputStream 787行:   --->>> ");
        // Get a ZipResourceFile representing a merger of both the main and patch files
        if (expansionFile == null) {
            Integer mainVersion = Integer.valueOf(nativeGetHint("SDL_ANDROID_APK_EXPANSION_MAIN_FILE_VERSION"));
            Integer patchVersion = Integer.valueOf(nativeGetHint("SDL_ANDROID_APK_EXPANSION_PATCH_FILE_VERSION"));

            try {
                // To avoid direct dependency on Google APK extension library that is
                // not a part of Android SDK we access it using reflection
                expansionFile = Class.forName("com.android.vending.expansion.zipfile.APKExpansionSupport")
                        .getMethod("getAPKExpansionZipFile", Context.class, int.class, int.class)
                        .invoke(null, this, mainVersion, patchVersion);

                expansionFileMethod = expansionFile.getClass()
                        .getMethod("getInputStream", String.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                expansionFile = null;
                expansionFileMethod = null;
            }
        }

        // Get an input stream for a known file inside the expansion file ZIPs
        InputStream fileStream;
        try {
            fileStream = (InputStream) expansionFileMethod.invoke(expansionFile, fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
            fileStream = null;
        }

        if (fileStream == null) {
            throw new IOException();
        }

        return fileStream;
    }

    // Messagebox

    /**
     * Result of current messagebox. Also used for blocking the calling thread.
     */
    protected final int[] messageboxSelection = new int[1];

    /**
     * Id of current dialog.
     */
    protected int dialogs = 0;

    /**
     * This method is called by SDL using JNI.
     * Shows the messagebox from UI thread and block calling thread.
     * buttonFlags, buttonIds and buttonTexts must have same length.
     *
     * @param buttonFlags array containing flags for every button.
     * @param buttonIds   array containing id for every button.
     * @param buttonTexts array containing text for every button.
     * @param colors      null for default or array of length 5 containing colors.
     * @return button id or -1.
     */
    public int messageboxShowMessageBox(
            final int flags,
            final String title,
            final String message,
            final int[] buttonFlags,
            final int[] buttonIds,
            final String[] buttonTexts,
            final int[] colors) {

        messageboxSelection[0] = -1;

        // sanity checks

        if ((buttonFlags.length != buttonIds.length) && (buttonIds.length != buttonTexts.length)) {
            return -1; // implementation broken
        }

        // collect arguments for Dialog

        final Bundle args = new Bundle();
        args.putInt("flags", flags);
        args.putString("title", title);
        args.putString("message", message);
        args.putIntArray("buttonFlags", buttonFlags);
        args.putIntArray("buttonIds", buttonIds);
        args.putStringArray("buttonTexts", buttonTexts);
        args.putIntArray("colors", colors);

        // trigger Dialog creation on UI thread

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog(dialogs++, args);
            }
        });

        // block the calling thread

        synchronized (messageboxSelection) {
            try {
                messageboxSelection.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return -1;
            }
        }

        // return selected value

        return messageboxSelection[0];
    }

    @Override
    protected Dialog onCreateDialog(int ignore, Bundle args) {

        // TODO set values from "flags" to messagebox dialog

        // get colors
        Log.e("MyLog", "SDLActivity.onCreateDialog 906行:   --->>> ");
        int[] colors = args.getIntArray("colors");
        int backgroundColor;
        int textColor;
        int buttonBorderColor;
        int buttonBackgroundColor;
        int buttonSelectedColor;
        if (colors != null) {
            int i = -1;
            backgroundColor = colors[++i];
            textColor = colors[++i];
            buttonBorderColor = colors[++i];
            buttonBackgroundColor = colors[++i];
            buttonSelectedColor = colors[++i];
        } else {
            backgroundColor = Color.TRANSPARENT;
            textColor = Color.TRANSPARENT;
            buttonBorderColor = Color.TRANSPARENT;
            buttonBackgroundColor = Color.TRANSPARENT;
            buttonSelectedColor = Color.TRANSPARENT;
        }

        // create dialog with title and a listener to wake up calling thread

        final Dialog dialog = new Dialog(this);
        dialog.setTitle(args.getString("title"));
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface unused) {
                synchronized (messageboxSelection) {
                    messageboxSelection.notify();
                }
            }
        });

        // create text

        TextView message = new TextView(this);
        message.setGravity(Gravity.CENTER);
        message.setText(args.getString("message"));
        if (textColor != Color.TRANSPARENT) {
            message.setTextColor(textColor);
        }

        // create buttons

        int[] buttonFlags = args.getIntArray("buttonFlags");
        int[] buttonIds = args.getIntArray("buttonIds");
        String[] buttonTexts = args.getStringArray("buttonTexts");

        final SparseArray<Button> mapping = new SparseArray<Button>();

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);
        for (int i = 0; i < buttonTexts.length; ++i) {
            Button button = new Button(this);
            final int id = buttonIds[i];
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageboxSelection[0] = id;
                    dialog.dismiss();
                }
            });
            if (buttonFlags[i] != 0) {
                // see SDL_messagebox.h
                if ((buttonFlags[i] & 0x00000001) != 0) {
                    mapping.put(KeyEvent.KEYCODE_ENTER, button);
                }
                if ((buttonFlags[i] & 0x00000002) != 0) {
                    mapping.put(111, button); /* API 11: KeyEvent.KEYCODE_ESCAPE */
                }
            }
            button.setText(buttonTexts[i]);
            if (textColor != Color.TRANSPARENT) {
                button.setTextColor(textColor);
            }
            if (buttonBorderColor != Color.TRANSPARENT) {
                // TODO set color for border of messagebox button
            }
            if (buttonBackgroundColor != Color.TRANSPARENT) {
                Drawable drawable = button.getBackground();
                if (drawable == null) {
                    // setting the color this way removes the style
                    button.setBackgroundColor(buttonBackgroundColor);
                } else {
                    // setting the color this way keeps the style (gradient, padding, etc.)
                    drawable.setColorFilter(buttonBackgroundColor, PorterDuff.Mode.MULTIPLY);
                }
            }
            if (buttonSelectedColor != Color.TRANSPARENT) {
                // TODO set color for selected messagebox button
            }
            buttons.addView(button);
        }

        // create content

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(message);
        content.addView(buttons);
        if (backgroundColor != Color.TRANSPARENT) {
            content.setBackgroundColor(backgroundColor);
        }

        // add content to dialog and return

        dialog.setContentView(content);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                Button button = mapping.get(keyCode);
                if (button != null) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        button.performClick();
                    }
                    return true; // also for ignored actions
                }
                return false;
            }
        });

        return dialog;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.STOP_PLAY:
                if (message.getType() == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    releaseMediaRes();
                }
                break;
        }
    }
}


/**
 * Simple nativeInit() runnable
 */
class SDLMain implements Runnable {
    private NativeUtil fdd;

    @Override
    public void run() {
        Log.e("MyLog", "SDLMain.run 1044行:   --->>> ");
        // Runs SDL_main()

//    	 Log.i( "SDLMain", "SDLMain start!");
//        SDLActivity.nativeInit(SDLActivity.mSingleton.getArguments());
//        Log.i( "SDLMain", "SDLMain finish!");
//        //Log.v("SDL", "SDL thread terminated");
//
//        fdd = NativeUtil.getInstance();
//        fdd.javaInitSys();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }//休眠3秒
//                /**
//                 * 要执行的操作
//                 */
//            }
//        }.start();


        try {
            nativeUtil.initvideores(/*0, 0, SDLActivity.screenw, SDLActivity.screenh*/);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}


/**
 * SDLSurface. This is what we draw on, so we need to know when it's created
 * in order to do anything useful.
 * <p>
 * Because of this, that's where we set up the SDL thread
 */
class SDLSurface extends SurfaceView implements SurfaceHolder.Callback,
        View.OnKeyListener, View.OnTouchListener, SensorEventListener, View.OnClickListener {

    // Sensors
    protected static SensorManager mSensorManager;
    protected static Display mDisplay;

    // Keep track of the surface size to normalize touch events
    protected static float mWidth, mHeight;

    //add by gowcage
    private Context context;
    private PopupWindow popupWindow;
    //    private NativeUtil nativeUtil;
    private boolean isPause = false, isShare = false;//isShare,是否处于共享中
    // 参见Interface_main.proto中的播放进度通知
    private int mediaId = 0, status = 2, per = 0,//当前播放的媒体 ID、状态、百分比；
            sec = 0, time = 0;//当前播放的媒体 播放秒数、文件时长；
    private SeekBar seekbar;
    private TextView tv_curTime, tv_totalTime;

    // Startup
    public SDLSurface(Context context) {

        super(context);
        this.context = context;
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        setOnClickListener(this);
        EventBus.getDefault().register(this);
//        nativeUtil = NativeUtil.getInstance();

        mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 12) {
            setOnGenericMotionListener(new SDLGenericMotionListener_API12());
        }

        // Some arbitrary defaults to avoid a potential division by zero
        mWidth = 1.0f;
        mHeight = 1.0f;
        Log.i("SDLSurface", "SDLSurface finish!");
    }

    public void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(Sensor.TYPE_ACCELEROMETER, true);
    }

    public Surface getNativeSurface() {
        return getHolder().getSurface();
    }

    // Called when we have a valid drawing surface
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("SDL", "surfaceCreated()");
        holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
//        nativeUtil.setCallListener(this);
        try {
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    // Called when we lose the surface
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("SDL", "surfaceDestroyed()");
        // Call this *before* setting mIsSurfaceReady to 'false'
        SDLActivity.handlePause();
        SDLActivity.mIsSurfaceReady = false;
        SDLActivity.onNativeSurfaceDestroyed();
        EventBus.getDefault().unregister(this);
        popupWindow = null;
    }

    // Called when the surface is resized
    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        Log.v("SDL", "surfaceChanged()");

        int sdlFormat = 0x15151002; // SDL_PIXELFORMAT_RGB565 by default
        switch (format) {
            case PixelFormat.A_8:
                Log.v("SDL", "pixel format A_8");
                break;
            case PixelFormat.LA_88:
                Log.v("SDL", "pixel format LA_88");
                break;
            case PixelFormat.L_8:
                Log.v("SDL", "pixel format L_8");
                break;
            case PixelFormat.RGBA_4444:
                Log.v("SDL", "pixel format RGBA_4444");
                sdlFormat = 0x15421002; // SDL_PIXELFORMAT_RGBA4444
                break;
            case PixelFormat.RGBA_5551:
                Log.v("SDL", "pixel format RGBA_5551");
                sdlFormat = 0x15441002; // SDL_PIXELFORMAT_RGBA5551
                break;
            case PixelFormat.RGBA_8888:
                Log.v("SDL", "pixel format RGBA_8888");
                sdlFormat = 0x16462004; // SDL_PIXELFORMAT_RGBA8888
                break;
            case PixelFormat.RGBX_8888:
                Log.v("SDL", "pixel format RGBX_8888");
                sdlFormat = 0x16261804; // SDL_PIXELFORMAT_RGBX8888
                break;
            case PixelFormat.RGB_332:
                Log.v("SDL", "pixel format RGB_332");
                sdlFormat = 0x14110801; // SDL_PIXELFORMAT_RGB332
                break;
            case PixelFormat.RGB_565:
                Log.v("SDL", "pixel format RGB_565");
                sdlFormat = 0x15151002; // SDL_PIXELFORMAT_RGB565
                break;
            case PixelFormat.RGB_888:
                Log.v("SDL", "pixel format RGB_888");
                // Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
                sdlFormat = 0x16161804; // SDL_PIXELFORMAT_RGB888
                break;
            default:
                Log.v("SDL", "pixel format unknown " + format);
                break;
        }

        mWidth = width;
        mHeight = height;
        SDLActivity.onNativeResize(width, height, sdlFormat, mDisplay.getRefreshRate());
        Log.v("SDL", "Window size: " + width + "x" + height);


        boolean skip = false;
        int requestedOrientation = SDLActivity.mSingleton.getRequestedOrientation();

        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            // Accept any
        } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if (mWidth > mHeight) {
                skip = true;
            }
        } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if (mWidth < mHeight) {
                skip = true;
            }
        }

        // Special Patch for Square Resolution: Black Berry Passport
        if (skip) {
            double min = Math.min(mWidth, mHeight);
            double max = Math.max(mWidth, mHeight);

            if (max / min < 1.20) {
                Log.v("SDL", "Don't skip on such aspect-ratio. Could be a square resolution.");
                skip = false;
            }
        }

        if (skip) {
            Log.v("SDL", "Skip .. Surface is not ready.");
            return;
        }


        // Set mIsSurfaceReady to 'true' *before* making a call to handleResume
        SDLActivity.mIsSurfaceReady = true;
        SDLActivity.onNativeSurfaceChanged();


        if (SDLActivity.mSDLThread == null) {
            // This is the entry point to the C app.
            // Start up the C app thread and enable sensor input for the first time

            final Thread sdlThread = new Thread(new SDLMain(), "SDLThread");
            enableSensor(Sensor.TYPE_ACCELEROMETER, true);
            Log.v("SDLActivity-->", "sdlThread start");
            sdlThread.start();

            // Set up a listener thread to catch when the native thread ends
           /* SDLActivity.mSDLThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        sdlThread.join();
                    }
                    catch(Exception e){}
                    finally{
                        // Native thread has finished
                        if (! SDLActivity.mExitCalledFromJava) {
//                            SDLActivity.handleNativeExit();
                        }
                    }
                }
            }, "SDLThreadListener");
            SDLActivity.mSDLThread.start();*/
        }

        if (SDLActivity.mHasFocus) {
            SDLActivity.handleResume();
        }
    }

    // unused
    @Override
    public void onDraw(Canvas canvas) {
    }

    /**
     * add by gowcage====================================================================
     */

    /**
     * 创建一个popupWindow
     *
     * @param parent
     * @return
     * @author gowcage
     */
    private PopupWindow createBottomPopup(View parent) {
        View content = LayoutInflater.from(context).inflate(R.layout.popup_ctrl, null);
        PopupWindow popup = new PopupWindow(content, (int) mWidth, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popup.setTouchable(true);
        // 此处为popwindow 设置背景，同时做到点击外部区域，popwindow消失
        // popup.setBackgroundDrawable(getResources().getDrawable(
        // R.drawable.popupwindow_bg));
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);// 设置焦点
        popup.setAnimationStyle(R.style.Anim_PopupWindow);
        popup.showAtLocation(parent, Gravity.BOTTOM, 0, 0); // 将window视图显示在myButton下面
        //initView
        seekbar = content.findViewById(R.id.SDL_playCtrl_seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                List<Integer> devIds = new ArrayList<Integer>();
                if (!isShare)
                    devIds.add(MeetingActivity.DevMeetInfo.getDeviceid());
                nativeUtil.setPlayPlace(0, seekBar.getProgress(), devIds);
            }
        });

        tv_curTime = content.findViewById(R.id.SDL_playCtrl_tv_curTime);
        tv_totalTime = content.findViewById(R.id.SDL_playCtrl_tv_totalTime);
        try {
            byte[] timedata = nativeUtil.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_TIME.getNumber(), mediaId);
            InterfaceBase.pbui_CommonInt32uProperty commonInt32uProperty = InterfaceBase.pbui_CommonInt32uProperty.parseFrom(timedata);
            tv_totalTime.setText("" + DateUtil.convertTime((long) commonInt32uProperty.getPropertyval()));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        Button btn_pause = content.findViewById(R.id.SDL_playCtrl_btn_pause),
                btn_stop = content.findViewById(R.id.SDL_playCtrl_btn_stop),
                btn_shareScreen = content.findViewById(R.id.SDL_playCtrl_btn_shareScreen),
                btn_stopShare = content.findViewById(R.id.SDL_playCtrl_btn_stopShare),
                btn_startProjection = content.findViewById(R.id.SDL_playCtrl_btn_startProjection),
                btn_stopProjection = content.findViewById(R.id.SDL_playCtrl_btn_stopProjection);

        btn_pause.setOnClickListener(onclick);
        btn_stop.setOnClickListener(onclick);
        btn_shareScreen.setOnClickListener(onclick);
        btn_stopShare.setOnClickListener(onclick);
        btn_startProjection.setOnClickListener(onclick);
        btn_stopProjection.setOnClickListener(onclick);

        return popup;
    }

    // Click events
    @Override
    public void onClick(View v) {
        Log.v("SDL-->", "onClick: ");
        popupWindow = createBottomPopup(v);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.PLAY_PROGRESS_NOTIFY:
                InterfacePlaymedia.pbui_Type_PlayPosCb playPos = (InterfacePlaymedia.pbui_Type_PlayPosCb) message.getObject();
                mediaId = playPos.getMediaId();
                status = playPos.getStatus();
                per = playPos.getPer();
                sec = playPos.getSec();
                if (seekbar != null)
                    seekbar.setProgress(per);
                if (tv_curTime != null)
                    tv_curTime.setText("" + DateUtil.convertTime(sec));
                break;
            case IDEventF.fab_member_pro:
                FabInfo fabInfo = (FabInfo) message.getObject();
                onlineProjectors = fabInfo.getOnLineProjectors();
                for (int i = 0; i < onlineProjectors.size(); i++) {
                    onlineProjectorIds.add(onlineProjectors.get(i).getDevId());
                }
                onlineClientIds = fabInfo.getOnlineClientIds();
                break;
        }
    }

    //在线投影机
    private List<DeviceInfo> onlineProjectors = new ArrayList();
    private ArrayList onlineProjectorIds = new ArrayList();
    private List<Integer> onlineClientIds = new ArrayList();

//    @Override
//    public void callListener(int action, Object result) {
//        switch (action) {
//            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
//                InterfaceDevice.pbui_Type_DeviceDetailInfo devInfos = (InterfaceDevice.pbui_Type_DeviceDetailInfo) result;
//                Log.e("MyLog", "MeetingActivity.handleMessage 184行:  查询设备信息 --->>> ");
//                if (devInfos != null) {
//                    InterfaceDevice.pbui_Type_DeviceDetailInfo o5 = devInfos;
//                    List<DeviceInfo> deviceInfos = Dispose.DevInfo(o5);
//                    onlineProjectors.clear();
//                    List allProjectors = new ArrayList<>();
//                    for (int i = 0; i < deviceInfos.size(); i++) {
//                        DeviceInfo deviceInfo = deviceInfos.get(i);
//                        int netState = deviceInfo.getNetState();
//                        int devId = deviceInfo.getDevId();
//                        //判断是否是投影机
//                        if ((devId & Macro.DEVICE_MEET_PROJECTIVE) == Macro.DEVICE_MEET_PROJECTIVE) {
//                            // 添加所有投影机
//                            allProjectors.add(deviceInfo);
//                            //判断是否是在线状态
//                            if (netState == 1) {
//                                //说明是在线状态的投影机
//                                onlineProjectors.add(deviceInfo);
//                                onlineProjectorIds.add(deviceInfo.getDevId());
//                            }
//                        } else if ((devId & Macro.DEVICE_MEET_CLIENT) == Macro.DEVICE_MEET_CLIENT) {//客户端
//                            if (netState == 1 && devId != MeetingActivity.getDevId()) {
//                                onlineClientIds.add(devId);//添加在线客户端
////                                for (int j = 0; j < onlineClientIds.size(); j++) {
////                                    Log.i("SDLSurface-->", "onlienClient: id=" + onlineClientIds.get(j));
////                                }
//                            }
//                        }
//                    }
//                }
//                break;
//        }
//    }

    OnClickListener onclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Integer> devIds = new ArrayList<Integer>();
            List<Integer> res = new ArrayList<Integer>();
            res.add(0);
            switch (v.getId()) {
                case R.id.SDL_playCtrl_btn_pause:
                    if (!isShare)
                        devIds.add(MeetingActivity.DevMeetInfo.getDeviceid());
                    if (isPause) {
                        Log.i("SDL-->", "resume");
                        nativeUtil.setPlayRecover(0, devIds);
                        isPause = false;
                    } else {
                        Log.i("SDL-->", "pause");
                        nativeUtil.setPlayStop(0, devIds);
                        isPause = true;
                    }
                    break;
                case R.id.SDL_playCtrl_btn_stop:
                    List<Integer> a = new ArrayList<Integer>();
                    List<Integer> b = new ArrayList<Integer>();
                    a.add(0);
                    if (!isShare)
                        b.add(NativeService.localDevId);
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(a, b);
                    /** ************ ******  释放播放资源  ****** ************ **/
//                    nativeUtil.mediaDestroy(0);
//                    SDLActivity.mSingleton.finish();
                    SDLActivity.mSingleton.releaseMediaRes();
                    break;
                case R.id.SDL_playCtrl_btn_shareScreen:
                    Log.v("SDL-->", "mediaId=" + mediaId + " per=" + per);
                    isShare = true;
//                    devIds.add(0x110000c);
                    nativeUtil.mediaPlayOperate(mediaId, devIds, per);
                    break;
                case R.id.SDL_playCtrl_btn_stopShare:
                    if (!isShare)
                        break;
                    isShare = false;
//                    devIds.add(0x110000c);
                    nativeUtil.stopResourceOperate(res, devIds);
                    break;
                case R.id.SDL_playCtrl_btn_startProjection:
                    isShare = true;
                    nativeUtil.mediaPlayOperate(mediaId, onlineProjectorIds, per);//开始投影
                    break;
                case R.id.SDL_playCtrl_btn_stopProjection:
                    if (!isShare)
                        break;
                    isShare = false;
                    nativeUtil.stopResourceOperate(res, onlineProjectorIds);
                    break;
            }
        }
    };

    /**
     * add by gowcage====================================================================
     */

    // Key events
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // Dispatch the different events depending on where they come from
        // Some SOURCE_DPAD or SOURCE_GAMEPAD are also SOURCE_KEYBOARD
        // So, we try to process them as DPAD or GAMEPAD events first, if that fails we try them as KEYBOARD

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) != 0 ||
                (event.getSource() & InputDevice.SOURCE_DPAD) != 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (SDLActivity.onNativePadDown(event.getDeviceId(), keyCode) == 0) {
                    return true;
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                if (SDLActivity.onNativePadUp(event.getDeviceId(), keyCode) == 0) {
                    return true;
                }
            }
        }

        if ((event.getSource() & InputDevice.SOURCE_KEYBOARD) != 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.v("SDL", "key down: " + keyCode);
                SDLActivity.onNativeKeyDown(keyCode);
                return false;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                Log.v("SDL", "key up: " + keyCode);
                SDLActivity.onNativeKeyUp(keyCode);
                return false;
            }
        }

        return false;
    }

    // Touch events
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /* Ref: http://developer.android.com/training/gestures/multi.html */
        final int touchDevId = event.getDeviceId();
        final int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();
        int pointerFingerId;
        int mouseButton;
        int i = -1;
        float x, y, p;

        // !!! FIXME: dump this SDK check after 2.0.4 ships and require API14.
        if (event.getSource() == InputDevice.SOURCE_MOUSE && SDLActivity.mSeparateMouseAndTouch) {
            if (Build.VERSION.SDK_INT < 14) {
                mouseButton = 1;    // For Android==12 all mouse buttons are the left button
            } else {
                try {
                    mouseButton = (Integer) event.getClass().getMethod("getButtonState").invoke(event);
                } catch (Exception e) {
                    mouseButton = 1;    // oh well.
                }
            }
            SDLActivity.onNativeMouse(mouseButton, action, event.getX(0), event.getY(0));
        } else {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    for (i = 0; i < pointerCount; i++) {
                        pointerFingerId = event.getPointerId(i);
                        x = event.getX(i) / mWidth;
                        y = event.getY(i) / mHeight;
                        p = event.getPressure(i);
                        if (p > 1.0f) {
                            // may be larger than 1.0f on some devices
                            // see the documentation of getPressure(i)
                            p = 1.0f;
                        }
                        SDLActivity.onNativeTouch(touchDevId, pointerFingerId, action, x, y, p);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    Log.v("SDL-->", "onTouch: action up");
                case MotionEvent.ACTION_DOWN:
                    // Primary pointer up/down, the index is always zero
                    i = 0;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_POINTER_DOWN:
                    // Non primary pointer up/down
                    if (i == -1) {
                        i = event.getActionIndex();
                    }

                    pointerFingerId = event.getPointerId(i);
                    x = event.getX(i) / mWidth;
                    y = event.getY(i) / mHeight;
                    p = event.getPressure(i);
                    if (p > 1.0f) {
                        // may be larger than 1.0f on some devices
                        // see the documentation of getPressure(i)
                        p = 1.0f;
                    }
                    SDLActivity.onNativeTouch(touchDevId, pointerFingerId, action, x, y, p);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    for (i = 0; i < pointerCount; i++) {
                        pointerFingerId = event.getPointerId(i);
                        x = event.getX(i) / mWidth;
                        y = event.getY(i) / mHeight;
                        p = event.getPressure(i);
                        if (p > 1.0f) {
                            // may be larger than 1.0f on some devices
                            // see the documentation of getPressure(i)
                            p = 1.0f;
                        }
                        SDLActivity.onNativeTouch(touchDevId, pointerFingerId, MotionEvent.ACTION_UP, x, y, p);
                    }
                    break;

                default:
                    break;
            }
        }

        return false;
    }

    // Sensor events

    public void enableSensor(int sensortype, boolean enabled) {
        // TODO: This uses getDefaultSensor - what if we have >1 accels?
        if (enabled) {
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(sensortype),
                    SensorManager.SENSOR_DELAY_GAME, null);
        } else {
            mSensorManager.unregisterListener(this,
                    mSensorManager.getDefaultSensor(sensortype));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x, y;
            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_90:
                    x = -event.values[1];
                    y = event.values[0];
                    break;
                case Surface.ROTATION_270:
                    x = event.values[1];
                    y = -event.values[0];
                    break;
                case Surface.ROTATION_180:
                    x = -event.values[1];
                    y = -event.values[0];
                    break;
                default:
                    x = event.values[0];
                    y = event.values[1];
                    break;
            }
            SDLActivity.onNativeAccel(-x / SensorManager.GRAVITY_EARTH,
                    y / SensorManager.GRAVITY_EARTH,
                    event.values[2] / SensorManager.GRAVITY_EARTH - 1);
        }
    }
}

/* This is a fake invisible editor view that receives the input and defines the
 * pan&scan region
 */
class DummyEdit extends View implements View.OnKeyListener {
    InputConnection ic;

    public DummyEdit(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setOnKeyListener(this);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        // This handles the hardware keyboard input
        if (event.isPrintingKey()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                ic.commitText(String.valueOf((char) event.getUnicodeChar()), 1);
            }
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            SDLActivity.onNativeKeyDown(keyCode);
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            SDLActivity.onNativeKeyUp(keyCode);
            return true;
        }

        return false;
    }

    //
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // As seen on StackOverflow: http://stackoverflow.com/questions/7634346/keyboard-hide-event
        // FIXME: Discussion at http://bugzilla.libsdl.org/show_bug.cgi?id=1639
        // FIXME: This is not a 100% effective solution to the problem of detecting if the keyboard is showing or not
        // FIXME: A more effective solution would be to change our Layout from AbsoluteLayout to Relative or Linear
        // FIXME: And determine the keyboard presence doing this: http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
        // FIXME: An even more effective way would be if Android provided this out of the box, but where would the fun be in that :)
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            if (SDLActivity.mTextEdit != null && SDLActivity.mTextEdit.getVisibility() == View.VISIBLE) {
                SDLActivity.onNativeKeyboardFocusLost();
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        ic = new SDLInputConnection(this, true);

        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
                | 33554432 /* API 11: EditorInfo.IME_FLAG_NO_FULLSCREEN */;

        return ic;
    }
}

class SDLInputConnection extends BaseInputConnection {

    public SDLInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);

    }

    @Override
    public boolean sendKeyEvent(KeyEvent event) {

        /*
         * This handles the keycodes from soft keyboard (and IME-translated
         * input from hardkeyboard)
         */
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.isPrintingKey()) {
                commitText(String.valueOf((char) event.getUnicodeChar()), 1);
            }
            SDLActivity.onNativeKeyDown(keyCode);
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_UP) {

            SDLActivity.onNativeKeyUp(keyCode);
            return true;
        }
        return super.sendKeyEvent(event);
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {

        nativeCommitText(text.toString(), newCursorPosition);

        return super.commitText(text, newCursorPosition);
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {

        nativeSetComposingText(text.toString(), newCursorPosition);

        return super.setComposingText(text, newCursorPosition);
    }

    public native void nativeCommitText(String text, int newCursorPosition);

    public native void nativeSetComposingText(String text, int newCursorPosition);

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        // Workaround to capture backspace key. Ref: http://stackoverflow.com/questions/14560344/android-backspace-in-webview-baseinputconnection
        if (beforeLength == 1 && afterLength == 0) {
            // backspace
            return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        }

        return super.deleteSurroundingText(beforeLength, afterLength);
    }
}

/* A null joystick handler for API level < 12 devices (the accelerometer is handled separately) */
class SDLJoystickHandler {

    /**
     * Handles given MotionEvent.
     *
     * @param event the event to be handled.
     * @return if given event was processed.
     */
    public boolean handleMotionEvent(MotionEvent event) {
        return false;
    }

    /**
     * Handles adding and removing of input devices.
     */
    public void pollInputDevices() {
    }
}

/* Actual joystick functionality available for API >= 12 devices */
class SDLJoystickHandler_API12 extends SDLJoystickHandler {

    static class SDLJoystick {
        public int device_id;
        public String name;
        public ArrayList<InputDevice.MotionRange> axes;
        public ArrayList<InputDevice.MotionRange> hats;
    }

    static class RangeComparator implements Comparator<InputDevice.MotionRange> {
        @Override
        public int compare(InputDevice.MotionRange arg0, InputDevice.MotionRange arg1) {
            return arg0.getAxis() - arg1.getAxis();
        }
    }

    private ArrayList<SDLJoystick> mJoysticks;

    public SDLJoystickHandler_API12() {

        mJoysticks = new ArrayList<SDLJoystick>();
    }

    @Override
    public void pollInputDevices() {
        Log.e("MyLog", "SDLJoystickHandler_API12.pollInputDevices 1627行:   --->>> ");
        int[] deviceIds = InputDevice.getDeviceIds();
        // It helps processing the device ids in reverse order
        // For example, in the case of the XBox 360 wireless dongle,
        // so the first controller seen by SDL matches what the receiver
        // considers to be the first controller

        for (int i = deviceIds.length - 1; i > -1; i--) {
            SDLJoystick joystick = getJoystick(deviceIds[i]);
            if (joystick == null) {
                joystick = new SDLJoystick();
                InputDevice joystickDevice = InputDevice.getDevice(deviceIds[i]);

                if (
                        (joystickDevice.getSources() & InputDevice.SOURCE_CLASS_JOYSTICK) != 0
                                ||
                                (joystickDevice.getSources() & InputDevice.SOURCE_CLASS_BUTTON) != 0
                        ) {
                    joystick.device_id = deviceIds[i];
                    joystick.name = joystickDevice.getName();
                    joystick.axes = new ArrayList<InputDevice.MotionRange>();
                    joystick.hats = new ArrayList<InputDevice.MotionRange>();

                    List<InputDevice.MotionRange> ranges = joystickDevice.getMotionRanges();
                    Collections.sort(ranges, new RangeComparator());
                    for (InputDevice.MotionRange range : ranges) {
                        if ((range.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK) != 0) {
                            if (range.getAxis() == MotionEvent.AXIS_HAT_X ||
                                    range.getAxis() == MotionEvent.AXIS_HAT_Y) {
                                joystick.hats.add(range);
                            } else {
                                joystick.axes.add(range);
                            }
                        }
                    }

                    mJoysticks.add(joystick);
                    SDLActivity.nativeAddJoystick(joystick.device_id, joystick.name, 0, -1,
                            joystick.axes.size(), joystick.hats.size() / 2, 0);
                }
            }
        }

        /* Check removed devices */
        ArrayList<Integer> removedDevices = new ArrayList<Integer>();
        for (int i = 0; i < mJoysticks.size(); i++) {
            int device_id = mJoysticks.get(i).device_id;
            int j;
            for (j = 0; j < deviceIds.length; j++) {
                if (device_id == deviceIds[j]) break;
            }
            if (j == deviceIds.length) {
                removedDevices.add(Integer.valueOf(device_id));
            }
        }

        for (int i = 0; i < removedDevices.size(); i++) {
            int device_id = removedDevices.get(i).intValue();
            SDLActivity.nativeRemoveJoystick(device_id);
            for (int j = 0; j < mJoysticks.size(); j++) {
                if (mJoysticks.get(j).device_id == device_id) {
                    mJoysticks.remove(j);
                    break;
                }
            }
        }
    }

    protected SDLJoystick getJoystick(int device_id) {
        for (int i = 0; i < mJoysticks.size(); i++) {
            if (mJoysticks.get(i).device_id == device_id) {
                return mJoysticks.get(i);
            }
        }
        return null;
    }

    @Override
    public boolean handleMotionEvent(MotionEvent event) {
        Log.e("MyLog", "SDLJoystickHandler_API12.handleMotionEvent 1705行:   --->>> ");
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) != 0) {
            int actionPointerIndex = event.getActionIndex();
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    SDLJoystick joystick = getJoystick(event.getDeviceId());
                    if (joystick != null) {
                        for (int i = 0; i < joystick.axes.size(); i++) {
                            InputDevice.MotionRange range = joystick.axes.get(i);
                            /* Normalize the value to -1...1 */
                            float value = (event.getAxisValue(range.getAxis(), actionPointerIndex) - range.getMin()) / range.getRange() * 2.0f - 1.0f;
                            SDLActivity.onNativeJoy(joystick.device_id, i, value);
                        }
                        for (int i = 0; i < joystick.hats.size(); i += 2) {
                            int hatX = Math.round(event.getAxisValue(joystick.hats.get(i).getAxis(), actionPointerIndex));
                            int hatY = Math.round(event.getAxisValue(joystick.hats.get(i + 1).getAxis(), actionPointerIndex));
                            SDLActivity.onNativeHat(joystick.device_id, i / 2, hatX, hatY);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }
}

class SDLGenericMotionListener_API12 implements View.OnGenericMotionListener {
    // Generic Motion (mouse hover, joystick...) events go here
    @Override
    public boolean onGenericMotion(View v, MotionEvent event) {
        float x, y;
        int mouseButton;
        int action;
        switch (event.getSource()) {
            case InputDevice.SOURCE_JOYSTICK:
            case InputDevice.SOURCE_GAMEPAD:
            case InputDevice.SOURCE_DPAD:
                SDLActivity.handleJoystickMotionEvent(event);
                return true;

            case InputDevice.SOURCE_MOUSE:
                action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_SCROLL:
                        x = event.getAxisValue(MotionEvent.AXIS_HSCROLL, 0);
                        y = event.getAxisValue(MotionEvent.AXIS_VSCROLL, 0);
                        SDLActivity.onNativeMouse(0, action, x, y);
                        return true;

                    case MotionEvent.ACTION_HOVER_MOVE:
                        x = event.getX(0);
                        y = event.getY(0);

                        SDLActivity.onNativeMouse(0, action, x, y);
                        return true;

                    default:
                        break;
                }

            default:
                break;
        }
        Log.e("MyLog", "SDLGenericMotionListener_API12.onGenericMotion 1771行:   --->>> ");
        // Event was not managed
        return false;
    }
}
