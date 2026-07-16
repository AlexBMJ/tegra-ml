DESCRIPTION = "Core packages for basic functionality"

LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    networkmanager \
    tmux \
    vim \
    less \
    htop \
    btop \
    binutils \
    binutils-symlinks \
"

