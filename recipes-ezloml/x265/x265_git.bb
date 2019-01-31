DESCRIPTION = "x265 codec"
LICENSE = "MIT"

inherit cmake lib_package

SRC_URI = "git://github.com/videolan/x265;tag=1.9;protocol=http"
SRC_URI[md5sum] = "935e2f8e773ec1d1a1b68f0a01da096d"
LIC_FILES_CHKSUM = "file://CMakeLists.txt;md5=30e9a75061fc38f2f75ce0de38ce1709"
S = "${WORKDIR}/git/source"

EXTRA_OECONF += " -DSTATIC_LINK_CRT:BOOL=ON -DENABLE_CLI:BOOL=OFF -DCROSS_COMPILE_ARM=1"
EXTRA_OECONF_arm = "-DCROSS_COMPILE_ARM=1"
EXTRA_OECONF_aarch64 = "-DCROSS_COMPILE_ARM=1"

TARGET_CFLAGS += "-fPIC"
TARGET_CXXFLAGS += "-fPIC"

