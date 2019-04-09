DESCRIPTION = "Modern, powerful open source C++ class libraries and frameworks for building network- and internet-based applications that run on desktop, server, mobile and embedded systems."
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=ad296492125bc71530d06234d9bfebe0"

inherit externalsrc

EXTERNALSRC_pn-${PN} = "${WEBRTC_SRC_PATH}"
EXTERNALSRC_BUILD_pn-${PN} = "${WEBRTC_SRC_PATH}"

include gn-utils.inc
include webrtc-unbundle.inc

PACKAGES = "${PN} ${PN}-dev ${PN}-dbg ${PN}-staticdev"

# Append instead of assigning; the gtk-icon-cache class inherited above also
# adds packages to DEPENDS.
DEPENDS += " \
    alsa-lib \
    atk \
    flac \
    glib-2.0 \
    gtk+3 \
    jpeg \
    libdrm \
    libwebp \
    libxml2 \
    libxslt \
    ninja-native \
    pkgconfig-native \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
    virtual/egl \
    virtual/libgl \
"
REQUIRED_DISTRO_FEATURES = "x11 opengl"

DEPENDS += "\
        libx11 \
"

PACKAGECONFIG ??= "use-egl"

# this makes sure the dependencies for the EGL mode are present; otherwise, the configure scripts
# automatically and silently fall back to GLX
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"

# We do not want to use Chromium's own Debian-based sysroots, it is easier to
# just let Chromium's build system assume we are not using a sysroot at all and
# let Yocto handle everything.
GN_ARGS += "use_sysroot=false"

# Upstream Chromium uses clang on Linux, and GCC is not regularly tested. This
# means new GCC releases can introduce build failures as Chromium uses "-Wall
# -Werror" by default and we do not have much control over which warnings GCC
# decides to include in -Wall.
GN_ARGS += "treat_warnings_as_errors=false"
GN_ARGS += "rtc_include_tests=false"
GN_ARGS += "is_chrome_branded=false"

# By default webrtc don't use rtti
GN_ARGS += "use_rtti=true"

# Starting with M61, Chromium defaults to building with its own copy of libc++
# instead of the system's libstdc++. Explicitly disable this behavior.
# https://groups.google.com/a/chromium.org/d/msg/chromium-packagers/8aYO3me2SCE/SZ8pJXhZAwAJ
GN_ARGS += "use_custom_libcxx=false"

# Toolchains we will use for the build. We need to point to the toolchain file
# we've created, set the right target architecture and make sure we are not
# using Chromium's toolchain (bundled clang, bundled binutils etc).
GN_ARGS += ' \
        custom_toolchain="//build/toolchain/yocto:yocto_target" \
        host_toolchain="//build/toolchain/yocto:yocto_native" \
        is_clang=${@is_default_cc_clang(d)} \
        clang_base_path="${@clang_install_path(d)}" \
        linux_use_bundled_binutils=false \
        target_cpu="${@gn_target_arch_name(d)}" \
        rtc_enable_protobuf=false \
        is_component_build=false \
        enable_iterator_debugging=false \
'

GN_ARGS_append_x86-64 = ' \
        rtc_use_h264=true \
        is_debug=false \
'

# ARM builds need special additional flags (see ${S}/build/config/arm.gni).
# If we do not pass |arm_arch| and friends to GN, it will deduce a value that
# will then conflict with TUNE_CCARGS and CC.
# Note that as of M61 in some corner cases parts of the build system disable
# the "compiler_arm_fpu" GN config, whereas -mfpu is always passed via ${CC}.
# We might want to rework that if there are issues in the future.
def get_arm_version(arm_arch):
    import re
    try:
        return re.match(r'armv(\d).*', arm_arch).group(1)
    except IndexError:
        bb.fatal('Unrecognized ARM architecture value: %s' % arm_arch)
def get_compiler_flag(params, param_name, d):
    """Given a sequence of compiler arguments in |params|, returns the value of
    an option |param_name| or an empty string if the option is not present."""
    for param in params:
      if param.startswith(param_name):
        return param.split('=')[1]
    return ''

ARM_ARCH = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-march', d)}"
ARM_FLOAT_ABI = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'hard', 'softfp', d)}"
ARM_FPU = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mfpu', d)}"
ARM_TUNE = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mcpu', d)}"
ARM_VERSION = "${@get_arm_version(d.getVar('ARM_ARCH'))}"
GN_ARGS_append_arm = ' \
        arm_arch="${ARM_ARCH}" \
        arm_float_abi="${ARM_FLOAT_ABI}" \
        arm_fpu="${ARM_FPU}" \
        arm_tune="${ARM_TUNE}" \
        arm_version=${ARM_VERSION} \
'

# tcmalloc's atomicops-internals-arm-v6plus.h uses the "dmb" instruction that
# is not available on (some?) ARMv6 models, which causes the build to fail.
GN_ARGS_append_armv6 += 'use_allocator="none"'
# The WebRTC code fails to build on ARMv6 when NEON is enabled.
# https://bugs.chromium.org/p/webrtc/issues/detail?id=6574
GN_ARGS_append_armv6 += 'arm_use_neon=false'

export BRANCH_72="(HEAD detached at branch-heads/72)"

do_check_webrtc_branch() {
    PREV_DIR=$(pwd)
    cd ${WEBRTC_SRC_PATH}
    cd ..
    BRANCH="$(git branch | sed -n -e 's/^\* \(.*\)/\1/p')"

    if [ "$BRANCH" != "$BRANCH_72" ]
    then
        bbfatal "Please checkout to ${BRANCH}"
    fi

    cd ${PREV_DIR}
}

python do_write_toolchain_file () {
    """Writes a BUILD.gn file for Yocto detailing its toolchains."""
    toolchain_dir = d.expand("${EXTERNALSRC}/../build/toolchain/yocto")
    bb.utils.mkdirhier(toolchain_dir)
    toolchain_file = os.path.join(toolchain_dir, "BUILD.gn")
    write_toolchain_file(d, toolchain_file)
}

addtask do_write_toolchain_file before do_compile
addtask do_check_webrtc_branch before do_configure

do_compile( ) {
    export PATH=${DEPOT_TOOLS_PATH}:$PATH 
    cd ..

    if [ -d "third_party/flac" ]; then
        ./build/linux/unbundle/replace_gn_files.py --system-libraries ${GN_UNBUNDLE_LIBS}
    fi

    gn gen --args='${GN_ARGS}' "${EXTERNALSRC_BUILD}"
    ninja -C "${EXTERNALSRC_BUILD}"
}

do_install( ) {
    install -d ${D}${libdir}/webrtc
    install -m 0644 obj/libwebrtc.a ${D}${libdir}/webrtc

    install -d ${D}${libdir}/webrtc/api/audio_codecs
    cp -r obj/api/audio_codecs/* ${D}${libdir}/webrtc/api/audio_codecs/
}

do_package_qa[noexec] = "1"


