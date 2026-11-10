SUMMARY ?= "openwg79x base image"
LICENSE = "MIT"

inherit core-image

# Read-only rootfs: a slot is rewritten wholesale by an update and must survive
# power loss at any moment, so every mutable path lives on the data partition
# or on volatile storage.
IMAGE_FEATURES += "read-only-rootfs ssh-server-dropbear allow-root-login"

IMAGE_INSTALL:append = " \
    kernel-modules \
    kernel-image \
    kernel-devicetree \
    openwg79x-slotboot \
    libubootenv-bin \
    u-boot-env \
    wpa-supplicant \
    openwg79x-data \
    gpsd \
    gps-utils \
    openwg79x-rc \
    openocd \
    chrony \
    chronyc \
    "

# gps-utils recommends gps-utils-python, which drags the whole python3 runtime in
# for xgps/ubxtool/zerk. gpspipe and the other C tools do not need it.
BAD_RECOMMENDATIONS += "gps-utils-python"

ROOTFS_POSTPROCESS_COMMAND:append = " openwg79x_files_to_data"
openwg79x_files_to_data() {
    install -d ${IMAGE_ROOTFS}/data/etc
    printf "%s\n" "# * = locked. To set a password, run on the build machine: openssl passwd -6 'newpassword'" "# and replace * in the user's line with the printed hash." > ${IMAGE_ROOTFS}/data/etc/shadow
    cat ${IMAGE_ROOTFS}${sysconfdir}/shadow >> ${IMAGE_ROOTFS}/data/etc/shadow
    rm ${IMAGE_ROOTFS}${sysconfdir}/shadow
    chmod 0600 ${IMAGE_ROOTFS}/data/etc/shadow
    ln -s /data/etc/shadow ${IMAGE_ROOTFS}${sysconfdir}/shadow

    install -d ${IMAGE_ROOTFS}/data/etc/default
    mv ${IMAGE_ROOTFS}${sysconfdir}/default/gpsd.default ${IMAGE_ROOTFS}/data/etc/default/gpsd
    ln -sf /data/etc/default/gpsd ${IMAGE_ROOTFS}${sysconfdir}/default/gpsd

    sed -i "s|^DROPBEAR_RSAKEY_DIR=.*|DROPBEAR_RSAKEY_DIR=/data/etc/dropbear|" ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear
}
