DESCRIPTION = "Packagegroup for development"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN}:append = " \
    make \
    cmake \
    go-dev \
    binutils \
    binutils-symlinks \
    python3 \
    python3-dev \
    python3-pip \
    apt \
    git \
    tmux \
    vim \
    less \
    systemd-analyze \
"
