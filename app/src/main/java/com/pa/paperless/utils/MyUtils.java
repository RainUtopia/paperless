package com.pa.paperless.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.pa.paperless.R;
import com.pa.paperless.constant.Macro;
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
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 获取手机的唯一标识符
     * AndroidId 和 Serial Number结合使用
     *
     * @param context
     * @return
     */
    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }


    private static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

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


    public static void downLoadFile(String dir, String filename, View coordinatorLayout, final Context context, int posion, NativeUtil nativeUtil, long filesize) {
        if (SDCardUtils.isSDCardEnable()) {
            CreateFile(dir);
            dir += filename;
            final File file = new File(dir);
            Log.e("MyLog", "MyUtils.downLoadFile 205行:  文件的大小 --->>> " + filesize + "  文件目录：" + dir);
            if (file.exists()) {//文件已存在
                if (file.length() == filesize) {//该文件的大小与服务器中一致，已经是最新的
                    Snackbar.make(coordinatorLayout, "  文件已经存在是否直接打开？  ", Snackbar.LENGTH_LONG)
                            .setAction("打开查看", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FileUtil.openFile(context, file);
                                }
                            }).show();
                } else {//没下载完成，或者是旧的文件
                    //目前采用，删除未完成的再重新下载
                    file.delete();
                    nativeUtil.creationFileDownload(dir, posion, 0, 0);
                }
            } else {//文件不存在
                Log.e("MyLog", "MyUtils.downLoadFile:  下载操作： 文件的绝对路径： --->>> " + dir);
                nativeUtil.creationFileDownload(dir, posion, 0, 0);
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
    public static void openFile(String dir, String filename, View coordinatorLayout, final NativeUtil nativeUtil, final int posion, Context context, long filesize) {
        if (SDCardUtils.isSDCardEnable()) {
            //先创建好目录
            CreateFile(dir);
            dir += filename;
            File file1 = new File(dir);
            if (!file1.exists() || file1.length() != filesize) {
                final String finalFile = dir;
                Snackbar.make(coordinatorLayout, " 文件不存在，是否立即下载？ ", Snackbar.LENGTH_LONG)
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

    public static void CreateFile(String dir) {
        File file2 = new File(Macro.MEETFILE);
        File file3 = new File(dir);
        if (!file2.exists()) {
            boolean mkdir = file2.mkdir();
            if(mkdir) {
                file3.mkdir();
            }
        }else {
            file3.mkdir();
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
     * 字符串转二进制
     *
     * @param str 要转换的字符串
     * @return 转换后的二进制数组
     */
    public static byte[] hex2byte(String str) { // 字符串转二进制
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;

        }
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 方法一：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexFun1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for (byte b : bytes) { // 使用除与取余进行转换
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }

    /**
     * 二进制转字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex2(byte[] b) // 二进制转字符串
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }

    /**
     * 将图片转换成十六进制字符串
     *
     * @return
     */
    public static String sendPhoto(Bitmap bitmap) {
//        Drawable d = photo.getDrawable();
//        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);// (0 - 100)压缩文件
        byte[] bt = stream.toByteArray();
        String photoStr = byte2hex(bt);
        return photoStr;
    }

    /**
     * 二进制转字符串
     *
     * @return
     */
    public static String byte2hex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }

    /**
     * byte数组转换成十六进制字符串
     *
     * @param bArray
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
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

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     * 调用时判断除不为0
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = b.length - 4; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * 设置添加屏幕的背景透明度
     * 屏幕透明度0.0-1.0 1表示完全不透明
     *
     * @param context
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(Context context, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
    }
}
