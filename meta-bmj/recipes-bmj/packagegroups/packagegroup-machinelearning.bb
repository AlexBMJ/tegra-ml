DESCRIPTION = "Packagegroup for machine learning"

LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    cuda-nvcc-11-8 \
    cuda-toolkit-11-8 \
    cuda-cccl-11-8 \
    cuda-compiler-11-8 \
    cuda-libraries-11-8 \
    cuda-driver-11-8 \
    cuda-nvcc-headers-11-8 \
    gcc-for-nvcc \
    binutils \
    python3 \
    python3-dev \
    python3-pip \
    vim \
"
