DESCRIPTION = "Resize Rootfs systemd service"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SRC_URI = "file://resize-rootfs"

#inherit update-rc.d
#
#INITSCRIPT_NAME = "resize-rootfs"
#INITSCRIPT_PARAMS = "start 90 S ."

RDEPENDS_${PN} = "e2fsprogs-resize2fs parted"

do_install () {
        install -d ${D}${INIT_D_DIR}
        install -m 0755 ${WORKDIR}/${INITSCRIPT_NAME} ${D}/${INIT_D_DIR}/${INITSCRIPT_NAME}
}

FILES_${PN} = "${INIT_D_DIR}/*"
