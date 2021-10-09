//
// Created by liuxin on 21-7-15.
//


#include "BaseRtmp.h"


int BaseRtmp::connect(const char *serverUrl) {
    int ret = RTMP_SUCCESS;
    size_t len = strlen(serverUrl);
    if (mserverUrl != NULL) {
        free(mserverUrl);
    }
    mserverUrl = static_cast<char *>(malloc(len + 1));
    memset(mserverUrl, 0, len + 1);
    memcpy(mserverUrl, serverUrl, len);

    if (IS_DEBUG) {
        ALOGD("serverUrl==>%s", mserverUrl);
    }
    if (mRtmp != NULL) {
        if (IS_DEBUG) {
            ALOGD("has inited");
        }
        return (ret = RTMP_AlREADY_INITED_ERROR);
    }

    mRtmp = RTMP_Alloc();
    RTMP_Init(mRtmp);
    mRtmp->Link.timeout = 5;
    if (!RTMP_SetupURL(mRtmp, mserverUrl)) {
        if (IS_DEBUG) {
            ALOGE("set server Url failed");
        }
        RTMP_Free(mRtmp);
        mRtmp = NULL;
        return (ret = RTMP_SET_URL_ERROR);

    }
    RTMP_EnableWrite(mRtmp);
    if (!RTMP_Connect(mRtmp, NULL)) {
        if (IS_DEBUG) {
            ALOGE("rtmp connect error");
        }
        RTMP_Free(mRtmp);
        mRtmp = NULL;
        return (ret = RTMP_CONNECT_ERROR);

    }

    if (!RTMP_ConnectStream(mRtmp, 0)) {
        if (IS_DEBUG) {
            ALOGE("rtmp connectStream error");
        }
        RTMP_Free(mRtmp);
        mRtmp = NULL;
        return (ret = RTMP_CONNECT_STREAM_ERROR);


    }
    return ret;

}

BaseRtmp::BaseRtmp() {

}

BaseRtmp::~BaseRtmp() {

}


