DESCRIPTION = "Tool to transfer the rootfs of an already running image from SD card eMMC"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SRC_URI = " \
    file://flash_sd2emmc.sh \
    file://exclude.txt \
"

RDEPENDS_${PN} = "e2fsprogs-mke2fs dosfstools rsync"

do_install () {
        install -d ${D}/${datadir}/sd2emmc
        install -m 0755 ${WORKDIR}/flash_sd2emmc.sh ${D}/${datadir}/sd2emmc
        install -m 0755 ${WORKDIR}/exclude.txt ${D}/${datadir}/sd2emmc
}

FILES_${PN} = " \
    ${datadir}/sd2emmc/flash_sd2emmc.sh \
    ${datadir}/sd2emmc/exclude.txt \
"
