DESCRIPTION = "Header only codec cpp library"
LICENSE = "MIT"

inherit cmake pkgconfig

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/tplgy/cppcodec.git;branch=master;protocol=http"
SRC_URI[md5sum] = "e29b16e7463ae12b995fa9da84057e98"
S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d0456e7564890325951234b07c0cf764"

BBCLASSEXTEND = "native"
EXTRA_OECMAKE += "-DBUILD_TESTING=off"


