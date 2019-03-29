DESCRIPTION = "SeetaFace Engine is an open source C++ face recognition engine"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7374426a8b9503b840715d4ad12fdb5"

SRCREV = "358713d830aa346b1713fd4544971a8d4558b00f"
SRC_URI = " \
    git://github.com/seetaface/SeetaFaceEngine.git;branch=master;protocol=http \
    file://0000-all-in-one.patch \
"
SRC_URI[md5sum] = "2bd937a1a630765c68b3090574909454"

inherit cmake pkgconfig

S = "${WORKDIR}/git"

do_configure() {
    cmake -H${S}/FaceDetection -B${B}/FaceDetection 
    cmake -H${S}/FaceAlignment -B${B}/FaceAlignment 
    cmake -H${S}/FaceIdentification -B${B}/FaceIdentification 
}

do_compile() {
    cd ${B}/FaceDetection
    make -j

    cd ${B}/FaceAlignment
    make -j

    cd ${B}/FaceIdentification
    make -j
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -d ${D}${includedir}/seeta
    install -d ${D}${datadir}/seeta/FaceDetection/model/
    install -d ${D}${datadir}/seeta/FaceAlignment/model/

    cd ${B}/FaceDetection
    cp *.a ${D}${libdir}
    cp -r ${S}/FaceDetection/include/* ${D}${includedir}/seeta

    cd ${B}/FaceAlignment
    cp *.a ${D}${libdir}
    cp -r ${S}/FaceAlignment/include/* ${D}${includedir}/seeta

    cd ${B}/FaceIdentification
    cp *.a ${D}${libdir}
    cp -r ${S}/FaceIdentification/include/* ${D}${includedir}/seeta

    cp ${S}/FaceDetection/model/seeta_fd_frontal_v1.0.bin ${D}${datadir}/seeta/FaceDetection/model/
    cp ${S}/FaceAlignment/model/seeta_fa_v1.1.bin ${D}${datadir}/seeta/FaceAlignment/model/
}

PACKAGES =+ "${PN}-models"

FILES_${PN}-models = " \
    ${datadir}/seeta/FaceDetection/model/seeta_fd_frontal_v1.0.bin \
    ${datadir}/seeta/FaceAlignment/model/seeta_fa_v1.1.bin \
"
