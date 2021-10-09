package com.yunovo.rtmppush.filepush;

import android.text.TextUtils;
import android.util.Log;

import com.yunovo.rtmppush.RtmpPush;
import com.yunovo.rtmppush.TaskManager;

public class FilePush implements Runnable{
    private String mFilePath;
    private String mServerUrl;
    private volatile boolean isStart=false;
    public void startPush(String url,String file){
        mFilePath=file;
        mServerUrl=url;
        TaskManager.getInstance().execute(this);

    }

    @Override
    public void run() {
        if(TextUtils.isEmpty(mFilePath))return;
        if(TextUtils.isEmpty(mServerUrl)) return;
         if(isStart)return;
        if(RtmpPush.getRtmpPush().connect(mServerUrl,RtmpPush.RTMP_FILE_PUSH)!=0){
           return;
        }
        isStart=true;
        RtmpPush.getRtmpPush().startFilePush(mFilePath);
        Log.e("rtmp","finish file push");
    }

    public void close(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RtmpPush.getRtmpPush().close(RtmpPush.RTMP_FILE_PUSH);
                isStart=false;
            }
        }).start();
    }
}
