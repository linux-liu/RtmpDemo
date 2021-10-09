/**
Created by liuxin on 21-7-11.

 */
#include <jni.h>
#include <cassert>
#include <cstring>
#include <cstdlib>
#include "mylog.h"
#include <atomic>
#include "RtmpLive.h"
#include "RtmpFile.h"

#define RTMP_LIVE 0
#define RTMP_FILE 1

static RtmpLive *mLive = NULL;
static RtmpFile *mFile = NULL;

using  namespace std;


static jint connect(JNIEnv *env, jobject instance, jstring serverUrl, jint type);

static jint startFilePush(JNIEnv *env, jobject instance, jstring filePath);

static jint startDataPush(JNIEnv *env, jobject instance, jbyteArray yuv,
                          jint length, jlong pts, jint type);

static void close1(JNIEnv *env, jobject instance, jint type);

/**
* 方法对应表
*/
static JNINativeMethod gMethods[] = {
        {"connect",       "(Ljava/lang/String;I)I", (void *) connect},
        {"startFilePush", "(Ljava/lang/String;)I",  (void *) startFilePush},
        {"startDataPush", "([BIJI)I",               (void *) startDataPush},
        {"close",         "(I)V",                   (void *) close1}

};


/*
* 为某一个类注册本地方法
*/
static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNative(JNIEnv *env) {
    if (IS_DEBUG)
        ALOGD("=====registerNative=====");
    const char *kClassName = "com/yunovo/rtmppush/RtmpPush";
    return registerNativeMethods(env, kClassName, gMethods,
                                 sizeof(gMethods) / sizeof(gMethods[0]));
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *javaVM, void *reserved) {
    JNIEnv *env = NULL;
    if (javaVM->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    if (!registerNative(env)) {
        return -1;
    }

    return JNI_VERSION_1_6;
}


static jint connect(JNIEnv *env, jobject instance, jstring serverUrl, jint type) {
    int ret = RTMP_SUCCESS;
    const char *url = env->GetStringUTFChars(serverUrl, 0);
    if (type == RTMP_LIVE) {
        if (mLive) {
            ret = RTMP_AlREADY_INITED_ERROR;
            goto end;
        }
        mLive = new RtmpLive();
        ret = mLive->connect(url);

    } else if (type == RTMP_FILE) {
        if (mFile) {
            ret = RTMP_AlREADY_INITED_ERROR;
            goto end;
        }
        mFile = new RtmpFile();
        ret = mFile->connect(url);
    }



    end:
    env->ReleaseStringUTFChars(serverUrl, url);

    return ret;
}

static jint startFilePush(JNIEnv *env, jobject instance, jstring filePath) {
    int ret = RTMP_SUCCESS;
    const char *f = env->GetStringUTFChars(filePath, 0);
    if (mFile) {
        ret = mFile->filePush(f);
    }
    env->ReleaseStringUTFChars(filePath, f);
    return ret;

}


static jint startDataPush(JNIEnv *env, jobject instance, jbyteArray data_,
                          jint len, jlong tms, jint type) {
    int ret = RTMP_SUCCESS;
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    switch (type) {
        case 0: //video
            if (IS_DEBUG)
                ALOGD("send video  lenght :%d", len);
            if (mLive) {
                mLive->sendVideoData(data, len, tms);
            }
            break;
        default: //audio
            if (IS_DEBUG)
                ALOGD("send Audio  lenght :%d", len);
            if (mLive) {
                mLive->sendAudioData(data, len, tms, type);
            }
            break;
    }
    env->ReleaseByteArrayElements(data_, data, 0);
    return ret;

}


static void close1(JNIEnv *env, jobject instance, jint type) {
    if (type == RTMP_LIVE) {
        if (mLive) {
            mLive->close();
            delete mLive;
            mLive = NULL;
        }
    } else if (type == RTMP_FILE) {
        if (mFile) {
            mFile->close();
            delete mFile;
            mFile = NULL;
        }
    }


}



