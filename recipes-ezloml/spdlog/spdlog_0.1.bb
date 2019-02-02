DESCRIPTION = "Header only codec cpp library"
LICENSE = "MIT"

inherit cmake pkgconfig

SRCREV = "a7148b718ea2fabb8387cb90aee9bf448da63e65"
SRC_URI = "git://github.com/gabime/spdlog.git;branch=v1.x;protocol=http"
SRC_URI[md5sum] = "3b624ab7e53db2b4322f0c0098928840"
S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6e5242b8f24d08c5e948675102937cc9"

BBCLASSEXTEND = "native"
EXTRA_OECMAKE += "-DSPDLOG_BUILD_BENCH=off -DSPDLOG_BUILD_TESTS=off -DSPDLOG_BUILD_EXAMPLES=off"

FILES_${PN} += "${libdir}/cmake/spdlog/*.cmake"
