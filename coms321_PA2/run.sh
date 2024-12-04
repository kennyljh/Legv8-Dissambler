#!/bin/sh

if [ -n "$WINDIR" ]; then
    # Windows
    dir="$(cd "$(dirname "$0")" && pwd -W)"
else
    # Unix
    dir="$(dirname "$(realpath "$0")")"
fi

cd "$dir/output"
java Disassembler "$dir/$1"