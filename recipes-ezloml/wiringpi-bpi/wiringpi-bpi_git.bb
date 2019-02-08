DESCRIPTION = "A library to control Raspberry Pi GPIO channels"
HOMEPAGE = "https://projects.drogon.net/raspberry-pi/wiringpi/"
SECTION = "devel/libs"
LICENSE = "LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

#DEPENDS += "virtual/crypt"

S = "${WORKDIR}/git"

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/BPI-SINOVOIP/BPI-WiringPi2.git;branch=master;protocol=http \
           "
#file://0001-Add-initial-cross-compile-support.patch

CFLAGS_prepend = "-I${S}/wiringPi -I${S}/devLib "

EXTRA_OEMAKE += "'INCLUDE_DIR=${D}${includedir}' 'LIB_DIR=${D}${libdir}'"
EXTRA_OEMAKE += "'DESTDIR=${D}/usr' 'PREFIX=""'"

do_compile() {
    oe_runmake -C wiringPi static
}

do_install() {
    oe_runmake -C wiringPi install-static
}
