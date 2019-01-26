DESCRIPTION = "OpenBLAS is an optimized BLAS library based on GotoBLAS2 1.13 BSD version." 
SUMMARY = "OpenBLAS : An optimized BLAS library" 
HOMEPAGE = "http://www.openblas.net/" 
SECTION = "libs" 
LICENSE = "BSD-3-Clause" 


DEPENDS = "make" 


LIC_FILES_CHKSUM = "file://LICENSE;md5=5adf4792c949a00013ce25d476a2abc0" 


SRC_URI = "https://github.com/xianyi/OpenBLAS/archive/v${PV}.tar.gz" 
SRC_URI[md5sum] = "42cde2c1059a8a12227f1e6551c8dbd2" 
SRC_URI[sha256sum] = "cf51543709abe364d8ecfb5c09a2b533d2b725ea1a66f203509b21a8e9d8f1a1" 


S = "${WORKDIR}/OpenBLAS-${PV}" 

do_compile () { 
    if [ ${DEFAULTTUNE} = "aarch64" ]; then
        oe_runmake TARGET=ARMV8     \
                                HOSTCC="${BUILD_CC}"                                         \ 
								AR="${TARGET_PREFIX}ar"                        \
                                NOFORTRAN=0 NO_LAPACK=1 USE_THREAD=0
		oe_runmake -C utest TARGET=ARMV8     \
                                HOSTCC="${BUILD_CC}"                                         \ 
                                NOFORTRAN=0 NO_LAPACK=1 USE_THREAD=0
	else
	    oe_runmake TARGET=ARMV7     \
                                HOSTCC="${BUILD_CC}"                                         \ 
								AR="${TARGET_PREFIX}ar"                        \
                                NOFORTRAN=0 NO_LAPACK=1 USE_THREAD=0
		oe_runmake -C utest TARGET=ARMV7     \
                                HOSTCC="${BUILD_CC}"                                         \ 
                                NOFORTRAN=0 NO_LAPACK=1 USE_THREAD=0
	fi
} 


do_install () {
#runmake install PREFIX="${D}/opt/${PN}"
#cp ${WORKDIR}/OpenBLAS-${PV}/utest/openblas_utest ${D}/opt/${PN}/bin
            oe_runmake install DESTDIR=${D} PREFIX=${prefix}
	    rm -rf ${D}${libdir}/cmake            
	    rm -rf ${D}/usr/bin            
} 

do_package_qa[noexec] = "1"

FILES_${PN} += " \
        ${includedir} \
        ${libdir}/lib${PN}.a \
"
#INSANE_SKIP_${PN} += "staticdev"
