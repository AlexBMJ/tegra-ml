FILES:${PN}:append = " ${prefix}/local/cuda "

do_install:append() {
    ln -sf ${prefix}/local/cuda-${CUDA_VERSION} ${D}/${prefix}/local/cuda
}
