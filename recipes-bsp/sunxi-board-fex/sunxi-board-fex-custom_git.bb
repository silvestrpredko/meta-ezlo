# Copyright (C) 2013 Tomas Novotny <novotny@rehivetech.com>
# Released under BSD-2-Clause or MIT license
DESCRIPTION = "Handler for Allwinner's FEX files"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=0ceb3372c9595f0a8067e55da801e4a1"
DEPENDS = "sunxi-tools-native"
PV = "1.1+git${SRCPV}"
PR = "r0"

COMPATIBLE_MACHINE = "(sun4i|sun5i|sun7i|sun8i)"

SRC_URI = " \
        git://github.com/linux-sunxi/sunxi-boards.git;protocol=git \
        file://${SUNXI_FEX_FILE} \
"

# Increase PV with SRCREV change
SRCREV = "496ef0fbd166cc2395daa76dd3c359357420963d"

S = "${WORKDIR}/git"

SUNXI_FEX_BIN_IMAGE = "fex-${MACHINE}-${PV}-${PR}.bin"
SUNXI_FEX_BIN_IMAGE_SYMLINK = "fex-${MACHINE}.bin"
SUNXI_FEX_BIN_IMAGE_SYMLINK_SIMPLE = "fex.bin"

inherit deploy

do_compile() {
    fex2bin "${WORKDIR}/${SUNXI_FEX_FILE}" > "${B}/${SUNXI_FEX_BIN_IMAGE}"
}

do_deploy() {
    install -m 0644 ${B}/${SUNXI_FEX_BIN_IMAGE} ${DEPLOYDIR}/
    cd ${DEPLOYDIR}
    ln -sf ${SUNXI_FEX_BIN_IMAGE} ${SUNXI_FEX_BIN_IMAGE_SYMLINK}
    ln -sf ${SUNXI_FEX_BIN_IMAGE} ${SUNXI_FEX_BIN_IMAGE_SYMLINK_SIMPLE}
}
addtask deploy before do_build after do_compile

PACKAGES = ""

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install[continue] = "1"
do_package[continue] = "1"
do_packagedata[continue] = "1"
do_package_write[continue] = "1"
do_package_write_ipk[continue] = "1"
do_package_write_rpm[continue] = "1"
do_package_write_deb[continue] = "1"
do_populate_sysroot[continue] = "1"
