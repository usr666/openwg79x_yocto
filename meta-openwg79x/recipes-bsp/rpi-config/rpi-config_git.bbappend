# Disables the BSP check that U-Boot needs enable_uart=1.
UBOOT_MACHINE = ""

VC4GRAPHICS = "0"

RPI_EXTRA_CONFIG = "\narm_64bit=1\ndisable_fw_kms_setup=1\ndtparam=uart0=off\ndtparam=uart1=off"
