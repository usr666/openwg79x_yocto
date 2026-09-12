#!/bin/bash
# Sets up (or reuses) the Yocto build directory for the Raspberry Pi Zero 2 W
# image and runs bitbake. Run from the repo root:
#
#   ./build.sh                            rootfs only, for updating a running device
#   ./build.sh openwg79x-image-sdcard     complete SD card image, for a blank card
#
# Note: no "set -u". poky's oe-init-build-env tests $BBSERVER unset, which would
# abort the script before it gets going.
set -eo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET="${1:-openwg79x-image}"

export TEMPLATECONF="$REPO_ROOT/meta-openwg79x/conf/templates/default"

# oe-init-build-env must be sourced, not executed, so it can export the
# bitbake environment (PATH, BBPATH, ...) into this shell.
source "$REPO_ROOT/sources/poky/oe-init-build-env" "$REPO_ROOT/build"

bitbake "$TARGET"
