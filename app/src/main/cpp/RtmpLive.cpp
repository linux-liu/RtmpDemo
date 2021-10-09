//
// Created by liuxin on 21-7-15.
//



#include "RtmpLive.h"

RtmpLive::RtmpLive() {
    mserverUrl = NULL;
    mRtmp = NULL;
    sps_len = 0;
    pps_len = 0;
    sps = NULL;
    pps = NULL;
}



int RtmpLive::sendPacket(RTMPPacket *packet) {
    int r = RTMP_SendPacket(mRtmp, packet, 1);
    RTMPPacket_Free(packet);
    free(packet);
    return r;
}


RTMPPacket *RtmpLive::createAudioPacket(const int8_t *buf, const int len, const int type, const long tms) {

//    组装音频包  两个字节    是固定的   af    如果是第一次发  你就是 01       如果后面   00  或者是 01  aac
    int body_size = len + 2;
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, body_size);
//         音频头
    packet->m_body[0] = 0xAF;
    if (type == 1) {
//        头
        packet->m_body[1] = 0x00;
    } else {
        packet->m_body[1] = 0x01;
    }
    memcpy(&packet->m_body[2], buf, len);
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x05;
    packet->m_nBodySize = body_size;
    packet->m_nTimeStamp = tms;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = mRtmp->m_stream_id;
    return packet;
}

int RtmpLive::sendAudioData(const int8_t *data, int len, long ms, int type) {
    RTMPPacket *packet = createAudioPacket(data, len, type, ms);
    int ret = sendPacket(packet);
    return ret;
}

void RtmpLive::prepareVideo(const int8_t *data, int len) {
    for (int i = 0; i < len; i++) {
        //0x00 0x00 0x00 0x01
        if (i + 4 < len) {
            if (data[i] == 0x00 && data[i + 1] == 0x00
                && data[i + 2] == 0x00
                && data[i + 3] == 0x01) {
                //0x00 0x00 0x00 0x01 7 sps 0x00 0x00 0x00 0x01 8 pps
                //将sps pps分开
                //找到pps
                if (data[i + 4] == 0x68) {
                    //去掉界定符
                    if(sps){
                        free(sps);
                        sps=NULL;
                    }
                    if(pps){
                        free(pps);
                        pps=NULL;
                    }
                    sps_len = i - 4;
                    sps = static_cast<int8_t *>(malloc(sps_len));
                    memcpy(sps, data + 4, sps_len);

                    pps_len = len - (4 + sps_len) - 4;
                    pps = static_cast<int8_t *>(malloc(pps_len));
                    memcpy(pps, data + 4 + sps_len + 4, pps_len);
                    if (IS_DEBUG)
                        ALOGI("sps:%d pps:%d", sps_len, pps_len);
                    break;
                }
            }
        }
    }
}
 RTMPPacket *RtmpLive::createSpsPpsVideoPackage() {
    ALOGD("createVideoPackage sps:%d pps:%d", sps_len, pps_len);
    int body_size = 13 + sps_len + 3 + pps_len;
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, body_size);
    int i = 0;
    //AVC sequence header 与IDR一样
    packet->m_body[i++] = 0x17;
    //AVC sequence header 设置为0x00
    packet->m_body[i++] = 0x00;
    //CompositionTime
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    //AVC sequence header
    packet->m_body[i++] = 0x01;   //configurationVersion 版本号 1
    packet->m_body[i++] = sps[1]; //profile 如baseline、main、 high

    packet->m_body[i++] = sps[2]; //profile_compatibility 兼容性
    packet->m_body[i++] = sps[3]; //profile level
    packet->m_body[i++] = 0xFF; // reserved（111111） + lengthSizeMinusOne（2位 nal 长度） 总是0xff
    //sps
    packet->m_body[i++] = 0xE1; //reserved（111） + lengthSizeMinusOne（5位 sps 个数） 总是0xe1
    //sps length 2字节
    packet->m_body[i++] = (sps_len >> 8) & 0xff; //第0个字节
    packet->m_body[i++] = sps_len & 0xff;        //第1个字节
    memcpy(&packet->m_body[i], sps, sps_len);
    i += sps_len;

    /*pps*/
    packet->m_body[i++] = 0x01; //pps number
    //pps length
    packet->m_body[i++] = (pps_len >> 8) & 0xff;
    packet->m_body[i++] = pps_len & 0xff;
    memcpy(&packet->m_body[i], pps, pps_len);

    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = body_size;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = mRtmp->m_stream_id;
    return packet;
}


RTMPPacket *RtmpLive::createVideoPackage(const int8_t *buf, int len, const long tms) {
//    分隔符被抛弃了      --buf指的是651
    buf += 4;
    len -= 4;
    int body_size = len + 9;
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, len + 9);

    packet->m_body[0] = 0x27;
    if (buf[0] == 0x65) { //关键帧
        packet->m_body[0] = 0x17;
    }
    packet->m_body[1] = 0x01;
    packet->m_body[2] = 0x00;
    packet->m_body[3] = 0x00;
    packet->m_body[4] = 0x00;
    //长度
    packet->m_body[5] = (len >> 24) & 0xff;
    packet->m_body[6] = (len >> 16) & 0xff;
    packet->m_body[7] = (len >> 8) & 0xff;
    packet->m_body[8] = (len) & 0xff;

    //数据
    memcpy(&packet->m_body[9], buf, len);


    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = body_size;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = tms;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = mRtmp->m_stream_id;
    return packet;

}

int RtmpLive::sendVideoData(const int8_t *data, int len, long ms) {
    int ret = -1;
    ALOGD("buf[4]=>%x", data[4]);
    if (data[4] == 0x67) {//sps pps
        if (mRtmp) {
            prepareVideo(data, len);
        }
    } else {
        if (sps_len == 0 || pps_len == 0) return ret;
        if (data[4] == 0x65) {//关键帧 I 帧
            RTMPPacket *packet = createSpsPpsVideoPackage();//发送sps pps
            if (!(ret = sendPacket(packet))) {
            }
        }
        RTMPPacket *packet = createVideoPackage(data, len, ms);
        ret = sendPacket(packet);
    }
    return ret;
}

void RtmpLive::close() {
    if(IS_DEBUG){
        ALOGD("rtmp live close");
    }
    if (mserverUrl) {
        free(mserverUrl);
        mserverUrl = NULL;
    }
    if (mRtmp) {
        RTMP_Close(mRtmp);
        RTMP_Free(mRtmp);

    }
    mRtmp = NULL;
    pps_len = 0;
    sps_len = 0;
    if (sps) {
        free(sps);
        sps = NULL;
    }
    if (pps) {
        free(pps);
        pps = NULL;
    }

}

RtmpLive::~RtmpLive() {

}


