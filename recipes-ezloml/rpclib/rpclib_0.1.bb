DESCRIPTION = "RPC library"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=d49aa44c8300837bd95e0d345ce50fef"

SRCREV = "3b00c4ccf480b9f9569b1d064e7a3b43585b8dfd"
SRC_URI = " \
    git://github.com/rpclib/rpclib.git;branch=master;protocol=http \
    file://0001-fix-race-condiftion.patch \
"
SRC_URI[md5sum] = "1d04b7cda5e96ab841ca4bed71fe7d3b"

inherit cmake pkgconfig

S = "${WORKDIR}/git"
