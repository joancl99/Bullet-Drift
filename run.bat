@echo off
setlocal

cd /d "%~dp0"

if not exist out\Files\Main.class (
    call build.bat
    if errorlevel 1 exit /b 1
)

java -cp out Files.Main
exit /b %errorlevel%
