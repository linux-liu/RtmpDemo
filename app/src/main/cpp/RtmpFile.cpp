//
// Created by liuxin on 21-7-15.
//



#include "RtmpFile.h"

RtmpFile::RtmpFile() {
    mfilePath = NULL;

}

/*read 1 byte*/
static int ReadU8(uint32_t *u8, FILE *fp) {
    if (fread(u8, 1, 1, fp) != 1)
        return 0;
    return 1;
}

/*read 3 byte*/
static int ReadU24(uint32_t *u24, FILE *fp) {
    if (fread(u24, 3, 1, fp) != 1)
        return 0;
    *u24 = HTON24(*u24);
    return 1;
}

/*read 4 byte*/
static int ReadU32(uint32_t *u32, FILE *fp) {
    if (fread(u32, 4, 1, fp) != 1)
        return 0;
    *u32 = HTON32(*u32);
    return 1;
}

/*read 1 byte,and loopback 1 byte at once*/
static int PeekU8(uint32_t *u8, FILE *fp) {
    if (fread(u8, 1, 1, fp) != 1)
        return 0;
    fseek(fp, -1, SEEK_CUR);
    return 1;
}

/*read 4 byte and convert to time format*/
static int ReadTime(uint32_t *utime, FILE *fp) {
    if (fread(utime, 4, 1, fp) != 1)
        return 0;
    *utime = HTONTIME(*utime);
    return 1;
}

int RtmpFile::filePush(const char *filePath) {
    isFinish = false;
    uint32_t startTime = 0, type = 0, dataLength = 0, timeStamp = 0, streamId = 0, preTagSize = 0;
    long preFrameTime = 0, lastTime = 0;
    size_t len = strlen(filePath);
    if (mfilePath != NULL) {
        free(mfilePath);
    }
    mfilePath = static_cast<char *>(malloc(len + 1));
    memset(mfilePath, 0, len + 1);
    memcpy(mfilePath, filePath, len);
    fp = fopen(mfilePath, "rb");
    if (!fp) {
        if (IS_DEBUG) {
            ALOGD("open fail error");
        }
        if (mfilePath != NULL) {
            free(mfilePath);
        }
        return RTMP_OPEN_FILE_ERROR;
    }
    packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, 1024 * 64);
    RTMPPacket_Reset(packet);
    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_nInfoField2 = mRtmp->m_stream_id;
    if (IS_DEBUG) {
        ALOGD("开始发送数据");
    }
    //FLV格式(flv_head preTAGSize Tag preTAgSize TAG ....)   flv header 9个字节 然后就是preTAGsize 占4个字节表示上一个tag的长度 然后就是tag
    //Tag有三种类型 script tag、 audio tag 、video tag 具体存储格式自己百度了解
    //flv格式的存储方式比较简单，如果是mp4格式好复杂，找个第三方开源的库解码获取音频视频压缩数据吧
    //jump over FLV Header
    fseek(fp, 9, SEEK_SET);
    //jump over previousTagSizen
    fseek(fp, 4, SEEK_CUR);
    startTime = RTMP_GetTime();
    isPush = true;
    while (isPush) {
        if ((((RTMP_GetTime()) - startTime)
             < (preFrameTime))/* && bNextIsKey*/) {
            //wait for 1 sec if the send process is too fast
            //this mechanism is not very good,need some improvement
            if (preFrameTime > lastTime) {
                if (IS_DEBUG)
                    ALOGD("TimeStamp:%8lu ms\n", preFrameTime);
                lastTime = preFrameTime;
            }
            usleep(1000 * 100);
            continue;
        }
        //这里注意是小端cpu ,从文件读取的0xFFEE 到cpu
        //not quite the same as FLV spec
        if (!ReadU8(&type, fp))
            break;
        if (IS_DEBUG)
            ALOGD("type=>%x", type);
        if (!ReadU24(&dataLength, fp))
            break;
        if (!ReadTime(&timeStamp, fp))
            break;
        if (!ReadU24(&streamId, fp))
            break;

        if (type != 0x08 && type != 0x09) {
            //jump over non_audio and non_video frame，
            //jump over next previousTagSizen at the same time
            fseek(fp, dataLength + 4, SEEK_CUR);
            continue;
        }
        if (fread(packet->m_body, 1, dataLength, fp) != dataLength)
            break;

        packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
        packet->m_nTimeStamp = timeStamp;
        packet->m_packetType = type;
        packet->m_nBodySize = dataLength;
        preFrameTime = timeStamp;

        if (!RTMP_IsConnected(mRtmp)) {
            if (IS_DEBUG)
                ALOGE("rtmp is not connect\n");
            break;
        }
        if (!RTMP_SendPacket(mRtmp, packet, 0)) {
            if (IS_DEBUG)
                ALOGE("Send Error\n");
            break;
        }
        if (!ReadU32(&preTagSize, fp))
            break;

        if (!PeekU8(&type, fp))
            break;
        if (type == 0x09) {
            if (fseek(fp, 11, SEEK_CUR) != 0)
                break;
            if (!PeekU8(&type, fp)) {
                break;
            }
            fseek(fp, -11, SEEK_CUR);
        }
    }

    if (fp)
        fclose(fp);
    fp = NULL;
    RTMPPacket_Free(packet);
    free(packet);
    if (mRtmp != NULL) {
        RTMP_Close(mRtmp);
        RTMP_Free(mRtmp);
        mRtmp = NULL;
    }
    if (mserverUrl) {
        free(mserverUrl);
        mserverUrl = NULL;
    }
    if (mfilePath != NULL) {
        free(mfilePath);
    }
    mfilePath = NULL;
    isFinish = true;
    return RTMP_SUCCESS;
}

RtmpFile::~RtmpFile() {

}

void RtmpFile::close() {
    isPush = false;
    int count = 50;
    while (!isFinish) {
        usleep(1000 * 100);
        count--;
        if (count == 0) {
            break;
        }
    }

    isFinish=false;

}


