DESCRIPTION = "RPC library"
LICENSE = "MIT"
inherit cmake

DEPENDS = "perl-native libunwind-native golang-native"

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/google/boringssl.git;branch=master;protocol=http"
SRC_URI[md5sum] = "cec140cf0160dee3c08dcbc6a62c0deb"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9b082148f9953258347788edb83e401b"

SRC_URI += "\
        file://0001-remove-err-on-warn-set-perl.patch \
"

do_install(){
}

S = "${WORKDIR}/git"
