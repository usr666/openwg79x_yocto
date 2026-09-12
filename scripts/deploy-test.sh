#!/bin/sh
#   ./scripts/deploy-test.sh <user@host>

set -eu

target=${1:?usage: deploy-test.sh <user@host>}
deploy_dir=$(cd "$(dirname "$0")/../build/tmp/deploy/images/raspberrypi0-2w-64" && pwd)
rootfs=$deploy_dir/openwg79x-image-raspberrypi0-2w-64.rootfs.ext4.gz

running_slot=$(ssh -n "$target" cat /proc/cmdline | tr ' ' '\n' | sed -n 's|^root=/dev/mmcblk0||p')
case $running_slot in
    p2) idle_slot=p3 ;;
    p3) idle_slot=p2 ;;
    *)
        echo "$target is not running a slot (root=/dev/mmcblk0$running_slot)" >&2
        exit 1
        ;;
esac

echo "writing slot $idle_slot on $target"

dd if="$deploy_dir/openwg79x-bootslot-$idle_slot.tar.gz" bs=1M status=progress | ssh "$target" "
    set -e
    mount /dev/mmcblk0p1 /mnt
    rm -rf /mnt/$idle_slot
    gzip -dc | tar -C /mnt -xf -
    umount /mnt
"

dd if="$rootfs" bs=1M status=progress | ssh "$target" "
    set -e
    gzip -dc | dd of=/dev/mmcblk0$idle_slot bs=4M
    sync
"

ssh -n "$target" "
    set -e
    mount /dev/mmcblk0p1 /mnt
    cp /mnt/slot-$idle_slot.txt /mnt/tryboot.txt
    umount /mnt
    tryboot-reboot '0 tryboot'
" || true

echo "booting slot $idle_slot once; promote.sh keeps it, power cycle rolls back"
