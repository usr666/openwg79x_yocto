# openwg79x_yocto

Yocto Project build setup for the Raspberry Pi Zero 2 W, based on the
**scarthgap** (5.0 LTS) release. The image uses two interchangeable rootfs
slots so a device can be updated over the network without any risk of being
left unbootable.

## Prerequisites

A Linux host (or WSL2) with the standard Yocto build-essential packages
installed. On Ubuntu/Debian:

```
sudo apt update
sudo apt install -y gawk wget git diffstat unzip texinfo gcc build-essential \
  chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils \
  iputils-ping python3-git python3-jinja2 python3-subunit zstd liblz4-tool \
  file locales libacl1
sudo locale-gen en_US.UTF-8
```
## Getting the sources

```
git clone --recurse-submodules <this-repo-url>
cd openwg79x_yocto
```

If you already cloned without `--recurse-submodules`:

```
git submodule update --init --recursive
```

## Building

Two targets:

```
./build.sh                          # openwg79x-image        -> rootfs only (.ext4)
./build.sh openwg79x-image-sdcard   # complete SD card image (.wic.bz2)
```

`openwg79x-image` is the everyday target — the `.ext4` it produces is exactly
one rootfs slot, and is what the update scripts stream onto a running device.
`openwg79x-image-sdcard` wraps that same rootfs into the full four-partition
card image, and is only needed to flash a blank card.

To work interactively instead (e.g. `bitbake -c menuconfig virtual/kernel`):

```
export TEMPLATECONF="$PWD/meta-openwg79x/conf/templates/default"
source sources/poky/oe-init-build-env build
bitbake openwg79x-image
```

## Updating a running device

The image has two interchangeable rootfs slots. A new build is written to
whichever slot is idle and booted **once**; if it fails to come up, the device
falls back to the slot it was running before.

```
./scripts/deploy-test.sh  root@10.0.0.7    # write the idle slot, boot it once
./scripts/promote.sh      root@10.0.0.7    # keep the slot that is running
```

## Target machine

Default is `raspberrypi0-2w-64` (64-bit kernel/userspace) — set in
`meta-openwg79x/conf/templates/default/local.conf.sample`. For a 32-bit
build, change `MACHINE` in `build/conf/local.conf` to `raspberrypi0-2w`.

## Flashing a blank card

```
cd build/tmp/deploy/images/raspberrypi0-2w-64/
bzcat openwg79x-image-sdcard-raspberrypi0-2w-64.rootfs.wic.bz2 | sudo dd of=/dev/sdX bs=4M status=progress conv=fsync
sync
```