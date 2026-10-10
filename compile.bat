@echo off
title Compile FocusMatrix Java Backend
echo Compiling FocusMatrix Java source code...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.4.0.jar;src" -d bin src\com\focusmatrix\Main.java src\com\focusmatrix\model\*.java src\com\focusmatrix\exception\*.java src\com\focusmatrix\engine\*.java src\com\focusmatrix\database\*.java src\com\focusmatrix\web\*.java
if %errorlevel% equ 0 (
    echo [SUCCESS] All Java classes compiled cleanly to bin\ !
) else (
    echo [ERROR] Compilation failed.
)
pause
