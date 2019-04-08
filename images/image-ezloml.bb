SUMMARY = "A minimal ezloml image"

IMAGE_FEATURES += "package-management x11-base"
IMAGE_LINGUAS = "en-us"

inherit core-image distro_features_check
require image-ezloml.inc

IMAGE_INSTALL_append = " \
    ${CORE_OS} \
    ${EZLOML_STUFF} \
"

export IMAGE_BASENAME = "image-ezloml"

REQUIRED_DISTRO_FEATURES = "x11"
