DESCRIPTION = "U-Boot port for sunxi"

require recipes-bsp/u-boot/u-boot.inc

DEPENDS += " bc-native dtc-native swig-native python3-native flex-native bison-native "

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# No patches for other machines yet
COMPATIBLE_MACHINE = "(sun4i|sun5i|sun7i|sun8i)"

DEFAULT_PREFERENCE_sun8i="1"

KERNEL_DEVICETREE = "sun8i-h3-orangepi-plus2e.dtb"
UTAG = "v2018.05"
SRC_URI = " \
        git://git.denx.de/u-boot.git;tag=${UTAG} \
        file://0001-yocto-apply-all-patches.patch \
        file://boot_sun8i.cmd \
        "

PV = "v2018.05+git${SRCPV}"
PE = "1"

S = "${WORKDIR}/git"

UBOOT_ENV_SUFFIX = "scr"
UBOOT_ENV = "boot"

#addtask do_configure_armbian before do_compile after do_configure

#do_configure_armbian() {
#    sed -i 's/CONFIG_LOCALVERSION=""/CONFIG_LOCALVERSION="-armbian"/g' ${B}/.config
#    sed -i 's/CONFIG_LOCALVERSION_AUTO=.*/# CONFIG_LOCALVERSION_AUTO is not set/g' ${B}/.config
#    sed -i 's/^.*CONFIG_ENV_IS_IN_FAT.*/# CONFIG_ENV_IS_IN_FAT is not set/g' ${B}/.config
#    sed -i 's/^.*CONFIG_ENV_IS_IN_EXT4.*/CONFIG_ENV_IS_IN_EXT4=y/g' ${B}/.config
#    sed -i 's/^.*CONFIG_ENV_IS_IN_MMC.*/# CONFIG_ENV_IS_IN_MMC is not set/g' ${B}/.config
#    sed -i 's/^.*CONFIG_ENV_IS_NOWHERE.*/# CONFIG_ENV_IS_NOWHERE is not set/g' ${B}/.config
#    
#    echo "# CONFIG_ENV_IS_NOWHERE is not set" >> ${B}/.config
#    echo 'CONFIG_ENV_EXT4_INTERFACE="mmc"' >> ${B}/.config
#    echo 'CONFIG_ENV_EXT4_DEVICE_AND_PART="0:auto"' >> ${B}/.config
#    echo 'CONFIG_ENV_EXT4_FILE="/boot/boot.env"' >> ${B}/.config
#}

do_compile_append() {
    ${B}/tools/mkimage -C none -A arm -T script -d ${WORKDIR}/boot_sun8i.cmd ${WORKDIR}/${UBOOT_ENV_BINARY}
}

RM_WORK_EXCLUDE += "${PN}"
