package com.yunovo.rtmppush.livepush;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoCodec extends ICodec {
    public LivePush mLivePush;
    private static final int WIDTH = 720;
    private static final int HEIGHT = 1280;
    private MediaCodec mMediaCodec;
    private volatile boolean mIsLiving;
    private long mStartTime;
    private volatile Thread mWorkThread;

    private LinkedBlockingQueue<byte[]> mQueue = new LinkedBlockingQueue<>();

    public VideoCodec(LivePush livePush) {
        this.mLivePush = livePush;
    }

    @Override
    public void start() {
        Log.e("rtmp", "video start");
        mIsLiving = true;
        super.start();

    }



    @Override
    public void putYUV(byte[] data) {
        Log.e("rtmp", "put");
        if (data != null && data.length == WIDTH * HEIGHT * 3 / 2) {
            Log.e("rtmp", "put after");
            mQueue.offer(data);
        }
    }

    @Override
    void waitWorkFinish() {
        if(mWorkThread!=null){
            try {
                mWorkThread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Log.e("rtmp", "video run");
        mWorkThread=Thread.currentThread();
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,
                WIDTH,
                HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        //码率，帧率，分辨率，关键帧间隔
        format.setInteger(MediaFormat.KEY_BIT_RATE, 1000_000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);

        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);//手机
            mMediaCodec.configure(format, null, null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE);

        } catch (IOException e) {
            Log.e("rtmp", "video error");
            e.printStackTrace();
        }
        mMediaCodec.start();

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        Log.e("rtmp", "video start"+mIsLiving);
        while (mIsLiving) {
            byte[] data = mQueue.poll();
            if(data==null)continue;
            int inputIndex = mMediaCodec.dequeueInputBuffer(0);

            Log.d("rtmp", " inputIndex " + inputIndex + "inputFormat=>" + mMediaCodec.getInputFormat());
            if (inputIndex >= 0) {
                ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputIndex);
                if (inputBuffer != null) {
                    inputBuffer.clear();
                    try {
                        byte[] outNV12 = new byte[data.length];
                        long now = System.currentTimeMillis();

                        YUV420spRotate270(outNV12, data, HEIGHT, WIDTH);
                        NV21ToNV12(outNV12, data, WIDTH, HEIGHT);
                        Log.d("rtmp", "spend=>" + (System.currentTimeMillis() - now));
                        inputBuffer.put(data);
                        mMediaCodec.queueInputBuffer(inputIndex, 0, data.length, System.nanoTime() / 1000, 0);
                        Log.e("rtmp", " queueInputBuffer after");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("rtmp", "input error");
                        continue;
                    }
                } else {
                    Log.e("rtmp", "input error null buffer");
                    continue;
                }

            } else {
              //  Log.e("rtmp", "input error index ");
                continue;
            }

            int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            Log.e("rtmp", "out index " + index);
            while (index >= 0) {
                ByteBuffer buffer = mMediaCodec.getOutputBuffer(index);
                MediaFormat mediaFormat = mMediaCodec.getOutputFormat(index);
                Log.i("rtmp", "mediaFormat: " + mediaFormat.toString());
                byte[] outData = new byte[bufferInfo.size];
                buffer.get(outData);
                if (mStartTime == 0) {
                    // 微妙转为毫秒
                    mStartTime = bufferInfo.presentationTimeUs / 1000;
                }
                RTMPPackage rtmpPackage = new RTMPPackage(outData, (bufferInfo.presentationTimeUs / 1000) - mStartTime);
                rtmpPackage.setType(RTMPPackage.RTMP_PACKET_TYPE_VIDEO);
                if (mLivePush != null)
                    mLivePush.addPackage(rtmpPackage);
                mMediaCodec.releaseOutputBuffer(index, false);

                index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
        }

        Log.e("rtmp", "video thread end ") ;
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
        mQueue.clear();
        mWorkThread=null;

    }

    //顺时针旋转270度
    public static void YUV420spRotate270(byte[] des, byte[] src, int width, int height) {
        int n = 0;
        int uvHeight = height >> 1;
        int wh = width * height;
        //copy y
        for (int j = width - 1; j >= 0; j--) {
            for (int i = 0; i < height; i++) {
                des[n++] = src[width * i + j];
            }
        }

        for (int j = width - 1; j > 0; j -= 2) {
            for (int i = 0; i < uvHeight; i++) {
                des[n++] = src[wh + width * i + j - 1];
                des[n++] = src[wh + width * i + j];
            }
        }
    }
    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }

    @Override
    public void release() {
        try {
            mIsLiving = false;

            mStartTime = 0;
            mLivePush = null;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
