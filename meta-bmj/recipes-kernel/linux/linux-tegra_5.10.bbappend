FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:tegra194 = " \
    file://0001-disable-tegra-carveouts.patch \
    file://disable-spectre-bhb.cfg \
"

KERNEL_ARGS += " nospectre_bhb"

