package com.yunovo.rtmppush.livepush;

import android.text.TextUtils;
import android.util.Log;

import com.yunovo.rtmppush.RtmpPush;
import com.yunovo.rtmppush.TaskManager;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 根据摄像头直播推流
 */
public class LivePush implements Runnable {
    private volatile boolean mIsLiving;
    private LinkedBlockingQueue<RTMPPackage> mQueue = new LinkedBlockingQueue<>();
    private String mServiceUrl;
    private ICodec mAudioCodec, mVideoCodec;


    public void startLive(String serviceUrl) {
        this.mServiceUrl = serviceUrl;
        TaskManager.getInstance().execute(this);
    }

    public void putYUV(byte []data){
        if(mVideoCodec!=null){
            mVideoCodec.putYUV(data);
        }
    }

    public boolean isLiving() {
        return mIsLiving;
    }

    @Override
    public void run() {
        if (mIsLiving) {
            return;
        }
        if (TextUtils.isEmpty(mServiceUrl)) {
            return;
        }
        int ret=RtmpPush.getRtmpPush().connect(mServiceUrl,RtmpPush.RTMP_DATA_PUSH);
        Log.e("rtmp","ret=>"+ret);
        if (ret != 0) {
            return;
        }
        mAudioCodec = new AudioCodec(this);
        mAudioCodec.start();

        mVideoCodec = new VideoCodec( this);
        mVideoCodec.start();
        mIsLiving = true;
        while (mIsLiving) {
            RTMPPackage rtmpPackage = null;
            try {
                rtmpPackage = mQueue.poll();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (rtmpPackage!=null&&rtmpPackage.getBuffer() != null && rtmpPackage.getBuffer().length != 0) {
                RtmpPush.getRtmpPush().startDataPush(rtmpPackage.getBuffer(), rtmpPackage.getBuffer()
                        .length, rtmpPackage.getTms(), rtmpPackage.getType());
            }
        }

        if(mAudioCodec!=null){
            mAudioCodec.release();
            mAudioCodec.waitWorkFinish();
            mAudioCodec=null;
        }
        if(mVideoCodec!=null){
            mVideoCodec.release();
            mVideoCodec.waitWorkFinish();
            mVideoCodec=null;
        }
        mQueue.clear();
        RtmpPush.getRtmpPush().close(RtmpPush.RTMP_DATA_PUSH);
    }

    public void addPackage(RTMPPackage pkt){
        mQueue.offer(pkt);
    }


    public void release() {
        mIsLiving=false;

    }
}
