SUMMARY = "A base console ezloml image"

inherit core-image distro_features_check
require image-ezloml.inc

IMAGE_INSTALL_append = " \
    ${CORE_OS} \
    ${EZLOML_STUFF} \
    ${DEV_SDK_INSTALL} \
    zram \
    e2fsprogs-mke2fs \
    dosfstools \
"

export IMAGE_BASENAME = "image-ezloml"

REQUIRED_DISTRO_FEATURES = "x11"
