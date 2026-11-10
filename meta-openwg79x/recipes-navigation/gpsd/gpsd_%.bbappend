FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://0001-ntrip-retry.patch"

do_install:append() {
    sed -i -e "/dpkg-reconfigure/d" -e "/directly - use/d" -e "/change the options/d" -e "s|^DEVICES=.*|DEVICES=\"/dev/ttyAMA0\"|" -e "s|^USBAUTO=.*|USBAUTO=\"false\"|" ${D}${sysconfdir}/default/gpsd.default
}
