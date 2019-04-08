DESCRIPTION = "udev rules for Freescale i.MX SOCs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://40-i2c-tools.rules \
    file://60-libcedrus1.rules \
    file://60-libmali-sunxi-r3p0.rules \
    file://60-libump.rules \
"

S = "${WORKDIR}"

do_install () {
	install -d ${D}${sysconfdir}/udev/rules.d
	install -m 0644 ${WORKDIR}/40-i2c-tools.rules ${D}${sysconfdir}/udev/rules.d/
	install -m 0644 ${WORKDIR}/60-libcedrus1.rules ${D}${sysconfdir}/udev/rules.d/
	install -m 0644 ${WORKDIR}/60-libmali-sunxi-r3p0.rules ${D}${sysconfdir}/udev/rules.d/
	install -m 0644 ${WORKDIR}/60-libump.rules ${D}${sysconfdir}/udev/rules.d/
}
