BMJ_VPR_RESIZE_ENABLE ?= "1"

do_compile:prepend() {
    if [ "${BMJ_VPR_RESIZE_ENABLE}" = "1" ] && [ "${SOC_FAMILY}" = "tegra194" ]; then
        bct_misc="${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra194-mb1-bct-misc-l4t.cfg"
        if [ -f "$bct_misc" ]; then
            if grep -q '^enable_vpr_resize *= *0' "$bct_misc"; then
                bbnote "BMJ: enabling VPR resize in $bct_misc to reclaim ~688 MiB"
                sed -i 's/^enable_vpr_resize *= *0;/enable_vpr_resize = 1;/' "$bct_misc"
            elif grep -q '^enable_vpr_resize *= *1' "$bct_misc"; then
                bbnote "BMJ: VPR resize already enabled in $bct_misc"
            else
                bbwarn "BMJ: enable_vpr_resize line not found in $bct_misc - skipping"
            fi
        else
            bbwarn "BMJ: $bct_misc not present - cannot enable VPR resize"
        fi
    fi
}

# Force a recompile if our toggle changes
do_compile[vardeps] += "BMJ_VPR_RESIZE_ENABLE"

