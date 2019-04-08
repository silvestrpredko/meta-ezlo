SUMMARY = " ezloml image with some C/C++ dev tools "

IMAGE_FEATURES += "package-management x11-base"
IMAGE_LINGUAS = "en-us"

inherit core-image distro_features_check
require image-ezloml.inc

IMAGE_INSTALL += " \
    ${CORE_OS} \
    ${EZLOML_STUFF} \
    ${DEV_SDK_INSTALL} \
    ${EXTRA_TOOLS_INSTALL} \
    ${WIFI_SUPPORT} \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    packagegroup-core-full-cmdline \
"

export IMAGE_BASENAME = "image-ezloml-dev"

REQUIRED_DISTRO_FEATURES = "x11"
