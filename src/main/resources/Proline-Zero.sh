#!/bin/sh

chmod +x ProlineStudio-${studio.version}/bin/prolinestudio

# Retrieve the JDK packaged within Proline Studio and set execution permission to the java process
# TODO check that this path exists, if not try the local jdk/java_home if any
JAVA_HOME=ProlineStudio-${studio.version}/jdk
chmod +x ${JAVA_HOME}/bin/java

# Set execution permission to all PostgreSQL files
if [ -d pgsql ]; then
  chmod +x pgsql/bin/*
fi

# Start Proline Zero launcher
${JAVA_HOME}/bin/java -jar proline-zero-${pom.version}.jar

