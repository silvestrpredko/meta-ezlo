SUMMARY = "nodejs onvif package"
SECTION = "js/devel"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "npm://registry.npmjs.org;name=${PN};version=${PV}"
NPMPN = "node-onvif"
inherit npm

