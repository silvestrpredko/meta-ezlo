DESCRIPTION = "GL4ES - OpenGL for GLES Hardware"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b7b867e0242b12e3ca931020e1a08345"

SRCREV = "dd4da0a6992fdb9a7397c2dabc8c8587a8af7151"
SRC_URI = " \
    git://github.com/ptitSeb/gl4es.git;branch=master;protocol=http \
"
SRC_URI[md5sum] = "a322f005f9a045f644186d4cd504691c"

inherit cmake pkgconfig

#EXTRA_OECMAKE += "-DODROID=1"

S = "${WORKDIR}/git"

DEPENDS_append = " libx11"

do_install() {
    install -d ${D}${libdir} 

    install -m 0644 ${B}/lib/libGL.so.1 ${D}${libdir}/libGL2ES.so
    chrpath -d ${D}${libdir}/libGL2ES.so
}
