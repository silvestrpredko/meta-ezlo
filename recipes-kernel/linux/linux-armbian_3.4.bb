DESCRIPTION = "Linux kernel for sun8i processors"
SECTION = "kernel"
LICENSE = "GPLv2"

INC_PR = "r0"

inherit kernel kernel-yocto siteinfo

# Enable OABI compat for people stuck with obsolete userspace
ARM_KEEP_OABI ?= "0"

COMPATIBLE_MACHINE = "(sun8i)"
DEPENDS += "sunxi-board-fex-custom"

PV = "3.4.113"
PR = "r1"
SRCREV_pn-${PN} = "${AUTOREV}"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

KBRANCH = "sun8i"
MACHINE_KERNEL_PR_append = "a"
LOCALVERSION ?= ""
KCONFIG_MODE ?= "alldefconfig"
KMACHINE ?= "${MACHINE}"

SRC_URI += " \
    git://github.com/armbian/linux.git;branch=${KBRANCH} \
    file://0001-apply-all-armbian-patches.patch \
    file://0002-apply-all-gcc8-patches.patch \
    file://0003-kmodules-with-no-pic-flag.patch \
    file://defconfig \
"

S = "${WORKDIR}/git"

do_kernel_configme_append() {
    #brutal hack to force applying our configs without canonical check
    cp ${WORKDIR}/defconfig ${B}/.config
}

do_compile_kernelmodules_prepend() {
    #copy bsp banaries to build dir
    cp ${S}/drivers/media/video/sunxi-vfe/lib/libisp ${B}/drivers/media/video/sunxi-vfe/lib/
    cp ${S}/drivers/media/video/sunxi-vfe/lib/lib_mipicsi2_v* ${B}/drivers/media/video/sunxi-vfe/lib/

    cp ${S}/drivers/input/touchscreen/aw5x06/libAW5306 ${B}/drivers/input/touchscreen/aw5x06/
    cp ${S}/drivers/input/touchscreen/gslx680new/gsl_point_id_20131111 ${B}/drivers/input/touchscreen/gslx680new/
}

do_install_append() {
    oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}/src/linux-${KERNEL_VERSION} ARCH=$ARCH
}

PACKAGES =+ "kernel-headers"
FILES_kernel-headers = "${exec_prefix}/src/linux*"

#KERNEL_MODULE_AUTOLOAD += "mali_drm drm pcf8591 bmp085 mali ump 8189fs btrfs"
