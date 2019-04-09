DESCRIPTION = "Nodejs onvif packages"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SRC_URI = " \
    file://cc.js \
    file://find.js \
"

RDEPENDS_${PN} = "node-colors node-onvif node-reconnecting-websocket node-request node-ws node-xml2js"

do_install () {
        install -d ${D}/opt/node-camera-search
        install -m 0755 ${WORKDIR}/cc.js ${D}/opt/node-camera-search
        install -m 0755 ${WORKDIR}/find.js ${D}/opt/node-camera-search
}

FILES_${PN} = " \
    /opt/node-camera-search/*.js \
"
