SUMMARY ?= "openwg79x base image"
LICENSE = "MIT"

require conf/include/openwg79x-slots.inc

inherit core-image

# Read-only rootfs: a slot is rewritten wholesale by an update and must survive
# power loss at any moment, so every mutable path lives on the data partition
# or on volatile storage.
IMAGE_FEATURES += "read-only-rootfs ssh-server-dropbear"

# openwg79x-tryboot provides tryboot-reboot, without which nothing in the image
# can arm the one-shot test boot.
IMAGE_INSTALL:append = " kernel-modules openwg79x-tryboot gpsd gps-utils lighttpd"

# gps-utils recommends gps-utils-python, which drags the whole python3 runtime in
# for xgps/ubxtool/zerk. gpspipe and the other C tools do not need it.
BAD_RECOMMENDATIONS += "gps-utils-python"
