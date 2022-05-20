#!/usr/bin/env sh

# $1: path to compiled test classes to instrument
# $2: where to save report

MVN_LOCAL_REPO=$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
BADUA_CLI_PATH="$MVN_LOCAL_REPO/br/usp/each/saeg/ba-dua-cli/0.6.0/ba-dua-cli-0.6.0-all.jar"

java -jar "$BADUA_CLI_PATH" report -classes "$1" -xml "$2" -input target/badua.ser -show-classes -show-methods