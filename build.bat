@echo off
setlocal

cd /d "%~dp0"

if exist out (
    rmdir /s /q out
)

mkdir out

javac -d out src\Files\*.java
if errorlevel 1 (
    echo.
    echo Error: no se pudo compilar el proyecto.
    exit /b 1
)

echo.
echo Compilacion completada correctamente.
exit /b 0
