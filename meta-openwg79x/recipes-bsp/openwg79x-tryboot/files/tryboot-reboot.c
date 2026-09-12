/*
 * tryboot-reboot - reboot passing a string to the Raspberry Pi firmware.
 *
 * The one-shot tryboot flag is armed by rebooting with the string "0 tryboot".
 * The kernel hands that string to the BCM restart handler, which sets a bit in
 * the power management scratch register; the bootloader reads it on the next
 * boot, loads tryboot.txt instead of config.txt, and clears it again. Nothing
 * is written to the SD card, so an interrupted or failed test boot always comes
 * back on the committed slot.
 *
 * This exists because busybox's reboot applet calls reboot(RB_AUTOBOOT) and has
 * no way to pass a string, and this image has no systemd whose "systemctl
 * reboot <arg>" would do it.
 *
 * SPDX-License-Identifier: MIT
 */

#include <linux/reboot.h>
#include <stdio.h>
#include <string.h>
#include <sys/syscall.h>
#include <unistd.h>

int main(int argc, char **argv)
{
	const char *arg = (argc > 1) ? argv[1] : "0 tryboot";

	if (argc > 2) {
		fprintf(stderr, "usage: %s [reboot-string]   (default: \"0 tryboot\")\n",
			argv[0]);
		return 2;
	}

	sync();

	if (syscall(SYS_reboot, LINUX_REBOOT_MAGIC1, LINUX_REBOOT_MAGIC2,
		    LINUX_REBOOT_CMD_RESTART2, arg) != 0) {
		perror("reboot");
		return 1;
	}

	return 0;
}
