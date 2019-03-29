## These are some package groups I'm using
# but are not enabled by default. If you
# want to enable them then add them in the
# IMAGE_INSTALL in allwinner-image.bb

# Sensors
SENSORS_PKGS = " \
    lmsensors-sensors \
    lmsensors-libsensors \
"

# Standard packages
STANDARD_PKGS = " \
    tar \
    bash \
    merge-files \
    wget \
    procps \
    udev-automount \
    usbreset \
    udev \
    dfu-util \
    pkgconfig \
    u-boot-fw-tools \
"

# Extra debug packages
EXTRA_DEBUG_PKGS = " \
    bc \
    ethtool \
    htop \
    nano \
    pciutils \
    binutils \
    zip \
    unzip \
    util-linux \
    tmux \
    lsof \
    strace \
"

APT_PKGS = " \
    apt \
    apt-repo \
"

WIFI_SUPPORT = " \
    crda \
    iw \
    wireless-tools \
    wpa-supplicant \
    dnsmasq \
    hostapd \
"

# Packages for ALSA
ALSA_PKGS = " \
    libasound \
    alsa-tools \
    alsa-utils \
    alsa-server \
"


# Dev and SDK packages
DEV_SDK_INSTALL = " \
    cmake \
    binutils \
    binutils-symlinks \
    coreutils \
    gdb \
    gdbserver \
    diffutils \
    file \
    gettext \
    git \
    ldd \
    libtool \
    make \
    pkgconfig \
    ninja \
    valgrind \
"

# Testing tools
TEST_TOOLS = " \
    rt-tests \
    stress \
    sysstat \
    perf \
    memtester \
    iperf3 \
"
