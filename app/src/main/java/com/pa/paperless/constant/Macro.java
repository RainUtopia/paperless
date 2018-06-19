package com.pa.paperless.constant;


import android.os.Environment;

import com.pa.paperless.utils.SDCardUtils;


/**
 * Created by Administrator on 2018/2/5.
 */

public class Macro {

    public static int DEVICE_MEET_DB = 0x01000000; //会议数据库(会议后台)
    public static int DEVICE_MEET_SERVICE = 0x01040000; //会议茶水服务
    public static int DEVICE_MEET_PROJECTIVE = 0x01080000;//会议投影机
    public static int DEVICE_MEET_CAPTURE = 0x010c0000; //会议流采集设备
    public static int DEVICE_MEET_CLIENT = 0x01100000;  //会议客户端
    public static int DEVICE_MEET_ONEKEYSHARE = 0x01200000; //会议一键同屏

    /**
     * *********** ******  上传文件  ****** ************
     **/
    //  大类
    public static int MEDIA_FILETYPE_AUDIO = 0x00000000; //音频
    public static int MEDIA_FILETYPE_VIDEO = 0x20000000; //视频
    public static int MEDIA_FILETYPE_RECORD = 0x40000000; //录制
    public static int MEDIA_FILETYPE_PICTURE = 0x60000000; //图片
    public static int MEDIA_FILETYPE_UPDATE = 0xe0000000; //升级
    public static int MEDIA_FILETYPE_TEMP = 0x80000000; //临时文件
    public static int MEDIA_FILETYPE_OTHER = 0xa0000000; //其它文件
    public static int MAINTYPEBITMASK = 0xe0000000;

    //  小类
    public static int MEDIA_FILETYPE_PCM = 0x01000000; //PCM文件
    public static int MEDIA_FILETYPE_MP3 = 0x02000000;    //MP3文件
    public static int MEDIA_FILETYPE_ADPCM = 0x03000000;//WAV文件
    public static int MEDIA_FILETYPE_FLAC = 0x04000000;    //FLAC文件
    public static int MEDIA_FILETYPE_MP4 = 0x07000000; //MP4文件
    public static int MEDIA_FILETYPE_MKV = 0x08000000;  //MKV文件
    public static int MEDIA_FILETYPE_RMVB = 0x09000000;  //RMVB文件
    public static int MEDIA_FILETYPE_RM = 0x0a000000; //RM文件
    public static int MEDIA_FILETYPE_AVI = 0x0b000000; //AVI文件
    public static int MEDIA_FILETYPE_BMP = 0x0c000000; //bmp文件
    public static int MEDIA_FILETYPE_JPEG = 0x0d000000; //jpeg文件
    public static int MEDIA_FILETYPE_PNG = 0x0e000000; //png文件
    public static int MEDIA_FILETYPE_OTHERSUB = 0x10000000; //其它文件
    public static int SUBTYPEBITMASK = 0x1f000000;
    //assets 文件存放目录
    public static String INITFILESDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NETCONFIG";//app各类文件存放路径 SD卡下
    public static String FILENAME = "client.ini";
    public static String FILENAME_DEV = "client.dev";
    //下载的文件存放目录
    public static final String PAPERLESSDATA = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MEETFILE/";
    public static final String MEETFILE = PAPERLESSDATA + "download/";
    public static final String MEETMATERIAL = MEETFILE + "meetFile/";//会议资料
    public static final String SHAREMATERIAL = MEETFILE + "shareFile/";//共享资料
    public static final String POSTILFILE = MEETFILE + "postilFile/";//批注文件

    public static final String VOTERESULT = PAPERLESSDATA + "exportFile/";//导出文件
    public static final String TAKE_PHOTO = PAPERLESSDATA + "scratch/";//临时文件
    //自定义 Fragment索引
    public static final int Pb_MEET_FUNCODE_AGENDA_BULLETIN = 0;    //会议议程
    public static final int Pb_MEET_FUNCODE_MATERIAL = 1;    //会议资料
    public static final int Pb_MEET_FUNCODE_SHAREDFILE = 2;    //	共享文件
    public static final int Pb_MEET_FUNCODE_POSTIL = 3;    //批注文件
    public static final int Pb_MEET_FUNCODE_MESSAGE = 4;    //会议交流
    public static final int Pb_MEET_FUNCODE_VIDEOSTREAM = 5;    //视频直播
    public static final int Pb_MEET_FUNCODE_WHITEBOARD = 6;    //白板
    public static final int Pb_MEET_FUNCODE_WEBBROWSER = 7;    //网页
    public static final int Pb_MEET_FUNCODE_VOTERESULT = 8;    //投票
    public static final int Pb_MEET_FUNCODE_SIGNINRESULT = 9;    //签到
    public static final int Pb_MEET_FUNCODE_DOCUMENT = 10;    //外部文档
}
