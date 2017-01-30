@echo off
setlocal EnableExtensions EnableDelayedExpansion

for /F "tokens=1,2* delims==" %%i in (pid.txt) do (
	if $%%i$ == $CORTEX$ set CORTEXPID=%%j
)

REM Kill Cortex process
echo Stopping Cortex PID %CORTEXPID%
taskkill /pid %CORTEXPID% /F /T >NUL 2>&1

REM Stop HornetQ daemon  
echo Stopping HornetQ
pushd Proline-Cortex-${cortex.version}\hornetq_light-${hornetq.version}\bin
call stop.bat
popd

REM Stop H2 database and store H2 PID in pid.txt file
echo Stopping H2 database
set PROCESSNAME=javaw -cp "Proline-Cortex-${cortex.version}/lib/*" org.h2.tools.Server -tcpShutdown tcp://localhost:9092
start /B %PROCESSNAME% 

REM timeout /t 3 /nobreak
REM ::Kill the new threads (but no other)
REM taskkill %RETPIDS% /T > NUL 2>&1

REM Finally remove the pid file 
IF EXIST pid.txt del /F pid.txt

timeout /t 5 /nobreak  >nul 2>&1
echo Proline server stopped successfully 

endlocal
