@echo off
javaw -jar Google2SRT.jar >nul 2>&1

if %errorlevel% == 0 goto exit
start /min Google2SRT.jar

:exit
