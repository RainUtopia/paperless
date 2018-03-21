package com.wind.myapplication;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.pa.paperless.activity.MeetingActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Gowcage
 */
public class ScreenRecorder extends Thread {

    private final String TAG = "ScreenRecorder-->";
    private static final String MIME_TYPE = "video/avc";// h.264编码
    private static final int FRAME_RATE = 15;// 帧率
    private static final int IFRAME_INTERVAL = 10;// 关键帧间隔
    private static final int TIMEOUT_US = 10 * 1000;// 超时

    private int width;
    private int height;
    private int bitrate;
    private int dpi;
    private String savePath;
    private AtomicBoolean quit = new AtomicBoolean(false);
    private boolean muxerStarted = false;
    private int videoTrackIndex = -1;// 视频轨道索引

    private NativeUtil jni = NativeUtil.getInstance();

    private MediaProjection projection;
    private MediaMuxer muxer;
    private VirtualDisplay display;
    private Surface mSurface;
    private MediaCodec encoder;
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private ByteBuffer rawData, encoderData;

    private final int channelIndex = 2;

    public ScreenRecorder(int width, int height, int bitrate, int dpi, MediaProjection projection, String savePath) {
        Log.d(TAG, "ScreenRecorder: width:" + width + "height:" + height);

        jni.InitAndCapture(0, channelIndex);
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.dpi = dpi;
        this.projection = projection;
        this.savePath = savePath;
    }

    public void quit() {
        quit.set(true);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        try {
            try {
                prepareEncoder();// 初始化编码器
//                rawData = getRawData(encoder);

                // Muxer需要传入一个文件路径来保存输出的视频，并传入输出格式
//                muxer = new MediaMuxer(savePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e);
            }
            // 4:创建VirtualDisplay实例,DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC / DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
            display = projection.createVirtualDisplay("MainScreen", width, height, dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mSurface, null, null);
            Log.d(TAG, "created virtual display: " + display);

            recordVirtualDisplay();// 录制虚拟屏幕

        } finally {
            release();
        }
    }

    private BufferedOutputStream outputStream;

