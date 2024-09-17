SUMMARY = "Ollama is a tool for deploying large language models"
DESCRIPTION = "Get up and running with large language models"
HOMEPAGE = "https://github.com/ollama/ollama/"

GO_IMPORT = "github.com/ollama/ollama"
# S = "${WORKDIR}/${BPN}-${PV}/src/${GO_IMPORT}"
S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

GOPATHDIR = "${B}/src/${GO_IMPORT}"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8abe7311c869aba169d640cf367a4af"

DEPENDS += " \
    llama.cpp \
    git \
    bash \
    gcc-for-nvcc \
    cuda-nvcc-11-8 \
    cuda-nvcc-headers-11-8 \
    cuda-driver-11-8 \
    cuda-compat-11-8 \
    cuda-compatibility-workarounds \
"
RDEPENDS:${PN} += " \
    git \
    bash \
    gcc-for-nvcc \
    cuda-nvcc-11-8 \
    cuda-nvcc-headers-11-8 \
    cuda-driver-11-8 \
    cuda-compat-11-8 \
    cuda-compatibility-workarounds \
    cuda-command-line-tools-11-8 \
"

inherit go go-mod

do_configure[network] = "1"
do_compile[network] = "1"
do_install[network] = "1"

SRC_URI = "gitsm://${GO_IMPORT}.git;nobranch=1;protocol=https"
SRCREV = "v${PV}"
UPSTREAM_CHECK_COMMITS = "1"

# SRC_URI += " \
#     file://disable-git.patch \
#     file://disable-lib-check.patch \
# "

export CGO_ENABLED = "1"

# GO_LINKSHARED = ""

BBCLASSEXTEND = "native"
# GO_EXTLDFLAGS = "-static"

do_configure:prepend () {
    # Remove all the src present in build if it is not a symbolic link to ${S}
    if [ -d ${B}/src ]; then
        rm -rf ${B}/src
    fi
}

do_configure:append () {
    # Remove the symbolic link created by go.bbclass in do_configure. This is to
    # make sure that the build environment ${B} does not touch ${S} in any way.
    if [ -h ${B}/src ]; then
        rm ${B}/src
    fi
    mkdir -p $(dirname ${B}/src/${GO_IMPORT})
    cp --archive ${S} ${B}/src/${GO_IMPORT}
}

do_compile:prepend() {
    cd ${B}/src/${GO_IMPORT}
    go generate ./...
}