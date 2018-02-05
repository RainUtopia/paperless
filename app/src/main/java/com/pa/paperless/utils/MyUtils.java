package com.pa.paperless.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.google.protobuf.ByteString;
import com.pa.paperless.R;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.pa.paperless.utils.FileUtil.getMIMEType;

/**
 * Created by Administrator on 2017/11/17.
 */

public class MyUtils {


    public static void ScreenShot(View v) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        String name = "/sdcard/" + sdf.format(new Date()) + ".png";
        View view = v.getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        if (null != drawingCache) {
            try {
                FileOutputStream fos = new FileOutputStream(name);
                drawingCache.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Log.e("MyLog", "MyUtils.ScreenShot:  文件名： --->>> " + name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("MyLog", "MyUtils.ScreenShot:  drawingCache 为空 --->>> ");
        }
    }

    /**
     * 在线播放视屏
     *
     * @param nativeUtil
     * @param context
     * @param devID
     * @param activity
     * @return
     */
    public static View playMedia(final NativeUtil nativeUtil, Context context, final int devID, Activity activity) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        View inflate = LayoutInflater.from(context).inflate(R.layout.media_pop, null);
        final PopupWindow mediaPop = new PopupWindow(inflate, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        mediaPop.setAnimationStyle(R.style.AnimHorizontal);
        mediaPop.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        mediaPop.setTouchable(true);
        mediaPop.setOutsideTouchable(true);
        SurfaceView sv = inflate.findViewById(R.id.sv);
        Button stop_play = inflate.findViewById(R.id.stop_play);
        stop_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //248.停止资源操作
                nativeUtil.stopResourceOperate(0, devID);
                mediaPop.dismiss();
            }
        });
        mediaPop.showAtLocation(activity.findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
        return inflate;
    }

    public static void downLoadFile(String filename, View coordinatorLayout, final Context context, int posion, NativeUtil nativeUtil) {
        if (SDCardUtils.isSDCardEnable()) {
            String sdCardPath = SDCardUtils.getSDCardPath();
            sdCardPath += filename;
            final File file = new File(sdCardPath);
            if (file.exists()) {
                Snackbar.make(coordinatorLayout, "  文件已经存在是否直接打开？  ", Snackbar.LENGTH_LONG)
                        .setAction("打开查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                FileUtil.openFile(context, file);
                            }
                        }).show();
            } else {
                Log.e("MyLog", "MyUtils.downLoadFile:  下载操作： 文件的绝对路径： --->>> " + sdCardPath);
                nativeUtil.creationFileDownload(sdCardPath, posion, 0, 0);
            }
        }
    }

    /**
     * 打开文件
     *
     * @param filename
     * @param coordinatorLayout
     * @param nativeUtil
     * @param posion
     * @param context
     */
    public static void openFile(String filename, View coordinatorLayout, final NativeUtil nativeUtil, final int posion, Context context) {
        if (SDCardUtils.isSDCardEnable()) {
            String file = SDCardUtils.getSDCardPath();
            file += filename;
            File file1 = new File(file);
            if (!file1.exists()) {
                final String finalFile = file;
                Snackbar.make(coordinatorLayout, " 文件不存在，是否先下载？ ", Snackbar.LENGTH_LONG)
                        .setAction("立即下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nativeUtil.creationFileDownload(finalFile, posion, 0, 0);
                            }
                        }).show();
            } else {
                //已经存在才打开文件
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //设置intent的Action属性
                intent.setAction(Intent.ACTION_VIEW);
                //获取文件file的MIME类型
                //Collection mimeTypes = getMimeTypes(file);
                //String type = mimeTypes.toString();
                String type = getMIMEType(file1);
                //设置intent的data和Type属性。
                intent.setDataAndType(/*uri*/Uri.fromFile(file1), type);
                //跳转
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 设置popupWindow 的动画
     *
     * @param popupWindow
     */
    public static void setAnimal(PopupWindow popupWindow) {
        popupWindow.setAnimationStyle(R.style.AnimHorizontal);
    }

    /**
     * String 转 ByteString
     *
     * @param name
     * @return
     */
    public static ByteString getStb(String name) {
        return ByteString.copyFrom(name, Charset.forName("UTF-8"));
    }

    /**
     * ByteString 转 String
     *
     * @param string
     * @return
     */
    public static String getBts(ByteString string) {
        return new String(string.toByteArray());
    }

    /**
     * 更改按钮选择颜色
     *
     * @param index
     * @param btns
     */
    public static void setViewBackground(int index, List<Button> btns) {
        for (int i = 0; i < btns.size(); i++) {
            if (i == index) {
                btns.get(i).setBackgroundColor(Color.WHITE);
            } else {
                btns.get(i).setBackgroundColor(Color.rgb(216, 216, 216));
            }
        }
    }

    /**
     * 对象转byte
     *
     * @param obj
     * @return
     */
    public static byte[] ObjectToByte(Object obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();
            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * byte转对象
     *
     * @param bytes
     * @return
     */
    private Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 设置控件点击时放大动画
     *
     * @param iv
     */
    public static void setAnimator(View iv) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 1.5f, 1f);
        animator.setDuration(300);
        animator.start();
    }
}
