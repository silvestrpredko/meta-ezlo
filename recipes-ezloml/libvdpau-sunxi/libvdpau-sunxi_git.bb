DESCRIPTION = "libvdpau-sunxi is a [VDPAU] backend driver for Allwinner based (sunxi) SoC"
LICENSE = "MIT"

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/linux-sunxi/libvdpau-sunxi.git;branch=master;protocol=http"
SRC_URI[md5sum] = "fcc5bb28449634fc36ab5e3e7942037f"
LIC_FILES_CHKSUM = "file://README.md;md5=441836d5bd448b57321a6ca1407bee17"
S = "${WORKDIR}/git"

DEPENDS = "libvdpau libcedrus pixman libx11"

CFLAGS_prepend = "-I/usr/include/pixman-1/"

inherit pkgconfig

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install 'DESTDIR=${D}'
}

FILES_${PN} += "${libdir}/vdpau/*.so*" 
FILES_${PN}-dev += "${libdir}/vdpau/*.so*" 
