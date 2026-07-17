SUMMARY = "Get up and running with Llama, Mistral, Gemma, and other LLMs"
HOMEPAGE = "https://ollama.com"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=a8abe7311c869aba169d640cf367a4af"

GO_IMPORT = "github.com/ollama/ollama"
SRC_URI = "git://${GO_IMPORT}.git;protocol=https;branch=main;name=ollama \
           git://github.com/ggml-org/llama.cpp.git;protocol=https;nobranch=1;name=llamacpp;destsuffix=${BP}/llama.cpp \
           file://0001-cmake-cross-compile-support-for-nested-llama-server.patch \
           file://ollama.service"
# v0.32.1
SRCREV_ollama = "30c390384e20333b67cadab60da5bcb669407f01"
# llama.cpp tag b9888; keep in sync with LLAMA_CPP_VERSION in the ollama tree
SRCREV_llamacpp = "cb295bf59663cd3577389315636772f4060bd1f5"
SRCREV_FORMAT = "ollama_llamacpp"

CUDA_VERSION = "11.8"

inherit cmake cuda go-mod systemd useradd

CUDA_NATIVEDEPS = "cuda-compiler-11-8-native cuda-cudart-11-8-native"

DEPENDS += "cmake-native cuda-cudart-11-8 libcublas-11-8 cuda-nvcc-11-8 cuda-driver-11-8 gcc-for-nvcc"
RDEPENDS:${PN} += "cuda-cudart-11-8 libcublas-11-8 cuda-driver-11-8"

OECMAKE_SOURCEPATH = "${S}/src/${GO_IMPORT}"

# GGML_CPU_ALL_VARIANTS includes armv9+sme, unbuildable with gcc 13; build a
# single SoC-tuned variant instead.
GGML_CPU_ARM_ARCH ?= "armv8.2-a+fp16"
EXTRA_OECMAKE += "-DOLLAMA_LLAMA_BACKENDS=cuda_jetpack5 \
                  -DFETCHCONTENT_SOURCE_DIR_LLAMA_CPP=${S}/llama.cpp \
                  -DGGML_CPU_ALL_VARIANTS=OFF \
                  -DGGML_CPU_ARM_ARCH=${GGML_CPU_ARM_ARCH} \
                  -DCMAKE_SKIP_BUILD_RPATH=ON \
                  -DCMAKE_BUILD_WITH_INSTALL_RPATH=ON"

# Skip the superbuild's ollama-go target; the Go binary is built in do_compile.
OECMAKE_TARGET_COMPILE = "ollama-llama-server-local ollama-llama-server-cuda_jetpack5"

# nvcc's compiler-id probe needs a --sysroot and the cuda-compat-workarounds shim.
export NVCC_PREPEND_FLAGS = "-Xcompiler --sysroot=${RECIPE_SYSROOT} -Xcompiler -isystem${RECIPE_SYSROOT}/usr/include/cuda-compat-workarounds --library-path ${RECIPE_SYSROOT}/usr/local/cuda-${CUDA_VERSION}/${baselib} --library-path ${RECIPE_SYSROOT}/usr/local/cuda-${CUDA_VERSION}/${baselib}/stubs"

# cuda.bbclass writes a bare `set(CMAKE_CUDA_COMPILER nvcc)`.
OECMAKE_CUDA_COMPILER = "${CUDA_TOOLKIT_ROOT}/bin/nvcc"

GO_EXTRA_LDFLAGS:append = " -X=${GO_IMPORT}/version.Version=${PV}"

# go-mod's EXPORT_FUNCTIONS overrides cmake's do_configure; compose both.
do_configure() {
    # The superbuild skips its llama.cpp compat hooks for source overrides;
    # apply them here (idempotent). Subshell: cmake_do_configure needs cwd=${B}.
    (
        cd ${S}/llama.cpp
        cmake -DPATCH_DIR=${OECMAKE_SOURCEPATH}/llama/compat \
              -P ${OECMAKE_SOURCEPATH}/llama/compat/apply-patch.cmake
    )

    go_do_configure
    cmake_do_configure
}

do_compile[network] = "1"

# Keep go module caches writable so cleandirs can remove ${B}.
export GOFLAGS = "-modcacherw"

do_compile() {
    cmake_runcmake_build --target ${OECMAKE_TARGET_COMPILE}

    cd ${OECMAKE_SOURCEPATH}
    export TMPDIR="${GOTMPDIR}"
    ${GO} build ${GOBUILDFLAGS} -o ${B}/ollama .
}

do_install() {
    install -d ${D}${libdir}/ollama
    cp -a ${B}/lib/ollama/. ${D}${libdir}/ollama/
    chown -R root:root ${D}${libdir}/ollama

    install -d ${D}${bindir}
    install -m 0755 ${B}/ollama ${D}${bindir}/ollama

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ollama.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${libdir}/ollama \
                ${systemd_system_unitdir}/ollama.service"

SYSTEMD_SERVICE:${PN} = "ollama.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

USERADD_PACKAGES = "${PN}"
# No -m: /var/lib/ollama is created by StateDirectory=ollama in the unit.
USERADD_PARAM:${PN}  = "-r -s /bin/false -d /var/lib/ollama -g ollama -G video ollama"
GROUPADD_PARAM:${PN} = "-r ollama"

# dev-so: dlopen()ed ggml plugins; the rest is unavoidable for nvcc output.
INSANE_SKIP:${PN} += "textrel already-stripped dev-so buildpaths"

# cuda 11.4 and 11-8 packages both provide these SONAMEs; RDEPENDS pins 11-8.
PRIVATE_LIBS:${PN} = "libcudart.so.11.0 libcublas.so.11 libcublasLt.so.11"

COMPATIBLE_MACHINE = "(tegra)"
