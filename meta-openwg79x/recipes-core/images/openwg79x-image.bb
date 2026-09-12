SUMMARY = "openwg79x rootfs, the artifact written to a rootfs slot"
DESCRIPTION = "Builds the root filesystem only. This is the everyday target: the \
resulting .ext4 is exactly one rootfs slot and is what scripts/deploy-test.sh \
streams onto the idle slot of a running device. Build openwg79x-image-sdcard \
instead for a complete SD card image to flash a blank card with."

require openwg79x-image.inc

# .ext4.gz is what deploy-test.sh sends over the wire, decompressed on the target
# straight into the slot partition. The plain .ext4 is kept alongside it for
# local inspection (mount -o loop) and costs nothing but disk.
IMAGE_FSTYPES = "ext4 ext4.gz"
