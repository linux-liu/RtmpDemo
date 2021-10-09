//
//文件推流
// Created by liuxin on 21-7-15.
//

#ifndef RTMPDEMO_RTMPFILE_H
#define RTMPDEMO_RTMPFILE_H
#include "BaseRtmp.h"
#include <unistd.h>


//16位 24位 32位 小端存储转换成文件中真实数据 ,时间最后一位为扩展时间戳
#define HTON16(x)  ((x>>8&0xff)|(x<<8&0xff00))
#define HTON24(x)  ((x>>16&0xff)|(x<<16&0xff0000)|(x&0xff00))
#define HTON32(x)  ((x>>24&0xff)|(x>>8&0xff00)|\
         (x<<8&0xff0000)|(x<<24&0xff000000))
#define HTONTIME(x) ((x>>16&0xff)|(x<<16&0xff0000)|(x&0xff00)|(x&0xff000000))
class RtmpFile: public BaseRtmp{
private:
    char *mfilePath=NULL;
    volatile bool isPush = true;
    FILE*fp=NULL;
    RTMPPacket *packet;
    volatile bool  isFinish= false;
public:
    RtmpFile();

    int filePush(const char * filePath);
    void close();

    ~RtmpFile();

};


#endif //RTMPDEMO_RTMPFILE_H
