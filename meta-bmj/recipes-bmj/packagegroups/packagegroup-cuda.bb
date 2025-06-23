DESCRIPTION = "Packagegroup for tegra cuda packages"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    cuda-driver-11-8 \
    g++-for-nvcc \
    cpp-for-nvcc \
"
