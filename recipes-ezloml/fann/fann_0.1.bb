DESCRIPTION = "Header only codec cpp library"
LICENSE = "MIT"
inherit cmake pkgconfig
SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/libfann/fann;branch=master;protocol=http"
SRC_URI[md5sum] = "2c88f84a28956c81733a95e30d92c391"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f14599a2f089f6ff8c97e2baa4e3d575"
S = "${WORKDIR}/git"
BBCLASSEXTEND = "native"

