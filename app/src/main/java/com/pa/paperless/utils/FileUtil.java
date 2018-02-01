package com.pa.paperless.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/24.
 */

public class FileUtil {

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
        List<String> picList = new ArrayList<String>();
        try {
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath(), type)) {
                    picList.add(file.getPath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 返回得到的图片列表
        return picList;
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
     * 如果文件不存在，就创建文件
     *
     * @param path 文件路径
     * @return
     */
    public static String createIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return path;
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
     * 向文件中写入字符串String类型的内容
     *
     * @param file    文件路径
     * @param content 文件内容
     * @param charset 写入时候所使用的字符集
     */
    public static void writeString(String file, String content, String charset) {
        try {
            byte[] data = content.getBytes(charset);
            writeBytes(file, data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 从文件中读取数据，返回类型是字符串String类型
     *
     * @param file    文件路径
     * @param charset 读取文件时使用的字符集，如utf-8、GBK等
     * @return
     */
    public static String readString(String file, String charset) {
        byte[] data = readBytes(file);
        String ret = null;

        try {
            ret = new String(data, charset);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }


    /**
     * 打开文件
     *
     * @param file
     */
    public static void openFile(Context context, File file) {
        //Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
//        Collection mimeTypes = getMimeTypes(file);
//        String type = mimeTypes.toString();
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * 打开 WPS 文件
     *
     * @param context
     * @param file
     */
    public static void openFileByWps(Context context, File file) {
        Intent intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName("cn.wps.moffice", "cn.wps.moffice.documentmanager.PreStartActivity");
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.startActivity(intent);
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