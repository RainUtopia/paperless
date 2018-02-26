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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Gowcage
 */
public class ScreenRecorder extends Thread {

    private final String TAG = "ScreenRecorder-->";
    private static final String MIME_TYPE = "video/avc";// h.264����
    private static final int FRAME_RATE = 15;// ֡��
    private static final int IFRAME_INTERVAL = 10;// �ؼ�֡���
    private static final int TIMEOUT_US = 10 * 1000;// ��ʱ

    private int width;
    private int height;
    private int bitrate;
    private int dpi;
    private String savePath;
    private AtomicBoolean quit = new AtomicBoolean(false);
    private boolean muxerStarted = false;
    private int videoTrackIndex = -1;// ��Ƶ�������

    private NativeUtil jni = NativeUtil.getInstance();

    private MediaProjection projection;
    private MediaMuxer muxer;
    private VirtualDisplay display;
    private Surface mSurface;
    private MediaCodec encoder;
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private ByteBuffer rawData, encoderData;

    public ScreenRecorder(int width, int height, int bitrate, int dpi,
                          MediaProjection projection, String savePath) {
        Log.d(TAG, "ScreenRecorder: width:" + width + "height:" + height);

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
                prepareEncoder();// ��ʼ��������
//                rawData = getRawData(encoder);

                // Muxer��Ҫ����һ���ļ�·���������������Ƶ�������������ʽ
//                muxer = new MediaMuxer(savePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e);
            }
            // 4:����VirtualDisplayʵ��,DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC / DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
            display = projection.createVirtualDisplay("MainScreen", width, height, dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mSurface, null, null);
            Log.d(TAG, "created virtual display: " + display);
           
            recordVirtualDisplay();// ¼��������Ļ
           
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
     * ��ʼ��������
     *
     * @throws IOException
     */
    private void prepareEncoder() throws IOException {
        Log.e(TAG, "prepareEncoder---------------------------");
        // ����ý���ʽ
    	jni.InitAndCapture(0,  2);
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);// ��ɫ��ʽ
        // COLOR_FormatSurface����������ݽ���һ��graphicBufferԪ����
        // ��һ��Android surface����mediaCodec����
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);// ���� Խ��Խ����
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);// ֡�� Խ��Խ����,24���»Ῠ��
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);// ������ɫ��ʽ
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);// �ؼ�֡���ʱ��s
        // IFRAME_INTERVAL��ָ��֡�������ָ���ǣ��ؼ�֡�ļ��ʱ�䡣ͨ������£����óɶ������ⶼ����
        // �������ó�10���Ǿ���10��һ���ؼ�֡�����ǣ����������Ҫ����Ƶ��Ԥ������������ó�1
        // ��Ϊ������ó�10���ᷢ�֣�10���ڵ�Ԥ������һ����ͼ
        Log.d(TAG, "created video format: " + format);
        // ����MediaCodecʵ��
        encoder = MediaCodec.createEncoderByType(MIME_TYPE);// ���ﴴ�����Ǳ�����
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);// ���ñ���������

        mSurface = encoder.createInputSurface();// ��һ���ǳ��ؼ��������õģ���MediaCodec�ı���Դ��Ҳ����˵��Ҫ����Encoder������Щ����
        Log.d(TAG, "created input surface: " + mSurface);
        encoder.start();// ��ʼ����
        createfile();
    }

    public byte[] configbyte;

    /**
     * ¼��������Ļ
     *
     * @throws IOException
     */
    private void recordVirtualDisplay() {
        Log.w(TAG, "recordVirtualDisplay---------------------------");
        while (!quit.get()) {
            int index = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);// �����������ȡ��������,�����ѳɹ���������������������

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
                    jni.call(2, keyframe);
                    //jni
                } else {
                   /* try {
                        outputStream.write(outData, 0, outData.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    jni.call(2, outData);
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
     * ���������ʽ
     */
    private void resetOutputFormat() {
        Log.e(TAG, "resetOutputFormat---------------------------");
        // Ӧ���ڽ��ջ�����֮ǰ����������Ӧ��ֻ����һ��
        if (muxerStarted) {// ���muxer������
            throw new IllegalStateException("�����ʽ�Ѹ���!");
        }
        MediaFormat newFormat = encoder.getOutputFormat();
        // �ڴ�Ҳ���Խ���sps��pps�Ļ�ȡ����ȡ��ʽ�μ�����getSpsPpsByteBuffer()
        Log.i(TAG, "�����ʽ�Ѹ���.\n �¸�ʽ: " + newFormat.toString());
//        videoTrackIndex = muxer.addTrack(newFormat);
//        muxer.start();
        muxerStarted = true;
        Log.i(TAG, "started media muxer, videoIndex=" + videoTrackIndex);
    }

    /**
     * ���뵽��Ƶ���
     *
     * @param index �������������
     */
    private void encodeToVideoTrack(int index) {
        Log.e(TAG, "encodeToVideoTrack---------------------------");
        ByteBuffer encodedData = encoder.getOutputBuffer(index);// ��������Ƶ����

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {// ���ض���ʽ��Ϣ���������ݣ�����ý������
            // �����INFO_OUTPUT_FORMAT_CHANGED״̬ʱ����������������ݱ����������͵�muxer�� ��������
            Log.d(TAG, "���� BUFFER_FLAG_CODEC_CONFIG");
            bufferInfo.size = 0;

        }
        if (bufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, ������.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + bufferInfo.size
                    + ", presentationTimeUs=" + bufferInfo.presentationTimeUs
                    + ", offset=" + bufferInfo.offset);
        }
        if (encodedData != null) {// �б�������
            encodedData.position(bufferInfo.offset);// �൱��һ���α꣨cursor������¼�����￪ʼд���ݣ������￪ʼ�����ݡ�
            encodedData.limit(bufferInfo.offset + bufferInfo.size);// ���������ж��������ܹ�ȡ�����߻��������ж����������ڴ�����ݣ�
            muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo);

            rawData = null;
            rawData = encodedData;
            Log.i(TAG, "sent " + bufferInfo.size + " bytes to muxer...");
        }
    }

    /**
     * �ͷ���Դ
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

    // ��ȡ����ǰ����
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

    /*// ��ȡ����ǰ�����ݣ�jni�������
    public ByteBuffer getByteBufferData() {
        return rawData;
    }

    // ��ȡ����������
    public ByteBuffer getEncoderData() {
        return encoderData;
    }*/

    // ��ȡsps pps��ByteBuffer��ע��˴���sps pps����read-onlyֻ��״̬
    private void getSpsPpsByteBuffer(MediaFormat newFormat) {
        ByteBuffer rawSps = newFormat.getByteBuffer("csd-0");
        ByteBuffer rawPps = newFormat.getByteBuffer("csd-1");
    }

}
