@echo off
setlocal EnableExtensions EnableDelayedExpansion
title Proline
call start-server.bat 
@if ERRORLEVEL 1 exit /b %ERRORLEVEL%

prolinestudio\bin\prolinestudio.exe --console suppress

echo Stopping Proline server 
call stop-server.bat

endlocal
exit %ERRORLEVEL%
