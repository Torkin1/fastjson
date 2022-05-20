#!/usr/bin/env sh

# $1: ba-dua release

WORKDIR=$(mktemp -d)
BADUA_RELEASE="ba-dua-$1"
BADUA_FROM="https://github.com/saeg/ba-dua/archive/refs/tags/$BADUA_RELEASE.zip"
toUnzip="$BADUA_RELEASE.zip"

cd "$WORKDIR" || exit 
wget "$BADUA_FROM" -O "$toUnzip"
unzip "$toUnzip"
cd "ba-dua-$BADUA_RELEASE" || exit
mvn clean install
