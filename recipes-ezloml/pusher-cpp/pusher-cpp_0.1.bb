DESCRIPTION = "Pusher client and server built on Boost.Asio"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

DEPENDS += " \
    boost \
    openssl \
    curl \
    cmake-native \
    ninja-native \
"

SRCREV = "${AUTOREV}"
SRC_URI = " \
        git://github.com/BenPope/pusher-cpp.git;branch=master;protocol=http \
        file://0001-add-ssl-support.patch \
"

SRC_URI[md5sum] = "d41d8cd98f00b204e9800998ecf8427e"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"
S = "${WORKDIR}/git"

do_configure[no_exec] = "1"
do_compile[no_exec] = "1"

do_install(){
    install -d ${D}/${includedir}/pusher++
    install -m 0664 ${S}/pusher++/include/pusher++/*.hpp ${D}${includedir}/pusher++ 

    install -d ${D}/${includedir}/pusher++/detail/client/
    install -m 0664 ${S}/pusher++/include/pusher++/detail/client/*.hpp ${D}${includedir}/pusher++/detail/client

    install -d ${D}/${includedir}/pusher++/detail/server/
    install -m 0664 ${S}/pusher++/include/pusher++/detail/server/*.hpp ${D}${includedir}/pusher++/detail/server
}
