DESCRIPTION = "libcedrus provides low-level access to the video engine of Allwinner sunxi SoCs"
LICENSE = "MIT"
SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/linux-sunxi/libcedrus.git;branch=master;protocol=http"
SRC_URI[md5sum] = "1a2f7045be009a04d8263c40ba2fd2e0"
LIC_FILES_CHKSUM = "file://README;md5=739dbaec4a089ef038f7c86f4602a0ea"
S = "${WORKDIR}/git"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install 'DESTDIR=${D}'
}
