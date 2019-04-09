SUMMARY = "ws is a simple to use, blazing fast, and thoroughly tested WebSocket client and server implementation"
SECTION = "js/devel"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

inherit npm

SRC_URI = "npm://registry.npmjs.org;name=${NPMPN};version=${PV}"
