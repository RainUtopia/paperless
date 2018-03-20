package com.pa.paperless.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.pa.paperless.R;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.pa.paperless.utils.FileUtil.getMIMEType;

/**
 * Created by Administrator on 2017/11/17.
 */

public class MyUtils {


    /**
     * 截屏 不能截取状态栏  有bug：界面不同 截出来的图片都一样
     *
     * @param v 截取传入的控件父控件
     */
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
                showMsg(v.getContext(), "截图成功");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("MyLog", "MyUtils.ScreenShot:  drawingCache 为空 --->>> ");
        }
    }

    /**
     * 吐丝
     *
     * @param c
     * @param msg
     */
    public static void showMsg(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
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
    public static void setPopAnimal(PopupWindow popupWindow) {
        popupWindow.setAnimationStyle(R.style.Anim_PopupWindow);
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
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(iv, "scaleY", 1f, 1.5f, 1f);
        animatorX.setDuration(300);
        animatorY.setDuration(300);
        animatorX.start();
        animatorY.start();
    }

    /**
     * 将十进制的数装换成二进制的字符串
     *
     * @param value
     * @return
     */
    public static String get10To2(int value) {
        return Integer.toBinaryString(value);
    }


    /**
     * 查找sd卡中以 postfix 为后缀的文件
     *
     * @param postfix 文件的后缀  .txt .xls
     * @return 得到一个存放文件路径的集合
     */
    public static List<File> getSDPostfixFile(String postfix) {
        //1.获取到sd卡目录下所有的txt文件
        List<File> txtFile = new ArrayList<>();
        File file = new File(SDCardUtils.getSDCardPath());
        TxtFileFilter txtFileFilter = new TxtFileFilter();
        txtFileFilter.addType(postfix);
        File[] files = file.listFiles(txtFileFilter);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                txtFile.add(files[i]);
            }
            return txtFile;
        }
        return new ArrayList<>();
    }

    /**
     * 以弹出框的形式展示查找到的txt文档文件
     *
     * @param txtFile 存放文件路径的集合 /storage/emulated/0/游戏文本.txt
     * @param edt     获取TXT文本内容后展示到 edt中
     */
    public static void showTxtDialog(Context context, List<File> txtFile, final EditText edt) {
        //存放txt文件的路径  txtFile.size()用来定义数组的大小
        final String[] txtFilePath = new String[txtFile.size()];
        //txt文件的名称
        final String[] txtFileName = new String[txtFile.size()];
        for (int i = 0; i < txtFile.size(); i++) {
            txtFilePath[i] = txtFile.get(i).toString();//  /storage/emulated/0/文本.txt
            txtFileName[i] = txtFile.get(i).getName();//   文本.txt
        }
        if (txtFilePath.length == 0) {
            Toast.makeText(context, "在SD卡中没有找到该类型文件", Toast.LENGTH_SHORT).show();
        } else {
            //只有SD卡中有该类文件的时候才展示，否则会报错
            new AlertDialog.Builder(context).setTitle("SD卡中所有该类型文件")
                    .setItems(txtFileName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //  读取内容获得文本
                            String s = MyUtils.ReadTxtFile(txtFilePath[i]);
                            edt.setText(s);
                        }
                    }).create().show();
        }
    }

    /**
     * 读取TXT格式文件
     *
     * @param strFilePath
     * @return 返回该文件的文本内容
     */
    public static String ReadTxtFile(String strFilePath) {
        String path = String.valueOf(strFilePath);
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    public static void handTo(int action, Object object, String key, Handler handler) {
        if (object != null) {
            Bundle bundle = new Bundle();
            ArrayList arrayList = new ArrayList();
            arrayList.add(object);
            bundle.putParcelableArrayList(key, arrayList);
            Message message = new Message();
            message.what = action;
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    //将一个Double的数 转换成百分比字符串显示
    public static String getPercentage(double d) {
        java.text.NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(2);//最大小数位数
        percentInstance.setMaximumIntegerDigits(2);//最大整数位数
        percentInstance.setMinimumFractionDigits(1);//最小小数位数
        percentInstance.setMinimumIntegerDigits(1);//最小整数位数
        String format = percentInstance.format(d);
        return format;
    }
    //将一个百分比的字符串转换成一个数
    public static float getPerNumber(String perStr) {
        return new Float(perStr.substring(0, perStr.indexOf("%"))) / 100;
    }

}
