SUMMARY = "A base x11 ezloml image with some C/C++ dev tools "

IMAGE_FEATURES += "package-management x11-base"
IMAGE_LINGUAS = "en-us"

inherit image
require image-ezloml.inc

IMAGE_INSTALL += " \
    ${CORE_OS} \
    ${EZLOML_STUFF} \
    ${DEV_SDK_INSTALL} \
    ${EXTRA_TOOLS_INSTALL} \
    ${WIFI_SUPPORT} \
"

export IMAGE_BASENAME = "image-ezloml-dev"
