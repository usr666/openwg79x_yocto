#!/bin/sh
#   ./scripts/promote.sh <user@host>

set -eu

target=${1:?usage: promote.sh <user@host>}

running_slot=$(ssh -n "$target" cat /proc/cmdline | tr ' ' '\n' | sed -n 's|^root=/dev/mmcblk0||p')
if [ "$running_slot" != p2 ] && [ "$running_slot" != p3 ]; then
    echo "$target is not running a slot (root=/dev/mmcblk0$running_slot)" >&2
    exit 1
fi

ssh -n "$target" "
    set -e
    mount /dev/mmcblk0p1 /mnt
    cp /mnt/slot-$running_slot.txt /mnt/config.txt
    umount /mnt
"

echo "$target now boots slot $running_slot"
