#!/bin/sh

# Tool to transfer the rootfs of an already running installation from SD card eMMC.
# In case of eMMC it's also possible to transfer
# the bootloader to eMMC in a single step so from then on running without SD card is
# possible.

# emmc layout
BOOT_SECTOR_START=8192
BOOT_SECTOR_END=92160
ROOTFS_SECTOR_START=100352

# First partition begin at sector 2048 : 2048*1024 = 2097152
IMAGE_ROOTFS_ALIGNMENT="2048"

# current root
ROOT_PART=$(cat /proc/cmdline | tr " " "\n" | grep "root=" | cut -f 2 -d '='| cut -f 3 -d '/')
ROOT_DEV="/dev/$(dirname $(readlink /sys/class/block/${ROOT_PART}) | grep -o '[^/]*$')"

CWD=$PWD
EX_LIST="$CWD/exclude.txt"

if [ "$ROOT_DEV" = $1 ]; then
    echo "invalid argument - could not flash itself"
fi

# formatting eMMC [device] example /dev/mmcblk1 - one can select filesystem type
#
format_emmc()
{
	# deletes all partitions on eMMC drive
	dd bs=1 seek=446 count=64 if=/dev/zero of="$1"

    QUOTED_DEVICE=$(echo "$1" | sed 's:/:\\\/:g')
	CAPACITY=$(parted "$1" unit s print -sm | awk -F":" "/^${QUOTED_DEVICE}/ {printf (\"%0d\", \$2 / ( 1024 / \$4 ))}")
	if [[ $CAPACITY -lt 4000000 ]]; then
		# Leave 2 percent unpartitioned when eMMC size is less than 4GB (unlikely)
		LASTSECTOR=$(( 32 * $(parted "$1" unit s print -sm | awk -F":" "/^${QUOTED_DEVICE}/ {printf (\"%0d\", ( \$2 * 98 / 3200))}") -1 ))
	else
		# Leave 1 percent unpartitioned
		LASTSECTOR=$(( 32 * $(parted "$1" unit s print -sm | awk -F":" "/^${QUOTED_DEVICE}/ {printf (\"%0d\", ( \$2 * 99 / 3200))}") -1 ))
	fi

	# create partition table
	parted -s $1 mklabel msdos

	# create boot partition and mark it as bootable
	parted -s $1 -- mkpart primary fat32 ${BOOT_SECTOR_START}s ${BOOT_SECTOR_END}s
	parted -s $1 set 1 boot on

	# create rootfs partition
    parted -s $1 -- mkpart primary ext4 ${ROOTFS_SECTOR_START}s ${LASTSECTOR}s
    mkfs.ext4 -F -O ^metadata_csum,^has_journal "$1"'p2'

    # write boot partition
    dd if="$ROOT_DEV"p1 of="$1"'p1'

    echo "$EX_LIST"
    # write rootfs
    sync
    mkdir -p /mnt/rootfs
    rm -rf /mnt/rootfs/*
    mount "$1"'p2' /mnt/rootfs
    rsync -avrltD --delete --exclude-from=$EX_LIST / /mnt/rootfs
    sync
    umount /mnt/rootfs

    # write u-boot-spl at the begining of sdcard in one shot
    dd if="$ROOT_DEV" of=$1 bs=1024 count=640 skip=8 seek=8
}

format_emmc $1
