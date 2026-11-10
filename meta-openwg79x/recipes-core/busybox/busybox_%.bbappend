do_install:append() {
    sed -i -e "s|^#ROTATESIZE=.*|ROTATESIZE=25600|" -e "s|^#ROTATEGENS=.*|ROTATEGENS=1|" ${D}${sysconfdir}/syslog-startup.conf
}
