DESCRIPTION = "Apply ksz9897 fix on boot"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SRC_URI = " \
    file://ksz9897-init.sh \
    file://ksz9897r-rev.A1-fixup.py \
    "

inherit update-rc.d

INITSCRIPT_NAME = "ksz9897-init.sh"
INITSCRIPT_PARAMS = "start 98 S ."

RDEPENDS_${PN} = "python3-core i2c-tools"

do_install () {
        install -d ${D}${INIT_D_DIR}
        install -m 0755 ${WORKDIR}/${INITSCRIPT_NAME} ${D}/${INIT_D_DIR}/${INITSCRIPT_NAME}

        install -d ${D}/etc/ksz9787_revA1_fixup
        install -m 0755 ${WORKDIR}/ksz9897r-rev.A1-fixup.py ${D}/etc/ksz9787_revA1_fixup
}

FILES_${PN} = " \
    ${INIT_D_DIR}/${INITSCRIPT_NAME} \
    /etc/ksz9787_revA1_fixup/ksz9897r-rev.A1-fixup.py \
"
