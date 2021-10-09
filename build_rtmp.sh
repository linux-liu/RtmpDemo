#!/bin/bash
# 将NDK的路径替换成你自己的NDK路径
NDK_ROOT=/home/liuxin/Android/Sdk/ndk/16.1.4479499

# 设置工具链mac苹果电脑的是darwin-x86_64 而如果是linux的话则是linux-x86_64

TOOLCHAIN=$NDK_ROOT/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64

CPU=arm-linux-androideabi

export XCFLAGS="-isysroot $NDK_ROOT/sysroot -isystem $NDK_ROOT/sysroot/usr/include/arm-linux-androideabi -D__ANDROID_API__=21 -g -DANDROID -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -mthumb -Wa,--noexecstack -Wformat -Werror=format-security  -O0 -fPIC"
export XLDFLAGS="-L/home/liuxin/ffmpeg/rtmp/lib/sysroot/lib --sysroot=${NDK_ROOT}/platforms/android-21/arch-arm"
export INC="-I/home/liuxin/ffmpeg/rtmp/lib/sysroot/include" 
export CROSS_COMPILE=$TOOLCHAIN/bin/arm-linux-androideabi-

# XDEF=-DNO_SSL 的意思是不实用SSL，因为rtmp内部使用了SSL，如果开启的话需要编译链接SSL的库
make install SYS=android prefix=`pwd`/install/Armv7 CRYPTO=POLARSSL CRYPTO= SHARED=  XDEF=-DNO_SSL
