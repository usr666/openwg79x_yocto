SUMMARY = "Per-slot U-Boot boot script, /boot/boot.scr in the rootfs"
DESCRIPTION = "Loads this slot's kernel, device tree and overlays and boots them. Run by the slot selector on the boot partition (rpi-u-boot-scr)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "^rpi$"
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "u-boot-mkimage-native"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://slot.cmd"
S = "${WORKDIR}"

inherit kernel-arch

do_compile() {
    mkimage -A ${UBOOT_ARCH} -T script -C none -n "openwg79x slot" -d ${S}/slot.cmd ${B}/boot.scr
}

do_install() {
    install -D -m 0644 ${B}/boot.scr ${D}/boot/boot.scr
}

FILES:${PN} = "/boot/boot.scr"
