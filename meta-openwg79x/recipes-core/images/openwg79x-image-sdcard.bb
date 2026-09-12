SUMMARY = "openwg79x complete SD card image (dual-root + data partition)"
DESCRIPTION = "Builds the full .wic: boot partition, both rootfs slots populated \
with the same rootfs, and an empty data partition. Flash this to a blank card; \
use openwg79x-image afterwards to update a running device slot by slot."

require openwg79x-image.inc

IMAGE_FSTYPES = "wic.bz2 wic.bmap"
WKS_FILE = "openwg79x-dualroot.wks"

# The boot partition is shared by both slots, so each slot's kernel, device tree
# and overlays go into its own subdirectory and only the firmware, the slot
# configs and the config.txt/tryboot.txt pair sit at the root. Entries are
# installed in the order listed, which is how our config.txt overwrites the one
# the BSP drops in via the bootfiles glob.
IMAGE_BOOT_FILES = "\
    ${BOOTFILES_DIR_NAME}/* \
    ${OPENWG79X_SLOT_BOOT_FILES} \
    ${OPENWG79X_BOOTFILES_DIR}/cmdline-p2.txt;p2/cmdline.txt \
    ${OPENWG79X_BOOTFILES_DIR}/cmdline-p3.txt;p3/cmdline.txt \
    ${OPENWG79X_BOOTFILES_DIR}/slot-p2.txt;slot-p2.txt \
    ${OPENWG79X_BOOTFILES_DIR}/slot-p3.txt;slot-p3.txt \
    ${OPENWG79X_BOOTFILES_DIR}/config.txt;config.txt \
    ${OPENWG79X_BOOTFILES_DIR}/tryboot.txt;tryboot.txt \
    "

EXTRA_IMAGEDEPENDS += "openwg79x-bootfiles"
do_image_wic[depends] += "openwg79x-bootfiles:do_deploy"

# /etc/fstab is owned by the base-files bbappend. Without this, wic would append
# its own entries to the copy of the rootfs inside the .wic only, making a
# freshly flashed card differ from one updated with the standalone .ext4.
WIC_CREATE_EXTRA_ARGS ?= "--no-fstab-update"
