SUMMARY = "Sample content of the data partition and the openwg user"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://etc/wpa_supplicant.conf file://home/root/.ssh/authorized_keys file://home/openwg/.ssh/authorized_keys file://etc/sudoers.d/openwg"
S = "${WORKDIR}"

inherit useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-U -d /home/openwg -s /bin/sh -G dialout -p '*' openwg"

do_install() {
    install -D -m 0600 ${S}/etc/wpa_supplicant.conf ${D}/data/etc/wpa_supplicant.conf
    install -d -m 0700 ${D}/data/home/root/.ssh
    install -m 0600 ${S}/home/root/.ssh/authorized_keys ${D}/data/home/root/.ssh/authorized_keys
    install -d -m 0700 ${D}${ROOT_HOME}/.ssh
    ln -s /data/home/root/.ssh/authorized_keys ${D}${ROOT_HOME}/.ssh/authorized_keys

    install -d -m 0700 -o openwg -g openwg ${D}/data/home/openwg/.ssh
    install -m 0600 -o openwg -g openwg ${S}/home/openwg/.ssh/authorized_keys ${D}/data/home/openwg/.ssh/authorized_keys
    install -d -m 0755 -o openwg -g openwg ${D}/home/openwg
    install -d -m 0700 -o openwg -g openwg ${D}/home/openwg/.ssh
    ln -s /data/home/openwg/.ssh/authorized_keys ${D}/home/openwg/.ssh/authorized_keys

    install -D -m 0440 ${S}/etc/sudoers.d/openwg ${D}${sysconfdir}/sudoers.d/openwg
}

FILES:${PN} = "/data ${ROOT_HOME}/.ssh /home/openwg ${sysconfdir}/sudoers.d"
RDEPENDS:${PN} = "sudo"
