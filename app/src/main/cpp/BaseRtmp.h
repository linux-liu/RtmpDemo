//
// Created by liuxin on 21-7-15.
//

#ifndef RTMPDEMO_BASERTMP_H
#define RTMPDEMO_BASERTMP_H

extern "C"{
#include "rtmp.h"
};
#include <cstring>
#include <cstdlib>
#include "mylog.h"
#include "constant.h"

class BaseRtmp {
protected:
    char *mserverUrl = NULL;
    RTMP *mRtmp = NULL;

public:
    BaseRtmp();
    int connect(const char *serverUrl);
    virtual void close()=0;
    virtual ~BaseRtmp();
};


#endif //RTMPDEMO_BASERTMP_H
