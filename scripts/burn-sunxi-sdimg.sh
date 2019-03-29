#! /bin/bash

SD_DEV=$1
SUNXI_IMG=$2

if ! [ $(id -u) = 0 ]; then
    echo "run script as root"
    exit 1
fi

if [ -z "${SD_DEV}" ]; then
    echo "SD device is not specified"
    exit 1
fi

if ! [ -e ${SD_DEV}  ]; then
    echo "${SD_DEV} does not exist"
    exit 1
fi

for part in ${SD_DEV}*
do
    case "$part" in
    *[0-9])
        echo "umount ${part}"
        umount ${part}
        ;;
    *)
        echo "skip SD device itself ${part}"
        ;;
esac
done

echo "burning the sunxi sd-img to raw sd"
# 1. burn the image on raw device
sudo dd if=${SUNXI_IMG} of=${SD_DEV}

echo "check rootfs"
# 2. check root filesystem
sudo e2fsck -f "${SD_DEV}2"

echo "remove unsupported options"
# 3. remove options that are unsupported by legacy kernel ext4 driver
sudo tune2fs -O ^metadata_csum,^has_journal "${SD_DEV}2"

sync
echo "done"
