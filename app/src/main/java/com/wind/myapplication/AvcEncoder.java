package com.wind.myapplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

public class AvcEncoder {
	private final static String TAG = "MeidaCodec";

	private int TIMEOUT_USEC = 12000;
	private NativeUtil jni = NativeUtil.getInstance();
	private MediaCodec mediaCodec;
	int m_width;
	int m_height;
	int m_framerate;
	byte[] m_info = null;

	public byte[] configbyte;

	@SuppressLint("NewApi")
	public AvcEncoder(int width, int height, int framerate, int bitrate) throws IOException {

		jni.InitAndCapture(0,  2);
		m_width = width;
		m_height = height;
		m_framerate = framerate;

		mediaCodec = MediaCodec.createEncoderByType("video/avc");
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc",
				m_width, m_height);// MIME_TYPE = "video/avc",H264��MIME���ͣ�����
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);// ���ñ�����
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, m_framerate);// ����֡��
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
				MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);// ������ɫ��ʽ
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);// ���ùؼ�֡���ʱ��

		mediaCodec.configure(mediaFormat, null, null,
				MediaCodec.CONFIGURE_FLAG_ENCODE);// �ĸ���������һ����media��ʽ���ڶ����ǽ��������ŵ�surfaceview����������MediaCrypto�����ĸ��Ǳ������ı�ʶ
		mediaCodec.start();
		createfile();
	}

	private static String path = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/test1.h264";
	private BufferedOutputStream outputStream;
	FileOutputStream outStream;

	private void createfile() {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void StopEncoder() {
		try {
			mediaCodec.stop();
			mediaCodec.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	ByteBuffer[] inputBuffers;
//	ByteBuffer[] outputBuffers;
	public boolean isRuning = false;

	public void StopThread() {
		isRuning = false;
		try {
			StopEncoder();
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int count = 0;

	public void StartEncoderThread() {
		Thread EncoderThread = new Thread(new Runnable() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				isRuning = true;
				byte[] input = null;
				long pts = 0;
				long generateIndex = 0;
				byte[] yuv420sp = new byte[m_width * m_height * 3 / 2];
				
				while (isRuning) {
					if (CameraDemo.YUVQueue.size() > 0) {
						input = CameraDemo.YUVQueue.poll();// ����Դ
						//byte[] yuv420sp = new byte[m_width * m_height * 3 / 2];
						
						NV21ToNV12(input, yuv420sp, m_width, m_height);
						// swapYV12toNV12(input, yuv420sp, m_width, m_height);
						input = yuv420sp;
					}
					if (input != null) {
						try {
							long startMs = System.currentTimeMillis();
							ByteBuffer[] inputBuffers = mediaCodec
									.getInputBuffers();
							ByteBuffer[] outputBuffers = mediaCodec
									.getOutputBuffers();
							int inputBufferIndex = mediaCodec
									.dequeueInputBuffer(-1);
							if (inputBufferIndex >= 0) {
								pts = computePresentationTime(generateIndex);
								ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
								inputBuffer.clear();
								inputBuffer.put(input);
								mediaCodec.queueInputBuffer(inputBufferIndex,
										0, input.length, pts, 0);
								generateIndex += 1;
							}

							MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
							int outputBufferIndex = mediaCodec
									.dequeueOutputBuffer(bufferInfo,
											TIMEOUT_USEC);
							while (outputBufferIndex >= 0) {
								// Log.i("AvcEncoder",
								// "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
								ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
								byte[] outData = new byte[bufferInfo.size];
								outputBuffer.get(outData);
								if (bufferInfo.flags == 2) {
									configbyte = new byte[bufferInfo.size];
									configbyte = outData;
								} else if (bufferInfo.flags == 1) {
									byte[] keyframe = new byte[bufferInfo.size
											+ configbyte.length];
									System.arraycopy(configbyte, 0, keyframe,
											0, configbyte.length);
									System.arraycopy(outData, 0, keyframe,
											configbyte.length, outData.length);

									//outputStream.write(keyframe, 0,
									//		keyframe.length);
						
									jni.call(2, keyframe);
								} else {
									//outputStream.write(outData, 0,
									//		outData.length);
									
									jni.call(2, outData);
								}

								mediaCodec.releaseOutputBuffer(
										outputBufferIndex, false);
								outputBufferIndex = mediaCodec
										.dequeueOutputBuffer(bufferInfo,
												TIMEOUT_USEC);
							}

						} catch (Throwable t) {
							t.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		EncoderThread.start();

	}

	private void swapYV12toNV12(byte[] yv12bytes, byte[] nv12bytes, int width,
			int height) {

		int nLenY = width * height;
		int nLenU = nLenY / 4;

		System.arraycopy(yv12bytes, 0, nv12bytes, 0, width * height);
		for (int i = 0; i < nLenU; i++) {
			nv12bytes[nLenY + 2 * i] = yv12bytes[nLenY + i];
			nv12bytes[nLenY + 2 * i + 1] = yv12bytes[nLenY + nLenU + i];
		}
	}

	private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
		if (nv21 == null || nv12 == null)
			return;
		int framesize = width * height;
		int i = 0, j = 0, tmpval = 0, tmpval2 = framesize / 2;
		System.arraycopy(nv21, 0, nv12, 0, framesize);
//		for (i = 0; i < framesize; i++) {
//			nv12[i] = nv21[i];
//		}
		//for (j = 0; j < framesize / 2; j += 2) {
				//	nv12[framesize + j] = nv21[j + framesize - 1];
				//}
		long tmptm = System.currentTimeMillis();
		Log.i(TAG, "start "+tmptm);
		for (j = 0; j < tmpval2; j += 2) 
		{
			tmpval = framesize + j;
			nv12[tmpval - 1] = nv21[tmpval];
			nv12[tmpval] = nv21[tmpval - 1];
		}
		tmptm = System.currentTimeMillis() - tmptm;
		Log.i(TAG, "finish"+tmptm);
		//for (j = 0; j < framesize / 2; j += 2) {
		//	nv12[framesize + j] = nv21[j + framesize - 1];
		//}
	}

	/**
	 * ����֡N�ĳ���ʱ�䣨��΢��Ϊ��λ����
	 */
	private long computePresentationTime(long frameIndex) {
		return 132 + frameIndex * 1000000 / m_framerate;
	}
}
