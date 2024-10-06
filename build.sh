#!/bin/bash
if [ -n "$BASH_SOURCE" ]; then
    THIS_SCRIPT=$BASH_SOURCE
elif [ -n "$ZSH_NAME" ]; then
    THIS_SCRIPT=$0
else
    echo "Error: Unable to determine the script path" >&2
    exit 1
fi

declare -a ARGS
for var in "$@"; do
    ARGS[${#ARGS[@]}]="$var"
    shift
done

TOP_DIR=$(dirname $THIS_SCRIPT)

DISTRO=bmj
BITBAKEDIR="$TOP_DIR/poky/bitbake"
export DISTRO BITBAKEDIR

. $TOP_DIR/oe-init-build-env

if [ -z "${ARGS[0]}" ]; then
    bash
    exit 0
fi

${ARGS[@]}
