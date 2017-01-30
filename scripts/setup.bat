@echo off
setlocal EnableExtensions EnableDelayedExpansion
title Proline Setup

start "h2" java -cp "Proline-Cortex-${cortex.version}/lib/*" org.h2.tools.Console -tcp -pg
@if ERRORLEVEL 1 exit /b %ERRORLEVEL%

pushd Proline-Cortex-${cortex.version}
java -Xmx1024m -cp "lib/*;config" fr.proline.admin.RunCommand setup
java -Xmx1024m -cp "lib/*;config" fr.proline.admin.RunCommand create_user -l proline -p proline
java -Xmx1024m -cp "lib/*;config" fr.proline.admin.RunCommand create_project -oid 1 -n "Proline_Project" -desc "Proline default Project"
popd

echo init done, stopping H2 database
javaw -cp "Proline-Cortex-${cortex.version}/lib/*" org.h2.tools.Server -tcpShutdown tcp://localhost:9092
timeout /t 3 /nobreak  >nul 2>&1

endlocal
exit /b 0