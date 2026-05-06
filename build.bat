@echo off
setlocal

cd /d "%~dp0"

if exist out (
    rmdir /s /q out
)

mkdir out

javac -d out src\bulletdrift\Main.java src\bulletdrift\core\*.java src\bulletdrift\entities\*.java src\bulletdrift\rendering\*.java src\bulletdrift\spawning\*.java src\bulletdrift\systems\*.java
if errorlevel 1 (
    echo.
    echo Error: no se pudo compilar el proyecto.
    exit /b 1
)

echo.
echo Compilacion completada correctamente.
exit /b 0
