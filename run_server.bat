@echo off
title FocusMatrix Java Web Server
echo ====================================================================
echo    FOCUSMATRIX: TIME MANAGEMENT & TASK PRIORITIZATION SYSTEM
echo             Galgotias University SCSE Project Review
echo ====================================================================
echo.
echo Starting FocusMatrix Java Web Server on http://localhost:8080 ...
java -cp "lib\mysql-connector-j-8.4.0.jar;bin" com.focusmatrix.Main
pause
