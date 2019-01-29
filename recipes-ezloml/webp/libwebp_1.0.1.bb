SUMMARY = "WebP is an image format designed for the Web"
DESCRIPTION = "WebP is a method of lossy and lossless compression that can be \
               used on a large variety of photographic, translucent and \
               graphical images found on the web. The degree of lossy \
               compression is adjustable so a user can choose the trade-off \
               between file size and image quality. WebP typically achieves \
               an average of 30% more compression than JPEG and JPEG 2000, \
               without loss of image quality."
HOMEPAGE = "https://developers.google.com/speed/webp/"
SECTION = "libs"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=6e8dee932c26f2dab503abf70c96d8bb \
                    file://PATENTS;md5=c6926d0cb07d296f886ab6e0cc5a85b7"

SRC_URI = "http://downloads.webmproject.org/releases/webp/${BP}.tar.gz"
SRC_URI[md5sum] = "ba72dfa7588c751a3a9b735a6746a23e"
SRC_URI[sha256sum] = "8c744a5422dbffa0d1f92e90b34186fb8ed44db93fbacb55abd751ac8808d922"

UPSTREAM_CHECK_URI = "http://downloads.webmproject.org/releases/webp/index.html"

EXTRA_OECONF = " \
    --disable-wic \
    --enable-libwebpmux \
    --enable-libwebpdemux \
    --enable-threading \
    --enable-static \
"
# Do not trust configure to determine if neon is available.
#
EXTRA_OECONF_append_arm = " \
    ${@bb.utils.contains("TUNE_FEATURES","neon","--enable-neon","--disable-neon",d)} \
"

inherit autotools lib_package

PACKAGECONFIG ??= ""

# libwebpdecoder is a subset of libwebp, don't build it unless requested
PACKAGECONFIG[decoder] = "--enable-libwebpdecoder,--disable-libwebpdecoder"

# Apply for examples programs: cwebp and dwebp
PACKAGECONFIG[gif] = "--enable-gif,--disable-gif,giflib"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg"
PACKAGECONFIG[png] = "--enable-png,--disable-png,,libpng"
PACKAGECONFIG[tiff] = "--enable-tiff,--disable-tiff,tiff"

# Apply only for example program vwebp
PACKAGECONFIG[gl] = "--enable-gl,--disable-gl,mesa-glut"

PACKAGES =+ "${PN}-gif2webp"

DESCRIPTION_${PN}-gif2webp = "Simple tool to convert animated GIFs to WebP"
FILES_${PN}-gif2webp = "${bindir}/gif2webp"
