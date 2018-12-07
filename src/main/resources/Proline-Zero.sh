#!/bin/sh

# Retrieve Proline Studio version and set execution permission to the main script
STUDIO_VERSION=`grep studio_version proline_launcher.config |sed 's/.*=\s*//' |tr -d '\r\n'`
chmod +x ProlineStudio-${STUDIO_VERSION}/bin/prolinestudio

# Retrieve the JRE packaged within Proline Studio and set execution permission to the java process
# TODO check that this path exists, if not try the local jre/java_home if any
JAVA_HOME=ProlineStudio-${STUDIO_VERSION}/jre
chmod +x ${JAVA_HOME}/bin/java

# Set execution permission to all PostgreSQL files
if [ -d pgsql ]; then
  chmod +x pgsql/bin/*
fi

# Start Proline Zero launcher
${JAVA_HOME}/bin/java -jar Proline-Zero-1.6.0-javalauncher.jar

