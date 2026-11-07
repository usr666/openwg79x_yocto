SUMMARY = "openwg79x complete SD card image (dual-root + data partition)"
DESCRIPTION = "Builds the full .wic: boot partition, both rootfs slots populated \
with the same rootfs, and an empty data partition. Flash this to a blank card; \
use openwg79x-image afterwards to update a running device slot by slot."

require openwg79x-image.inc

IMAGE_FSTYPES = "wic.bz2 wic.bmap"
WKS_FILE = "openwg79x-dualroot.wks"

IMAGE_BOOT_FILES = "\
    ${BOOTFILES_DIR_NAME}/* \
    u-boot.bin;${SDIMG_KERNELIMAGE} \
    boot.scr \
    bcm2710-rpi-zero-2-w.dtb \
    bcm2710-rpi-zero-2.dtb \
    "

do_image_wic[depends] += "rpi-u-boot-scr:do_deploy"

# /etc/fstab is owned by the base-files bbappend. Without this, wic would append
# its own entries to the copy of the rootfs inside the .wic only, making a
# freshly flashed card differ from one updated with the standalone .ext4.
WIC_CREATE_EXTRA_ARGS ?= "--no-fstab-update"
