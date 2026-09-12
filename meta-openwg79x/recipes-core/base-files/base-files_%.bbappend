FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# base-files already fetches file://fstab; prepending our directory to
# FILESEXTRAPATHS is what makes ours win.

do_install:append() {
    install -d ${D}/data
}
