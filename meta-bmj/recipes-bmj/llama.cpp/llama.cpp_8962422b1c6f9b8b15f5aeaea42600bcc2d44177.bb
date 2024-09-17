SUMMARY = "LLM inference in C/C++"
DESCRIPTION = "The main goal of llama.cpp is to enable LLM inference with minimal setup and state-of-the-art performance on a wide variety of hardware - locally and in the cloud"
HOMEPAGE = "github.com/ggerganov/llama.cpp"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1539dadbedb60aa18519febfeab70632"

DEPENDS += " \
    gcc-for-nvcc \
    cuda-nvcc-11-8 \
    cuda-nvcc-headers-11-8 \
    cuda-driver-11-8 \
    cuda-compat-11-8 \
    cuda-compatibility-workarounds \
    cuda-cccl-11-8 \
    cuda-compiler-11-8 \
    cuda-libraries-11-8 \
    cuda-cudart-11-8 \
    libcublas-11-8 \
"
RDEPENDS:${PN} += " \
    gcc-for-nvcc \
    cuda-nvcc-11-8 \
    cuda-nvcc-headers-11-8 \
    cuda-driver-11-8 \
    cuda-compat-11-8 \
    cuda-compatibility-workarounds \
    cuda-cccl-11-8 \
    cuda-compiler-11-8 \
    cuda-libraries-11-8 \
    cuda-cudart-11-8 \
    libcublas-11-8 \
"

inherit cmake

SRC_URI = "git://github.com/ggerganov/llama.cpp.git;nobranch=1;protocol=https"
SRC_URI += " \
    file://patches \
"

SRCREV = "${PV}"
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

CUDACXX = "${STAGING_BINDIR_NATIVE}/nvcc"

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=off -DCMAKE_POSITION_INDEPENDENT_CODE=on -DGGML_NATIVE=off -DGGML_AVX=on -DGGML_AVX2=off -DGGML_AVX512=off -DGGML_FMA=off -DGGML_F16C=off -DGGML_OPENMP=off"
EXTRA_OEMAKE = "ollama_llama_server"