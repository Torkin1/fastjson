#!/usr/bin/env sh

jdk="openlogic-openjdk-8u332-b09-linux-x64"
jdkArchive="$jdk.tar.gz"
from="https://builds.openlogic.com/downloadJDK/openlogic-openjdk/8u332-b09/"

wget $from$jdkArchive -O $jdkArchive
tar -v -xf $jdkArchive
rm $jdkArchive
echo -n "$PWD/$jdk" > java_home