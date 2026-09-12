# Shared definitions for the dual-root tryboot layout.
#
# SD card layout, see meta-openwg79x/wic/openwg79x-dualroot.wks:
#
#   p1  boot  vfat   firmware, config.txt, tryboot.txt, slot-p{2,3}.txt, p2/, p3/
#   p2  root  ext4   one rootfs slot
#   p3  root  ext4   the other rootfs slot
#   p4  data  ext4   persistent state, never written by an update
#
# Slots are named after their partition, so slot p3 lives in /p3 on the boot
# partition and boots root=/dev/mmcblk0p3. Nothing has to map a slot name onto
# a partition number.
#
# Booting is done by the Raspberry Pi firmware itself, no U-Boot involved.
# config.txt selects the committed slot via os_prefix; when the one-shot
# tryboot flag is set (reboot "0 tryboot") the firmware loads tryboot.txt
# instead, which points at the other slot. The flag is cleared before the
# firmware starts, so any failure to boot falls back to config.txt.

OPENWG79X_DISK ?= "/dev/mmcblk0"
OPENWG79X_SLOTS ?= "p2 p3"

# Size of each rootfs slot in the .wic. The rootfs is capped well below this so
# that an image built later still fits a slot on an already flashed card. The
# partition layout cannot be changed without re-flashing.
OPENWG79X_SLOT_SIZE_MIB ?= "512"
IMAGE_ROOTFS_MAXSIZE ?= "458752"

# Subdirectory of DEPLOY_DIR_IMAGE holding the generated boot partition files.
OPENWG79X_BOOTFILES_DIR ?= "openwg79x-bootfiles"

def openwg79x_slot_boot_files(d):
    # IMAGE_BOOT_FILES entries placing the kernel, device tree and overlays
    # into a per-slot subdirectory of the boot partition, so that updating a
    # slot swaps kernel, DTB and userland together and the two slots can run
    # different kernel versions. Mirrors make_dtb_boot_files() from
    # meta-raspberrypi's rpi-base.inc, but prefixed with the slot name.
    kernel = d.getVar('KERNEL_IMAGETYPE')
    kernel_img = d.getVar('SDIMG_KERNELIMAGE')
    dtbs = (d.getVar('KERNEL_DEVICETREE') or '').split()

    entries = []
    for slot in (d.getVar('OPENWG79X_SLOTS') or '').split():
        entries.append('{};{}/{}'.format(kernel, slot, kernel_img))
        for dtb in dtbs:
            base = os.path.basename(dtb)
            if dtb.endswith('dtbo') or base == 'overlay_map.dtb':
                entries.append('{};{}/overlays/{}'.format(base, slot, base))
            elif dtb.endswith('dtb'):
                entries.append('{};{}/{}'.format(base, slot, base))
    return ' '.join(entries)

OPENWG79X_SLOT_BOOT_FILES = "${@openwg79x_slot_boot_files(d)}"
