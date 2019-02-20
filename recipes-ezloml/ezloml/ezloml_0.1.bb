DESCRIPTION = "Ezlo ML"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://../readme.txt;md5=341d1b99129987168a9b63216fcbefd5"
# While this item does not require it, it depends on ffmpeg which does
LICENSE_FLAGS += "commercial"


inherit pkgconfig cmake 
inherit externalsrc 

EXTERNALSRC_pn-${PN} = "${EZLOML_SRC_PATH}"
EXTERNALSRC_BUILD_pn-${PN} = "${EZLOML_SRC_PATH}"

def real_path( d ):
    path = d.getVar('WEBRTC_SRC_PATH')
    return os.path.split(path)[0]

WEBRTC_REAL_PATH = "${@real_path(d)}"
EXTRA_OECMAKE += "-DWEBRTC_SRC_PATH=${WEBRTC_REAL_PATH}"

DEPENDS += " \
    boost \
    webrtc \
    spdlog \
    boringssl \
    curl \
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
    openblas \
    virtual/egl \
    virtual/libgles2 \
    libvdpau-sunxi \
"

EXTRA_OECMAKE_append = " .."  

do_configure_append() {
    rm -f ${STAGING_DIR_TARGET}/usr/include/openssl/*
    cp ${STAGING_DIR_TARGET}/usr/lib/boringssl/include/openssl/* ${STAGING_DIR_TARGET}/usr/include/openssl/
}

do_install() {
    cp ${STAGING_DIR_TARGET}/usr/lib/vdpau/libvdpau_sunxi.so ${EXTERNALSRC} 
}

