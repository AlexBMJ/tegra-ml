SUMMARY = "Get up and running with Llama, Mistral, Gemma, and other LLMs"
HOMEPAGE = "https://ollama.com"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=a8abe7311c869aba169d640cf367a4af"

GO_IMPORT = "github.com/ollama/ollama"
SRC_URI = "git://${GO_IMPORT}.git;protocol=https;branch=main \
           file://0001-cmake-drop-cross-unsupported-RUNTIME_DEPENDENCIES.patch \
           file://ollama.service"
SRCREV = "57653b8e42d69ec35f68a59857bad4d0f07994a3"

CUDA_VERSION = "11.8"

inherit cmake cuda go-mod systemd useradd

CUDA_NATIVEDEPS = "cuda-compiler-11-8-native cuda-cudart-11-8-native"

DEPENDS += "cmake-native cuda-cudart-11-8 libcublas-11-8 cuda-nvcc-11-8 cuda-driver-11-8 gcc-for-nvcc"
RDEPENDS:${PN} += "cuda-cudart-11-8 libcublas-11-8 cuda-driver-11-8"

OECMAKE_SOURCEPATH = "${S}/src/${GO_IMPORT}"

EXTRA_OECMAKE += "-DGGML_CUDA=ON \
                  -DCMAKE_SKIP_BUILD_RPATH=ON \
                  -DCMAKE_BUILD_WITH_INSTALL_RPATH=ON"

# nvcc honours NVCC_PREPEND_FLAGS before all other args, so use it to give the
# compiler-id probe a --sysroot and the cuda-compat-workarounds shim.
export NVCC_PREPEND_FLAGS = "-Xcompiler --sysroot=${RECIPE_SYSROOT} -Xcompiler -isystem${RECIPE_SYSROOT}/usr/include/cuda-compat-workarounds --library-path ${RECIPE_SYSROOT}/usr/local/cuda-${CUDA_VERSION}/${baselib} --library-path ${RECIPE_SYSROOT}/usr/local/cuda-${CUDA_VERSION}/${baselib}/stubs"

# cuda.bbclass writes a bare `set(CMAKE_CUDA_COMPILER nvcc)`; give it an
# absolute path. Host compiler and --sysroot come from the class's exported
# CUDAHOSTCXX / CUDAFLAGS.
OECMAKE_CUDA_COMPILER = "${CUDA_TOOLKIT_ROOT}/bin/nvcc"

# Compose go's + cmake's configure (go-mod's EXPORT_FUNCTIONS overrides cmake's).
do_configure() {
    go_do_configure
    cmake_do_configure
}

export GOPROXY = "https://proxy.golang.org,direct"
do_compile[network] = "1"

do_compile() {
    cmake_runcmake_build

    cd ${OECMAKE_SOURCEPATH}
    export TMPDIR="${GOTMPDIR}"
    export CGO_ENABLED=1
    export CGO_LDFLAGS="${CGO_LDFLAGS} -L${B}/lib/ollama -Wl,-rpath,${libdir}/ollama"
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

# dev-so: dlopen()ed ggml plugins ship in the runtime package with their .so
# symlinks. already-stripped/textrel/buildpaths: unavoidable for nvcc output.
INSANE_SKIP:${PN} += "textrel already-stripped dev-so buildpaths"

# cuda-cudart/libcublas 11.4 and 11-8 both provide these SONAMEs; RDEPENDS
# already pins the 11-8 variants.
PRIVATE_LIBS:${PN} = "libcudart.so.11.0 libcublas.so.11 libcublasLt.so.11"

COMPATIBLE_MACHINE = "(tegra)"

