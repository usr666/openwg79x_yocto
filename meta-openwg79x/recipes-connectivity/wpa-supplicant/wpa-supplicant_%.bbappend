do_install:append() {
    ln -sf /data/etc/wpa_supplicant.conf ${D}${sysconfdir}/wpa_supplicant.conf
}
