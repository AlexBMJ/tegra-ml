#!/bin/bash
if [ -n "$BASH_SOURCE" ]; then
    THIS_SCRIPT="$BASH_SOURCE"
elif [ -n "$ZSH_NAME" ]; then
    THIS_SCRIPT="$0"
fi

if [ "$THIS_SCRIPT" != "$0" ]; then
    SOURCING=true
fi

declare -a ARGS
for var in "$@"; do
    ARGS[${#ARGS[@]}]="$var"
    shift
done

TOP_DIR=$(dirname $THIS_SCRIPT)
TOP_DIR=$(readlink -f "$TOP_DIR")
POKYDIR="$TOP_DIR/poky"
BITBAKEDIR="$POKYDIR/bitbake"
. $TOP_DIR/.templateconf
export BITBAKEDIR TEMPLATECONF

. $POKYDIR/oe-init-build-env

if [ "$SOURCING" != true ]; then
    if [ "${ARGS[0]}" == "-h" ] || [ "${ARGS[0]}" == "--help" ]; then
        printf "Usage: $0 -s [command]\n"
        exit 0
    fi

    declare -a COMMAND
    for i in "${!ARGS[@]}"; do
        if [[ "${ARGS[$i]}" = "-s" ]]; then
            COMMAND="${ARGS[@]:$i+1}"
        fi
    done

    if [ -z "${COMMAND[0]}" ]; then
        bash
        exit 0
    fi
    printf "Running command: ${COMMAND[@]}\n"
    ${COMMAND[@]}
fi

