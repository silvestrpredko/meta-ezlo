DEV_SDK_INSTALL = " \
    binutils \
    binutils-symlinks \
    coreutils \
    cpp \
    cpp-symlinks \
    diffutils \
    elfutils elfutils-binutils \
    file \
    g++ \
    g++-symlinks \
    gcc \
    gcc-symlinks \
    gdb \
    gdbserver \
    gettext \
    git \
    ldd \
    libstdc++ \
    libstdc++-dev \
    libtool \
    ltrace \
    make \
    pkgconfig \
    strace \
"
# Python2.7 related packages
PYTHON2_PKGS = " \
    python \
    python-pip \
    python-modules \
    python-dbus \
"

# Python3 packages
PYTHON3_PKGS = " \
    python3 \
    python3-pip \
    python3-modules \
"

# Bmap tool used to flash images
BMAPTOOL = " \
    gnupg \
    python3 \
    python3-modules \
    python3-six \
    bzip2 \
    bmap-tools-git \
"

EXTRA_TOOLS_INSTALL = " \
    bzip2 \
    devmem2 \
    dosfstools \
    ethtool \
    fbset \
    findutils \
    grep \
    i2c-tools \
    iperf3 \
    iproute2 \
    iptables \
    less \
    lsof \
    netcat-openbsd \
    nmap \
    ntp ntp-tickadj \
    procps \
    sysfsutils \
    tcpdump \
    unzip \
    util-linux \
    wget \
    zip \
"

WIFI_SUPPORT = " \
    crda \
    iw \
    wpa-supplicant \
"

EZLOML_STUFF = " \
    ezloml \ 
    seeta-face-engine-models \
"

SYSTEM_TUNING_SERVICES = " \
    resize-rootfs \
    udev-rules-custom \
    ksz9897-fix \
    sd2emmc \
    zram \    
    cpufreq-performance \
"

CORE_OS = " \
    kernel-modules \
    openssh openssh-keygen openssh-sftp-server \
    packagegroup-core-boot \
    packagegroup-core-x11-base \
    ca-certificates \
    ${SYSTEM_TUNING_SERVICES} \
"

