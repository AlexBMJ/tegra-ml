DESCRIPTION = "Tegra distro image with no display/window manager."

require image-common.inc

IMAGE_FEATURES += "hwcodecs"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CORE_IMAGE_BASE_INSTALL += " \
    packagegroup-machinelearning \
    packagegroup-core-buildessential \
"
