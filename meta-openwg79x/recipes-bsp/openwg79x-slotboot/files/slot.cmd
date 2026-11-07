setenv bootargs "console=tty1 root=/dev/mmcblk0p${slot_part} rootfstype=ext4 rootwait net.ifnames=0 dwc_otg.lpm_enable=0"

load mmc ${devnum}:${slot_part} ${kernel_addr_r} /boot/Image
load mmc ${devnum}:${slot_part} ${fdt_addr_r} /boot/bcm2710-rpi-zero-2-w.dtb
fdt addr ${fdt_addr_r}
fdt resize 65536

load mmc ${devnum}:${slot_part} ${ramdisk_addr_r} /boot/vc4-kms-v3d.dtbo
# U-Boot 2024.01 libfdt fails if fragment@0 keeps its phandle.
fdt addr ${ramdisk_addr_r}
fdt rm /fragment@0/__overlay__ phandle
fdt addr ${fdt_addr_r}
fdt apply ${ramdisk_addr_r} && load mmc ${devnum}:${slot_part} ${ramdisk_addr_r} /boot/disable-bt.dtbo && fdt apply ${ramdisk_addr_r} && load mmc ${devnum}:${slot_part} ${ramdisk_addr_r} /boot/uart0.dtbo && fdt apply ${ramdisk_addr_r} || load mmc ${devnum}:${slot_part} ${fdt_addr_r} /boot/bcm2710-rpi-zero-2-w.dtb

booti ${kernel_addr_r} - ${fdt_addr_r}
