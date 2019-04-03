DESCRIPTION = "libvdpau-sunxi is a [VDPAU] backend driver for Allwinner based (sunxi) SoC"
LICENSE = "MIT"

SRCREV = "${AUTOREV}"
SRC_URI = " \
            git://github.com/linux-sunxi/libvdpau-sunxi.git;branch=master;protocol=http \
            file://0001-enable-debug-print-via-define.patch \
"
SRC_URI[md5sum] = "fcc5bb28449634fc36ab5e3e7942037f"
LIC_FILES_CHKSUM = "file://README.md;md5=441836d5bd448b57321a6ca1407bee17"
S = "${WORKDIR}/git"

DEPENDS = "libvdpau libcedrus pixman libx11"

inherit pkgconfig

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${B}/libvdpau_sunxi.so.1 ${D}${libdir}/
    ln -sf libvdpau_sunxi.so.1 ${D}${libdir}/libvdpau_sunxi.so
}

