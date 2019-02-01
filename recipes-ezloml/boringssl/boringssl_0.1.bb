DESCRIPTION = "RPC library"
LICENSE = "MIT"
inherit cmake pkgconfig

DEPENDS = "perl-native libunwind-native golang-native"

SRCREV = "d2f87a777955dd53d7768b236ec3804fe50462a7"
SRC_URI = "git://github.com/google/boringssl.git;branch=master;protocol=http"
SRC_URI[md5sum] = "cec140cf0160dee3c08dcbc6a62c0deb"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9b082148f9953258347788edb83e401b"

SRC_URI += "\
        file://0001-remove-err-on-warn-set-perl.patch \
"

do_install(){
    install -d ${D}/${libdir}/boringssl/lib/
    install -m 0644 ${B}/ssl/libssl.a ${D}/${libdir}/boringssl/lib/
    
    install -d ${D}/${libdir}/boringssl/lib/
    install -m 0644 ${B}/crypto/libcrypto.a ${D}/${libdir}/boringssl/lib/
    
    install -d ${D}/${libdir}/boringssl/include/openssl
    install -m 0644 ${S}/include/openssl/*.h ${D}/${libdir}/boringssl/include/openssl/
}

S = "${WORKDIR}/git"

FILES_${PN}-staticdev += "${libdir}/boringssl/lib/*.a"
FILES_${PN}-staticdev += "${libdir}/boringssl/lib/*.a"

#BBCLASSEXTEND = "native"
