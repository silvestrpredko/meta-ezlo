# DO NOT EDIT THIS FILE
#
# Please edit /boot/armbianEnv.txt to set supported parameters
#

setenv overlay_error "false"
# default values
setenv verbosity "7"
setenv console "both"
setenv disp_mem_reserves "on"
setenv disp_mode "1920x1080p60"
setenv rootfstype "ext4"
setenv docker_optimizations "on"
setenv devnum "0"
setenv rootdev "/dev/mmcblk0p2"
setenv consoleargs "console=ttyS0,115200 console=tty1"
setenv boardargs "usb-storage.quirks=0x2537:0x1066:u,0x2537:0x1068:u cma=96M cgroup_enable=memory swapaccount=1"

# Print boot source
itest.b *0x28 == 0x00 && echo "U-boot loaded from SD"
itest.b *0x28 == 0x02 && echo "U-boot loaded from eMMC or secondary SD"
itest.b *0x28 == 0x03 && echo "U-boot loaded from SPI"

# get PARTUUID of first partition on SD/eMMC it was loaded from
# mmc 0 is always mapped to device u-boot (2016.09+) was loaded from
if test "${devtype}" = "mmc"; then
  part uuid mmc ${devnum}:1 partuuid;
  setenv devnum ${mmc_bootdev}
fi

echo "Boot script loaded from ${devtype}"

setenv bootargs "root=${rootdev} rootwait rootfstype=${rootfstype} ${consoleargs} hdmi.audio=EDID:0 disp.screen0_output_mode=${disp_mode} panic=10 consoleblank=0 loglevel=${verbosity} ${boardargs}"

echo "Found legacy kernel configuration"
load ${devtype} ${devnum} ${fdt_addr_r} script.bin 
load ${devtype} ${devnum} ${kernel_addr_r} uImage
bootm ${kernel_addr_r}

# Recompile with:
# mkimage -C none -A arm -T script -d /boot/boot.cmd /boot/boot.scr
