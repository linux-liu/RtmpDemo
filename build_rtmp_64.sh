#!/bin/bash
# 将NDK的路径替换成你自己的NDK路径
NDK_ROOT=/home/liuxin/Android/Sdk/ndk/16.1.4479499

# 设置工具链mac苹果电脑的是darwin-x86_64 而如果是linux的话则是linux-x86_64

TOOLCHAIN=$NDK_ROOT/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64

CPU=arm-linux-androideabi

export XCFLAGS="-isysroot $NDK_ROOT/sysroot -isystem $NDK_ROOT/sysroot/usr/include/aarch64-linux-android -D__ANDROID_API__=21 -g -DANDROID -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -march=armv8-a -Wa,--noexecstack -Wformat -Werror=format-security  -O0 -fPIC"
export XLDFLAGS="--sysroot=${NDK_ROOT}/platforms/android-21/arch-arm64 "
export CROSS_COMPILE=$TOOLCHAIN/bin/aarch64-linux-android-

# XDEF=-DNO_SSL 的意思是不实用SSL，因为rtmp内部使用了SSL，如果开启的话需要编译链接SSL的库
make install SYS=android prefix=`pwd`/install/Arm64 CRYPTO= SHARED=  XDEF=-DNO_SSL

