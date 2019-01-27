DESCRIPTION = "fdk-aac package provides the Fraunhofer FDK AAC library, which is purported to be a high quality Advanced Audio Coding implementation "

LICENSE = "MIT"

SRC_URI = "git://github.com/mstorsjo/fdk-aac.git;tag=v${PV};protocol=http"
S = "${WORKDIR}/git"

inherit autotools pkgconfig
LIC_FILES_CHKSUM = "file://NOTICE;md5=087ae5edf3094fbebf2e44334fa2155c"

SRC_URI[md5sum] = "300fc7f0e1f97c78f624834dfd185c3e"

CFLAGS += "-Wno-narrowing"
CXXFLAGS += "-Wno-narrowing"
