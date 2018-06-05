package com.pa.paperless.utils;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.pa.paperless.R;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.utils.MyUtils.setBackgroundAlpha;

/**
 * Created by Administrator on 2017/11/24.
 */

public class FileUtil {
    public static final String TAG = "FileUtil-->";

    /**
     * 将BitMap转为byte数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] Bitmap2bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byte数组转bitmap
     *
     * @param bytes
     * @return
     */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    /**
     * 将BitMap保存到指定目录下
     *
     * @param bitmap
     * @param file
     */
    public static boolean saveBitmap(Bitmap bitmap, File file) {
        boolean b = false;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            b = true;
        } catch (FileNotFoundException e) {
            b = false;
            e.printStackTrace();
        } catch (IOException e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 判断是否为视频文件
     *
     * @param fileName
     * @return
     */
    public static boolean isVideoFile(String fileName) {
        //获取文件的扩展名  mp3/mp4...
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        if (fileEnd.equals("mp4")
                || fileEnd.equals("3gp")
                || fileEnd.equals("mp3")
                || fileEnd.equals("wmv")
                || fileEnd.equals("ts")
                || fileEnd.equals("rmvb")
                || fileEnd.equals("mov")
                || fileEnd.equals("m4v")
                || fileEnd.equals("avi")
                || fileEnd.equals("m3u8")
                || fileEnd.equals("3gpp")
                || fileEnd.equals("3gpp2")
                || fileEnd.equals("mkv")
                || fileEnd.equals("flv")
                || fileEnd.equals("divx")
                || fileEnd.equals("f4v")
                || fileEnd.equals("rm")
                || fileEnd.equals("asf")
                || fileEnd.equals("ram")
                || fileEnd.equals("mpg")
                || fileEnd.equals("v8")
                || fileEnd.equals("swf")
                || fileEnd.equals("m2v")
                || fileEnd.equals("asx")
                || fileEnd.equals("ra")
                || fileEnd.equals("naivx")
                || fileEnd.equals("xvid")
                ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为图片文件
     *
     * @param fileName
     * @return
     */
    public static boolean isPictureFile(String fileName) {
        //获取文件的扩展名
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        if (fileEnd.equals("jpg")
                || fileEnd.equals("png")
                || fileEnd.equals("img")
                || fileEnd.equals("bmp")
                || fileEnd.equals("jpeg")
                ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为文档类文件
     *
     * @param fileName
     * @return
     */
    public static boolean isDocumentFile(String fileName) {
        //获取文件的扩展名 -->获得的是小写：toLowerCase()  /大写:toUpperCase()
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        if (fileEnd.equals("txt")
                || fileEnd.equals("doc")
                || fileEnd.equals("docx")
                || fileEnd.equals("xls")
                || fileEnd.equals("xlsx")
                || fileEnd.equals("ppt")
                || fileEnd.equals("wps")
                || fileEnd.equals("pdf")
                ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为除文档、视频、图片外的其它类文件
     *
     * @param fileName
     * @return
     */
    public static boolean isOtherFile(String fileName) {
        //获取文件的扩展名 -->获得的是小写：toLowerCase()  /大写:toUpperCase()
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        //除去文档/视频/图片其它的后缀名文件
        if (!isDocumentFile(fileName) && !isVideoFile(fileName) && !isPictureFile(fileName)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 遍历获取某目录下指定类型的所有文件
     *
     * @param filePath
     * @param type
     * @return
     */
    public List<String> getFileDir(String filePath, String type) {
        List<String> PathList = new ArrayList<String>();
        try {
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出目录下所有的文件
            // 将所有的文件存入ArrayList中,并过滤所有type格式的文件路径
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath(), type)) {
                    PathList.add(file.getPath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 返回得到的路径列表
        return PathList;
    }

    // 检查扩展名，得到图片格式的文件
    public static boolean checkIsImageFile(String fName, String type) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals(type)) {
            isImageFile = true;
            Log.e("MyLog", "FileUtil.checkIsImageFile:  图片文件名 --->>> " + fName);
        } else {
            isImageFile = false;
        }

        return isImageFile;

    }


    /**
     * 向文件中写入数据
     *
     * @param filePath 目标文件全路径
     * @param data     要写入的数据
     * @return true表示写入成功  false表示写入失败
     */
    public static boolean writeBytes(String filePath, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * 从文件中读取数据
     *
     * @param file
     * @return
     */
    public static byte[] readBytes(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 查找某目录下的某个文件
     *
     * @param dir      父目录
     * @param filename 文件名
     * @return
     */
    public static File findFilePathByName(String dir, final String filename) {
        File baseDir = new File(dir);
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在
            Log.e(TAG, "FileUtil.findFilePathByName :   --> " + "文件查找失败：" + dir + "不是一个目录！");
        }
        File[] files = baseDir.listFiles();
        List<File> fs = new ArrayList<>();
        List<File> file = findFile(files, filename, fs);
        if (file.size()>0) {
            Log.e(TAG, "FileUtil.findFilePathByName :  查找到文件： --> " + file.get(0).getName());
            return file.get(0);
        } else {
            Log.e(TAG, "FileUtil.findFilePathByName :  没有查找到文件 --> ");
            return null;
        }
    }

    public static List<File> findFile(File[] files, String filename,List<File> fs) {
        for (int i = 0; i < files.length; i++) {
            File f1 = files[i];
            Log.e(TAG, "FileUtil.findFile :  当前文件名 --> " + f1.getName());
            if (f1.isFile()) {
                Log.e(TAG, "FileUtil.findFile :  当前为文件 --> " + filename + "   ... " + f1.getName());
                if (filename.equals(f1.getName())) {
                    Log.e(TAG, "FileUtil.findFile :  添加文件： --> ");
                    fs.add(f1);
                }
            } else if (f1.isDirectory()) {
                Log.e(TAG, "FileUtil.findFile :  当前为目录 --> ");
                File[] files1 = f1.listFiles();
                if (files1.length > 0) {//如果目录下还有文件，递归查找
                    findFile(files1, filename,fs);
                }
            }
        }
        return fs;
    }

    /**
     * 删除目录下的所有文件，不包含文件夹
     *
     * @param dirfile 目录文件
     * @return
     */
    public static boolean deleteAllFile(File dirfile) {
        if (dirfile.exists()) {
            File[] files = dirfile.listFiles();
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    file.delete();
                    if (file.isDirectory()) {
                        deleteAllFile(file);
                    }
                }
            } else {
                dirfile.delete();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开图片
     *
     * @param filepath 文件路径
     * @param context  上下文
     * @param view     展示位置的 view
     */
    public static void openPicture(String filepath, final Context context, View view) {
        byte[] bytes = FileUtil.readBytes(filepath);
        View inflate = LayoutInflater.from(context).inflate(R.layout.open_piccc, null);
        ImageView imgv = inflate.findViewById(R.id.imgv);
        PopupWindow picPop = new PopupWindow(inflate, PercentLinearLayout.LayoutParams.MATCH_PARENT, PercentLinearLayout.LayoutParams.MATCH_PARENT, true);
        Glide.with(context).load(bytes).into(imgv);
        picPop.setAnimationStyle(R.style.AnimHorizontal);
        picPop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        picPop.setTouchable(true);
        setBackgroundAlpha(context, 0.3f);
        picPop.setOutsideTouchable(true);
        picPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(context, 1.0f);
            }
        });
        picPop.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        // 获取文件的后缀名
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;

    }

    //建立一个MIME类型与文件后缀名的匹配表
    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
//            {".doc", "application/msword"},
//            {".docx", "application/msword"},
            {".doc", "text/plain"},
            {".docx", "text/plain"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xls", "application/vnd.ms-works"},
//            {".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

}