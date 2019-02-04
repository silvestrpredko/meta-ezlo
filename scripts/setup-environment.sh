#!/bin/bash

NCPU=$(grep -c processor /proc/cpuinfo)
CWD=$(pwd)
METALAYER=meta-ezlo

if [ "$(whoami)" = "root" ]; then
    echo "ERROR: do not use the BSP as root. Exiting..."
fi

if [ ! -e $CWD/meta-sources/meta-sunxi/conf/machine/orange-pi-plus2.conf ]; then
    cp $CWD/meta-sources/meta-ezlo/conf/orange-pi-plus2.conf.template  $CWD/meta-sources/meta-sunxi/conf/machine/orange-pi-plus2.conf
fi

if [ -z "$MACHINE" ]; then
    echo "ERROR: you have to set the MACHINE. Exiting..."
    exit 1
fi

if [ -z "$WEBRTC_SRC_PATH" ]; then
    echo "ERROR: you have to set the path to webrtc sources. Exiting..."
    exit 1
fi

if [ -z "$DEPOT_TOOLS_PATH" ]; then
    echo "ERROR: you have to set the path to depot tools. Exiting..."
    exit 1
fi

if [ -z "$EZLOML_SRC_PATH" ]; then
    echo "ERROR: you have to set the path to  ezloml sources. Exiting..."
    exit 1
fi

# Check the machine type specified
LIST_MACHINES=$(ls -1 $CWD/meta-sources/*/conf/machine | grep -F ".conf" | sed s/\.conf//g)
VALID_MACHINE=$(echo -e "$LIST_MACHINES" | grep ${MACHINE} | wc -l )
if [ "x$MACHINE" = "x" ] || [ "$VALID_MACHINE" = "0" ]; then
    echo -e "\nThe \$MACHINE you have specified ($MACHINE) is not supported by this build setup"
    echo -e "\n \$x$MACHINE=(x$MACHINE) and \$VALID_MACHINE=($VALID_MACHINE)"
    echo -e "\n\nLIST:\n$LIST_MACHINES\n\n"
    return 1
else
    if [ ! -e $1/conf/local.conf.template ]; then
        echo "Configuring for ${MACHINE}"
    fi
fi

OEROOT=$(pwd)/meta-sources/poky
. $OEROOT/oe-init-build-env $CWD/$1 > /dev/null

# if conf/local.conf not generated, no need to go further
if [ ! -e conf/local.conf ]; then
    echo "conf/local.conf failed to generate. Exiting..."
    return 1
fi

export PATH="$(echo $PATH | sed 's/\(:.\|:\)*:/:/g;s/^.\?://;s/:.\?$//')"

if [ ! -e conf/local.conf.sample ]; then

    # Replace conf/bblayers.conf and conf/local.conf with the platform's ones
    cp -f $CWD/meta-sources/${METALAYER}/conf/bblayers.conf.template $CWD/$1/conf/bblayers.conf
    cp -f $CWD/meta-sources/${METALAYER}/conf/local.conf.template $CWD/$1/conf/local.conf

    # Change settings according environment
    sed -e "s,MACHINE ?=.*,MACHINE = '$MACHINE',g" -i conf/local.conf
    sed -e "s,WEBRTC_SRC_PATH ?=.*,WEBRTC_SRC_PATH = '$WEBRTC_SRC_PATH',g" -i conf/local.conf
    sed -e "s,DEPOT_TOOLS_PATH ?=.*,DEPOT_TOOLS_PATH = '$DEPOT_TOOLS_PATH',g" -i conf/local.conf
    sed -e "s,EZLOML_SRC_PATH ?=.*,EZLOML_SRC_PATH = '$EZLOML_SRC_PATH',g" -i conf/local.conf

    # Append in conf the number of cpus
    cat >> conf/local.conf <<EOF
BB_NUMBER_THREADS = "$NCPU"
PARALLEL_MAKE = "-j $NCPU"
EOF

echo -e "to cross compile ezloml run:\n\t bitbake ezloml"
fi

