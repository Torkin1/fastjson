#!/usr/bin/env sh

MVN_LOCAL_REPO=$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)

java -jar "$MVN_LOCAL_REPO/br/usp/each/saeg/ba-dua-cli/0.6.0/ba-dua-cli-0.6.0-all.jar" report -classes target/test-classes/ -xml target/report.xml -input target/badua.ser -show-classes -show-methods