SUMMARY = "reboot(2) wrapper that arms the Raspberry Pi one-shot tryboot flag"
DESCRIPTION = "Arming tryboot means rebooting with the string \"0 tryboot\", which \
requires LINUX_REBOOT_CMD_RESTART2. busybox's reboot applet cannot pass a reboot \
string and this image has no systemd, so without this helper there is no way to \
enter a test boot from userspace."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://tryboot-reboot.c"

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -o ${B}/tryboot-reboot ${S}/tryboot-reboot.c
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/tryboot-reboot ${D}${sbindir}/tryboot-reboot
}
