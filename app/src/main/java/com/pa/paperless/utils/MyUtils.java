package com.pa.paperless.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.pa.paperless.R;
import com.pa.paperless.activity.PeletteActivity;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.constant.WpsModel;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;

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

import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;
import static com.pa.paperless.utils.FileUtil.getMIMEType;

/**
 * Created by Administrator on 2017/11/17.
 */

public class MyUtils {
    public static final String TAG = "MyUtils-->";

    public static File mFile;
    public static int getMediaid(String path) {
        //其它
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_OTHER | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_RECORD | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_UPDATE | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_TEMP | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        //
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MAINTYPEBITMASK | Macro.SUBTYPEBITMASK;
        }
        //音频
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_PCM;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_MP3;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_ADPCM;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_FLAC;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_MP4;
        }
        //视屏
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_MKV;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_RMVB;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_AVI;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_RM;
        }
        //图片
        if (FileUtil.isPictureFile(path)) {
            return Macro.MEDIA_FILETYPE_PICTURE | Macro.MEDIA_FILETYPE_BMP;
        }
        if (FileUtil.isPictureFile(path)) {
            return Macro.MEDIA_FILETYPE_PICTURE | Macro.MEDIA_FILETYPE_JPEG;
        }
        if (FileUtil.isPictureFile(path)) {
            return Macro.MEDIA_FILETYPE_PICTURE | Macro.MEDIA_FILETYPE_PNG;
        }
        return 0;
    }

    public static File getmFile(){
        return mFile;
    }

    public static void setFile(File f){
        mFile = f;
    }

    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 收到会议消息 发送EventBus通知
     *
     * @param result
     */
    public static void receiveMessage(InterfaceIM.pbui_Type_MeetIM result, NativeUtil nativeUtil) {
        InterfaceIM.pbui_Type_MeetIM receiveMsg = result;
        //获取之前的未读消息个数
        int badgeNumber1 = mBadge.getBadgeNumber();
        Log.e("MyLog", "MyUtils.receiveMessage :  原来的个数 --->>> " + badgeNumber1);
        int all = badgeNumber1 + 1;
        if (receiveMsg != null) {
            List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
            receiveMeetIMInfos.get(0).setType(true);
            mReceiveMsg.add(receiveMeetIMInfos.get(0));
            try {
                //查询指定ID的参会人  获取名称
                nativeUtil.queryAttendPeopleFromId(receiveMeetIMInfos.get(0).getMemberid());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            Log.e("MyLog", "MyUtils.receiveMessage : 收到的信息个数：  --->>> " + mReceiveMsg.size());
        }
        List<EventBadge> num = new ArrayList<>();
        num.add(new EventBadge(all));
        // TODO: 2018/3/7 通知界面更新
        Log.e("MyLog", "MyUtils.receiveMessage :  传递过去的个数 --->>> " + all);
        EventBus.getDefault().post(new EventMessage(IDEventMessage.UpDate_BadgeNumber, num));
    }

    /**
     * case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人
     * MyUtils.queryName((InterfaceMember.pbui_Type_MemberDetailInfo) result);
     * break;
     */
    public static void queryName(InterfaceMember.pbui_Type_MemberDetailInfo result) {
        InterfaceMember.pbui_Type_MemberDetailInfo o2 = result;
        List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = o2.getItemList();
        if (itemList != null) {
            String name = MyUtils.getBts(itemList.get(0).getName());
            mReceiveMsg.get(mReceiveMsg.size() - 1).setName(name);
            Log.e("MyLog", "ChatFragment.handleMessage:  指定参会人名称： --->>> " + name);
        }
    }

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

    public static void showMsg(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    public static void downLoadFile(String dir, String filename, final Context context, int posion, NativeUtil nativeUtil, long filesize) {
        if (SDCardUtils.isSDCardEnable()) {
            CreateFile(dir);
            dir += filename;
            final File file = new File(dir);
            Log.e("MyLog", "MyUtils.downLoadFile 205行:  文件的大小 --->>> " + filesize + "  文件目录：" + dir);
            if (file.exists()) {//文件已存在
                if (file.length() == filesize) {//该文件的大小与服务器中一致，已经是最新的
//                    Snackbar.make(coordinatorLayout, "  文件已经存在是否直接打开？  ", Snackbar.LENGTH_LONG)
//                            .setAction("打开查看", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    FileUtil.openFile(context, file);
//                                }
//                            }).show();
//                    FileUtil.openFile(context, file);
                    OpenThisFile(context,file);
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

    public static void openFile(String dir, String filename, final NativeUtil nativeUtil, final int posion, Context context, long filesize) {
        if (SDCardUtils.isSDCardEnable()) {
            //先创建好目录
            CreateFile(dir);
            dir += filename;
            File file1 = new File(dir);
            if (!file1.exists() || file1.length() != filesize) {
                final String finalFile = dir;
//                Snackbar.make(coordinatorLayout, " 文件不存在，是否立即下载？ ", Snackbar.LENGTH_LONG)
//                        .setAction("立即下载", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                nativeUtil.creationFileDownload(finalFile, posion, 0, 0);
//                            }
//                        }).show();
                nativeUtil.creationFileDownload(finalFile, posion, 0, 0);
            } else {
                OpenThisFile(context, file1);
            }
        }
    }


    public static void OpenThisFile(Context context, File file1) {
        setFile(file1);
        String filename = file1.getName();
        Log.e(TAG, "MyUtils.OpenThisFile :   --> "+filename);
        if (FileUtil.isPictureFile(filename)) {
            /** **** **  如果是图片文件则使用自己的打开  ** **** **/
            Log.e(TAG, "MyUtils.openFile :  图片文件 --> ");
            EventBus.getDefault().post(new EventMessage(IDEventF.open_picture,file1.getAbsolutePath()));
        }else if(FileUtil.isDocumentFile(filename)){
            /** **** **  如果是文档类文件，设置只能使用WPS软件打开  ** **** **/
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.NORMAL); // 打开模式
            bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 关闭时是否发送广播
            bundle.putBoolean(WpsModel.SEND_SAVE_BROAD, true); // 保存时是否发送广播
            bundle.putBoolean(WpsModel.HOMEKEY_DOWN, true); // 单机home键是否发送广播
            bundle.putBoolean(WpsModel.BACKKEY_DOWN, true); // 单机back键是否发送广播
            bundle.putBoolean(WpsModel.ENTER_REVISE_MODE, true); // 以修订模式打开文档
            bundle.putBoolean(WpsModel.SAVE_PATH, true); // 文件保存路径
//            bundle.putString(WpsModel.THIRD_PACKAGE, getPackageName()); // 第三方应用的包名，用于对改应用合法性的验证
            bundle.putString(WpsModel.THIRD_PACKAGE, WpsModel.PackageName.NORMAL); // 第三方应用的包名，用于对改应用合法性的验证
            bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
            // bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);

            Uri uri = Uri.fromFile(file1);
            intent.setData(uri);
            intent.putExtras(bundle);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                System.out.println("打开wps异常："+e.toString());
                e.printStackTrace();
            }
        }
        else {
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


    /**
     * 在MEETFILE目录下创建文件
     *
     * @param dir
     */
    public static String CreateFile(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 截图预览
     *
     * @param bitmap
     */
    public static void previewPic(Context context, Bitmap bitmap, View parent) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = 0.4f;
        final View inflate = LayoutInflater.from(context).inflate(R.layout.preview_pop, null);
        final PopupWindow preViewPop = new PopupWindow(inflate,
                PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        preViewPop.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        preViewPop.setTouchable(true);
        preViewPop.setFocusable(true);
        preViewPop.setOutsideTouchable(true);
        ImageView previewIV = inflate.findViewById(R.id.preview_iv);
        //需要先将BitMap转为字节
        final byte[] bytes = FileUtil.Bitmap2bytes(bitmap);
        Glide.with(context)
                .load(bytes)
                .override((int) (width * scale), (int) (height * scale))
                .into(previewIV);
        final ObjectAnimator animator = ObjectAnimator.ofFloat(previewIV, "alpha", 1.0f, 0.0f);
        animator.setDuration(5000);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                preViewPop.dismiss();
            }
        });
        previewIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MyLog", "MyUtils.onClick 352行:  点击了预览图片 --->>> ");
//                Intent intent = new Intent(MeetingActivity.this, PostilActivity.class);
//                intent.putExtra("bitmap", bytes);
//                startActivity(intent);
            }
        });
        preViewPop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
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
