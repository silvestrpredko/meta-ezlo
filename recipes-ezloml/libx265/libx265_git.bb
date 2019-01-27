DESCRIPTION = "x265 codec"
LICENSE = "MIT"

inherit cmake pkgconfig

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/videolan/x265;branch=master;protocol=http"
SRC_URI[md5sum] = "935e2f8e773ec1d1a1b68f0a01da096d"
LIC_FILES_CHKSUM = "file://CMakeLists.txt;md5=b610123ddf960a2b7428a85017da5e50"
S = "${WORKDIR}/git/source"

EXTRA_OECONF += " -DENABLE_SHARED=OFF -DSTATIC_LINK_CRT=ON"
TARGET_CFLAGS += "-fPIC"
TARGET_CXXFLAGS += "-fPIC"

