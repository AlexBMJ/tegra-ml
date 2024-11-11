DESCRIPTION = "Packagegroup for tegra cuda packages"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    cuda-nvcc-11-8-dev \
    cuda-nvcc-headers-11-8-dev \
    cuda-compiler-11-8-dev \
    cuda-libraries-11-8-dev \
    cuda-cccl-11-8-dev \
    cuda-cudart-11-8-dev \
    libcudla-11-8-dev \
    cuda-driver-11-8 \
    cuda-compat-11-8 \
    cuda-compatibility-workarounds \
    gcc-for-nvcc-dev \
    g++-for-nvcc \
    cpp-for-nvcc \
"
