@echo off
setlocal EnableExtensions EnableDelayedExpansion
title Proline

IF EXIST pid.txt (echo Server already running ? Stop server before starting a new instance or delete pid.txt file && exit \b 1)

REM Test if database folder is empty 
( dir /b /a ".\data\databases" | findstr . ) > nul && (
 echo Proline databases found in .\data\databases, start Proline server ...
) || (
 echo databases is empty : run setup before starting server, please wait ...
 start /WAIT cmd /c setup.bat
 @if ERRORLEVEL 1 exit /b %ERRORLEVEL%
)

REM Start H2 database and store H2 PID in pid.txt file

set "PROCESSNAME="java -cp "Proline-Cortex-${cortex.version}/lib/*" org.h2.tools.Console -tcp -pg""
call:startProcess "H2" %PROCESSNAME% JAVAPID 
@if ERRORLEVEL 1 exit /b %ERRORLEVEL%
echo H2 process started with PID %JAVAPID%
echo H2=%JAVAPID% >> pid.txt

REM Start HornetQ database and store HORNETQ PID in pid.txt file
pushd Proline-Cortex-${cortex.version}\hornetq_light-${hornetq.version}\bin
set "PROCESSNAME="run.bat""
call:startProcess "HornetQ" %PROCESSNAME% JAVAPID
@if ERRORLEVEL 1 exit /b %ERRORLEVEL%
echo HORNETQ process started with PID %JAVAPID%
popd
echo HORNETQ=%JAVAPID% >> pid.txt

REM Start Cortex and store CORTEX PID in pid.txt file
timeout /t 5 /nobreak >nul 2>&1
pushd Proline-Cortex-${cortex.version}
set "PROCESSNAME="start_cortex.bat""
call:startProcess "Cortex" %PROCESSNAME% JAVAPID
@if errorlevel 1 exit /b %ERRORLEVEL%
echo PROLINE CORTEX process started with PID %JAVAPID%
popd
echo CORTEX=%JAVAPID% >> pid.txt
timeout /t 5 /nobreak >nul 2>&1

echo Proline Server successfully started ...

endlocal
exit /b 0


:startProcess
setlocal EnableExtensions EnableDelayedExpansion
set PROCESSNAME=%~2

set "RETJAVAPIDS="
set "OLDJAVAPIDS=p"

for /f "TOKENS=1" %%a in ('wmic PROCESS where "Name like 'java%%.exe'" get ProcessID ^| findstr [0-9]') do (set "OLDJAVAPIDS=!OLDJAVAPIDS!%%ap")

::Spawn new process(es)

start %1 /min cmd /c %PROCESSNAME%

::Check and find processes missing in the old pid list
for /f "TOKENS=1" %%a in ('wmic PROCESS where "Name like 'java%%.exe'" get ProcessID ^| findstr [0-9]') do (
	if "!OLDJAVAPIDS:p%%ap=zz!"=="%OLDJAVAPIDS%" (set "RETJAVAPIDS=%%a !RETJAVAPIDS!")
)

endlocal & set %~3=%RETJAVAPIDS%
goto :eof