DESCRIPTION = "Ezlo ML"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://readme.txt;md5=341d1b99129987168a9b63216fcbefd5"
# While this item does not require it, it depends on ffmpeg which does
LICENSE_FLAGS += "commercial"


inherit pkgconfig cmake 
inherit externalsrc 

EXTERNALSRC_pn-${PN} = "${HOME}/dev/ezloml_dev/ezloml"
EXTERNALSRC_BUILD_pn-${PN} = "${HOME}/dev/ezloml_dev/ezloml/build"

EXTRA_OECMAKE += "-DPC_BUILD=OFF -DWEBRTC_SRC_PATH=${WEBRTC_SRC_PATH}"

DEPENDS += " \
    boost \
    webrtc \
    spdlog \
    openssl \
    pusher-cpp \
    cppcodec \
    rpclib \
    ffmpeg \
    opencv \
    rapidjson \
    websocketpp \
    seeta-face-engine \
    wiringpi \
    gperftools \
    sunxi-mali \
    ffmpeg \
    openblas \
    cmake-native \
    ninja-native \
"
#lapack

do_install() {
}

