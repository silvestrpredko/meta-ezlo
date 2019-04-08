DESCRIPTION = "Setup CPU for maximum performance"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit update-rc.d

INITSCRIPT_NAME = "cpufrequtils-init"
INITSCRIPT_PARAMS = "start 93 S ."
INITSCRIPT_CONF = "cpufreq_config"

SRC_URI = " \
		file://${INITSCRIPT_NAME} \
		file://${INITSCRIPT_CONF} \
"

RDEPENDS_${PN} = "cpufrequtils"

do_install () {
    install -d ${D}${INIT_D_DIR}
    install -m 0755 ${WORKDIR}/${INITSCRIPT_NAME} ${D}/${INIT_D_DIR}/${INITSCRIPT_NAME}

    install -d ${D}/etc/default
    install -m 0755 ${WORKDIR}/${INITSCRIPT_CONF} ${D}/etc/default
}

FILES_${PN} += " \
    ${INIT_D_DIR}/${INITSCRIPT_NAME} \
    /etc/default/${INITSCRIPT_NAME} \
"
