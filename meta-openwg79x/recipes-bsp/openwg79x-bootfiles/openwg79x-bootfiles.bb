SUMMARY = "Boot partition files for the openwg79x dual-root (tryboot) layout"
DESCRIPTION = "Generates the per-slot boot configuration for the Raspberry Pi \
firmware: slot-p2.txt and slot-p3.txt (full config.txt variants differing only in \
os_prefix), the config.txt / tryboot.txt pair a freshly flashed card starts with, \
and one cmdline.txt per slot pointing at that slot's root partition. Also packs \
each slot's complete boot tree (kernel, device tree, overlays, cmdline.txt) into \
a tarball, which is what the update scripts push to a live system."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "^rpi$"
INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

require conf/include/openwg79x-slots.inc

inherit deploy nopackages

# The slot configs are the BSP's own generated config.txt/cmdline.txt with one
# line changed, so that whatever rpi-config derives from the machine features
# stays authoritative. Both must be deployed before this recipe compiles.
do_compile[depends] += " \
    rpi-config:do_deploy \
    rpi-cmdline:do_deploy \
    virtual/kernel:do_deploy \
"

BOOTSLOTS_DIR = "${WORKDIR}/bootslots"

do_compile() {
    bsp_config="${DEPLOY_DIR_IMAGE}/${BOOTFILES_DIR_NAME}/config.txt"
    bsp_cmdline="${DEPLOY_DIR_IMAGE}/${BOOTFILES_DIR_NAME}/cmdline.txt"
    test -r "$bsp_config" || bbfatal "rpi-config did not deploy $bsp_config"
    test -r "$bsp_cmdline" || bbfatal "rpi-cmdline did not deploy $bsp_cmdline"

    rm -rf ${BOOTSLOTS_DIR}
    install -d ${BOOTSLOTS_DIR}

    # Per slot: a full config.txt selecting it via os_prefix, and the BSP's
    # cmdline.txt with root= repointed at that slot's partition. [all] resets any
    # conditional filter the BSP config may have left open, so os_prefix is not
    # swallowed by it. The include directive is deliberately not used:
    # bootcode.bin, which is what boots the Zero 2 W, does not support it.
    for slot in ${OPENWG79X_SLOTS}; do
        root=${OPENWG79X_DISK}$slot

        cp "$bsp_config" ${WORKDIR}/slot-$slot.txt
        printf '\n[all]\nos_prefix=%s/\n' "$slot" >> ${WORKDIR}/slot-$slot.txt

        install -d ${BOOTSLOTS_DIR}/$slot
        sed -e "s|root=[^ ]*|root=$root|" "$bsp_cmdline" > ${BOOTSLOTS_DIR}/$slot/cmdline.txt

        # If that substitution ever silently misses, both slots boot the same
        # rootfs and the whole scheme quietly stops meaning anything.
        tr ' ' '\n' < ${BOOTSLOTS_DIR}/$slot/cmdline.txt | grep -qx "root=$root" ||
            bbfatal "slot $slot cmdline.txt has no root=$root; is CMDLINE_ROOTFS set in $bsp_cmdline?"
    done

    # Kernel, device tree and overlays into each slot directory, from the same
    # list the image recipe feeds to IMAGE_BOOT_FILES. The list is assigned to a
    # quoted variable first: bitbake shell-parses task code after expanding it,
    # and the "src;dst" separators would otherwise read as command separators.
    slot_boot_files="${OPENWG79X_SLOT_BOOT_FILES}"
    for entry in $slot_boot_files; do
        src=${entry%%;*}
        dst=${entry#*;}
        install -m 0644 -D "${DEPLOY_DIR_IMAGE}/$src" "${BOOTSLOTS_DIR}/$dst"
    done
}

do_deploy() {
    install -d ${DEPLOYDIR}/${OPENWG79X_BOOTFILES_DIR}

    for slot in ${OPENWG79X_SLOTS}; do
        install -m 0644 ${WORKDIR}/slot-$slot.txt ${DEPLOYDIR}/${OPENWG79X_BOOTFILES_DIR}/slot-$slot.txt
        install -m 0644 ${BOOTSLOTS_DIR}/$slot/cmdline.txt \
            ${DEPLOYDIR}/${OPENWG79X_BOOTFILES_DIR}/cmdline-$slot.txt
        # Boot tree per slot, pushed to a live system by scripts/deploy-test.sh
        tar --numeric-owner --owner=0 --group=0 --sort=name \
            -C ${BOOTSLOTS_DIR} -czf ${DEPLOYDIR}/openwg79x-bootslot-$slot.tar.gz $slot
    done

    # A freshly flashed card runs p2 and offers p3 as the try target.
    install -m 0644 ${WORKDIR}/slot-p2.txt ${DEPLOYDIR}/${OPENWG79X_BOOTFILES_DIR}/config.txt
    install -m 0644 ${WORKDIR}/slot-p3.txt ${DEPLOYDIR}/${OPENWG79X_BOOTFILES_DIR}/tryboot.txt
}

addtask deploy before do_build after do_compile
do_compile[dirs] += "${WORKDIR}"
do_deploy[dirs] += "${DEPLOYDIR}/${OPENWG79X_BOOTFILES_DIR}"
