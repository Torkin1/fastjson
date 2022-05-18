#!/usr/bin/env sh

WORKDIR=$(mktemp -d)
BADUA_RELEASE="ba-dua-0.6.0"
BADUA_FROM="https://github.com/saeg/ba-dua/archive/refs/tags/$BADUA_RELEASE.zip"
toUnzip="$BADUA_RELEASE.zip"

cd "$WORKDIR" || exit 
wget $BADUA_FROM -O $toUnzip
unzip $toUnzip
cd "ba-dua-$BADUA_RELEASE" || exit
mvn clean install
