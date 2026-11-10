SUMMARY = "openwg79x mower remote control and web pages"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/usr666/OpenWG79x_linux.git;protocol=https;branch=master \
           file://openwg79x-rc.init \
           file://sync.sh \
           "
SRCREV = "316eb234a0c521f76bcd638972f6ca949e6be4da"
PV = "1.0+git"
S = "${WORKDIR}/git"

DEPENDS = "openwg79x-data"

inherit update-rc.d

INITSCRIPT_NAME = "openwg79x-rc"
INITSCRIPT_PARAMS = "defaults 60"

do_compile() {
    oe_runmake -C openwg79x_rc
}

do_install() {
    install -D -m 0755 ${S}/openwg79x_rc/openwg79x_rc ${D}${bindir}/openwg79x_rc
    install -D -m 0755 ${WORKDIR}/openwg79x-rc.init ${D}${sysconfdir}/init.d/openwg79x-rc
    install -d ${D}${datadir}/openwg79x-rc/www
    install -m 0644 ${S}/web/mowermap.html ${S}/web/woeditor.html ${D}${datadir}/openwg79x-rc/www/
    install -d -o openwg -g openwg ${D}/data/openwg79x_rc/workorder ${D}/data/openwg79x_rc/layers
    install -m 0644 ${WORKDIR}/sync.sh ${D}/data/openwg79x_rc/sync.sh
}

FILES:${PN} += "/data"
RDEPENDS:${PN} = "gps-utils lighttpd lighttpd-module-openssl lighttpd-module-webdav lighttpd-module-wstunnel lighttpd-module-dirlisting openssl-bin rsync openssh-ssh"
