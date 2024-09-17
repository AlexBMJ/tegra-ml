DESCRIPTION = "Packagegroup for machine learning"

LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    cuda-nvcc-11-8 \
    cuda-nvcc-headers-11-8 \
    cuda-driver-11-8 \
    cuda-compat-11-8 \
    cuda-compatibility-workarounds \
    cuda-command-line-tools-11-8 \
    cuda-toolkit-11-8 \
    cuda-cccl-11-8 \
    cuda-compiler-11-8 \
    cuda-libraries-11-8 \
    cuda-cudart-11-8 \
    libcudla-11-8 \
    gcc-for-nvcc \
    gcc-for-nvcc-symlinks \
    g++-for-nvcc \
    g++-for-nvcc-symlinks \
    cpp-for-nvcc \
    cpp-for-nvcc-symlinks \
    make \
    binutils \
    binutils-symlinks \
    python3 \
    python3-dev \
    python3-pip \
    vim \
"
