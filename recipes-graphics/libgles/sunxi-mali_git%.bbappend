DEPENDS := "${@'${DEPENDS}'.replace('dri2proto', 'xorgproto')}"
PACKAGECONFIG = "x11"

do_configure() {
         DESTDIR=${D}/ make config VERSION=r3p0 ABI=armhf EGL_TYPE=x11
}
