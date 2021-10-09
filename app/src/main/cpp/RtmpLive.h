/**
 *
 * 摄像头直播推流
 * 传入h264 和AAC数据进行包装推流
 *
 */
#ifndef RTMPDEMO_LIVE_H
#define RTMPDEMO_LIVE_H
#include "BaseRtmp.h"
class RtmpLive :public BaseRtmp {
private:

     int sps_len=0;
     int pps_len=0;
     int8_t *sps=NULL;
     int8_t *pps=NULL;
    int sendPacket(RTMPPacket *packet);
    RTMPPacket *createAudioPacket(const int8_t *data, const int len, const int type, const long tms);
    void prepareVideo(const int8_t *data, int len);
    RTMPPacket *createSpsPpsVideoPackage();
    RTMPPacket *createVideoPackage(const int8_t *buf, int len, const long tms);

public:
    RtmpLive();
    int sendAudioData(const int8_t *data,int len, long ms,int type);
    int sendVideoData(const int8_t *data, int len, long ms);
    void close();
     ~RtmpLive();
};


#endif //RTMPDEMO_LIVE_H