    private void createfile() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.h264");
        if (file.exists()) {
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化编码器
     *
     * @throws IOException
     */
    private void prepareEncoder() throws IOException {
        Log.e(TAG, "prepareEncoder---------------------------");
        // 设置媒体格式
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);// 颜色格式
        // COLOR_FormatSurface这里表明数据将是一个graphicBuffer元数据
        // 将一个Android surface进行mediaCodec编码
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);// 码率 越高越清晰
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);// 帧数 越高越流畅,24以下会卡顿
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);// 设置颜色格式
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);// 关键帧间隔时间s
        // IFRAME_INTERVAL是指的帧间隔，它指的是，关键帧的间隔时间。通常情况下，设置成多少问题都不大。
        // 比如设置成10，那就是10秒一个关键帧。但是，如果有需求要做视频的预览，那最好设置成1
        // 因为如果设置成10，会发现，10秒内的预览都是一个截图
        Log.d(TAG, "created video format: " + format);
        // 创建MediaCodec实例
        encoder = MediaCodec.createEncoderByType(MIME_TYPE);// 这里创建的是编码器�
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);// 配置编码器属性

        mSurface = encoder.createInputSurface();// 这一步非常关键，它设置的，是MediaCodec的编码源，也就是说，要告诉Encoder解码哪些流。
        Log.d(TAG, "created input surface: " + mSurface);
        encoder.start();// 开始编码
        createfile();
    }

    public byte[] configbyte;

    /**
     * 录制虚拟屏幕
     *
     * @throws IOException
     */
    private void recordVirtualDisplay() {
        Log.w(TAG, "recordVirtualDisplay---------------------------");
        while (!quit.get()) {
            int index = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);// 输出流队列中取数据索引,返回已成功解码的输出缓冲区的索引

            ByteBuffer[] outputBuffers = encoder.getOutputBuffers();

            while (index >= 0) {
                //Log.i("AvcEncoder", "Get H264 Buffer Success! flag = " + bufferInfo.flags + ",pts = " + bufferInfo.presentationTimeUs + "");
                ByteBuffer outputBuffer = outputBuffers[index];
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                    Log.d(TAG, "get config byte!");
                    configbyte = new byte[bufferInfo.size];
                    configbyte = outData;
                } else if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                    Log.d(TAG, "get keyframe byte!");
                    byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                    System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                    System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                   /* try {
                        outputStream.write(keyframe, 0, keyframe.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    jni.call(channelIndex, keyframe);
                    //jni
                } else {
                   /* try {
                        outputStream.write(outData, 0, outData.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    jni.call(channelIndex, outData);
                    //jni
                }
                encoder.releaseOutputBuffer(index, false);
                index = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);
            }

            /*Log.i(TAG, "dequeue output buffer index=" + index);// �����������������
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {// ��������ʽ�����ˣ��������������ʽ
                resetOutputFormat();
            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {// �������Ϊ-1�Ժ����ԣ���ʱ�������߳�����10����
                Log.d(TAG, "������������ʱ!");
                try {
                    // wait 10ms
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } else {
                while (index >= 0) {// index>=0Ϊ��Ч���
                    if (!muxerStarted) {// ���muxerδ����
                        throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
                    }
//                    encodeToVideoTrack(index);// ���뵽��Ƶ���

                    //--
                    *//*ByteBuffer[] outputBuffers = encoder.getOutputBuffers();
                    ByteBuffer outputBuffer = outputBuffers[index];
                    byte[] outData = new byte[bufferInfo.size];
                    outputBuffer.get(outData);
                    if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        configbyte = new byte[bufferInfo.size];
                        configbyte = outData;
                    } else if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                        byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                        System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                        System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                        //jni
                    } else {
                        //jni
                    }*//*
                    //--

                    encoder.releaseOutputBuffer(index, false);// �ͷ�����ɵ�����
//                    index = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);
                }
            }*/
        }
    }

    /**
     * 重置输出格式
     */
    private void resetOutputFormat() {
        Log.e(TAG, "resetOutputFormat---------------------------");
        // 应该在接收缓冲区之前发生，并且应该只发生一次
        if (muxerStarted) {// 如果muxer已启动
            throw new IllegalStateException("输出格式已更改!");
        }
        MediaFormat newFormat = encoder.getOutputFormat();
        //在此也可以进行sps与pps的获取，获取方式参见方法getSpsPpsByteBuffer()
        Log.i(TAG, "输出格式已更改.\\n 新格式: " + newFormat.toString());
//        videoTrackIndex = muxer.addTrack(newFormat);
//        muxer.start();
        muxerStarted = true;
        Log.i(TAG, "started media muxer, videoIndex=" + videoTrackIndex);
    }

    /**
     * 编码到视频轨道
     *
     * @param index 输出缓冲区索引
     */
    private void encodeToVideoTrack(int index) {
        Log.e(TAG, "encodeToVideoTrack---------------------------");
        ByteBuffer encodedData = encoder.getOutputBuffer(index);// 编码后的视频数据

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {// 是特定格式信息等配置数据，不是媒体数据
            // 当获得INFO_OUTPUT_FORMAT_CHANGED状态时，编解码器配置数据被拉出并馈送到muxer。 忽略它。
            Log.d(TAG, "忽略 BUFFER_FLAG_CODEC_CONFIG");
            bufferInfo.size = 0;

        }
        if (bufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, 放弃它.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + bufferInfo.size
                    + ", presentationTimeUs=" + bufferInfo.presentationTimeUs
                    + ", offset=" + bufferInfo.offset);
        }
        if (encodedData != null) {// 有编码数据
            encodedData.position(bufferInfo.offset);// 相当于一个游标（cursor），记录从哪里开始写数据，从哪里开始读数据。
            encodedData.limit(bufferInfo.offset + bufferInfo.size);// 缓冲区还有多少数据能够取出或者缓冲区还有多少容量用于存放数据；
            muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo);

            rawData = null;
            rawData = encodedData;
            Log.i(TAG, "sent " + bufferInfo.size + " bytes to muxer...");
        }
    }

    /**
     * 释放资源
     *
     * @throws IOException
     */
    private void release() {
        Log.e(TAG, "release---------------------------");
        if (encoder != null) {
            encoder.stop();
            encoder.release();
            encoder = null;
        }
        if (display != null) {
            display.release();
        }
        if (projection != null) {
            projection.stop();
        }
        if (muxer != null) {
            muxer.stop();
            muxer.release();
            muxer = null;
        }
    }

    // 获取编码前数据
    private ByteBuffer getRawData(MediaCodec encoder) {
        Log.w(TAG,
                "get ByteBuffers before encoding  ---------------------------");
        ByteBuffer inputBuffer = null;
        int index = encoder.dequeueInputBuffer(-1);
        if (index >= 0) {
            inputBuffer = encoder.getInputBuffer(index);
        }

        return inputBuffer;
    }

    /*// 获取编码前的数据（jni用这个）
    public ByteBuffer getByteBufferData() {
        return rawData;
    }

    // �获取编码后的数据
    public ByteBuffer getEncoderData() {
        return encoderData;
    }*/

    // 获取sps pps的ByteBuffer，注意此处的sps pps都是read-only只读状态
    private void getSpsPpsByteBuffer(MediaFormat newFormat) {
        ByteBuffer rawSps = newFormat.getByteBuffer("csd-0");
        ByteBuffer rawPps = newFormat.getByteBuffer("csd-1");
    }

}
