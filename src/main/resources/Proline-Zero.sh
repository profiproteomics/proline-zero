#!/bin/sh

# Retrieve Proline Studio version and set execution permission to the main script
STUDIO_VERSION=`grep studio_version proline_launcher.config |sed 's/.*=\s*//' |tr -d '\r\n'`
chmod +x ProlineStudio-${STUDIO_VERSION}/bin/prolinestudio

# Retrieve the JDK packaged within Proline Studio and set execution permission to the java process
# TODO check that this path exists, if not try the local jdk/java_home if any
JAVA_HOME=ProlineStudio-${STUDIO_VERSION}/jdk
chmod +x ${JAVA_HOME}/bin/java

# Set execution permission to all PostgreSQL files
if [ -d pgsql ]; then
  chmod +x pgsql/bin/*
fi

# Start Proline Zero launcher
${JAVA_HOME}/bin/java -jar proline-zero-${pom.version}.jar

