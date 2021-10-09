package com.yunovo.rtmppush;

/**
 * create by liuxin
 */
public class RtmpPush {
    private static RtmpPush mRtmpPush;

    //支持2种类型流同时进行 别更改这2个值 与底层约定好的
    /***
     * 数据推流 目前就是摄像头、麦克风直播推流
     */
    public static int RTMP_DATA_PUSH=0;
    /**
     * FLV文件推流,目前只支持FLV文件
     */
    public static int RTMP_FILE_PUSH=1;

    public static RtmpPush getRtmpPush() {
        if (mRtmpPush == null) {
            synchronized (RtmpPush.class) {
                if (mRtmpPush == null) {
                    mRtmpPush = new RtmpPush();
                }
            }
        }
        return mRtmpPush;
    }

    static {
        System.loadLibrary("rtmp-push");
    }


    /**
     * 连接服务器
     * @param serverUrl 连接的服务器地址
     * @param type 推流类型 @see RTMP_DATA_PUSH  RTMP_FILE_PUSH
     * @return
     */
    public native int connect(String serverUrl,int type);


    /**
     * 直接得到压缩数据推流
     * @param data
     * @param length
     * @return
     */
    public native int startDataPush(byte []data,int length,long tms, int type);

    /**
     * 文件地址 进行文件推流 ,需要在java 层获取读取sd卡权限
     * @param filePath
     * @return
     */
    public native int startFilePush(String filePath);

    /**
     * 关闭推流 @see RTMP_DATA_PUSH  RTMP_FILE_PUSH
     * @param
     */
    public native void close(int type);

}
