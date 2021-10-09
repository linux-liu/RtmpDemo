package com.yunovo.rtmppush.livepush;

public abstract class ICodec extends Thread{

     void putYUV(byte[]data){

    }

    abstract void waitWorkFinish();

    abstract void release();

}
