@echo off
REM Build script for EndlessDrive - compiles Java sources and creates executable JAR

echo ============================================
echo Building EndlessDrive...
echo ============================================

REM Clean previous build
if exist bin (
    rmdir /s /q bin
)
if exist EndlessDrive.jar (
    del EndlessDrive.jar
)

REM Create output directories
mkdir bin
mkdir bin\assets

echo Compiling Java source files...
javac -d bin -sourcepath src src\*.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation FAILED!
    pause
    exit /b 1
)

echo.
echo Copying assets...
xcopy assets bin\assets /E /I /Q /Y >nul

echo.
echo Creating executable JAR...
jar cfe EndlessDrive.jar EndlessDriveGame -C bin .

if %errorlevel% neq 0 (
    echo.
    echo JAR creation FAILED!
    pause
    exit /b 1
)

echo.
echo ============================================
echo Build SUCCESSFUL!
echo ============================================
echo.
echo Output: EndlessDrive.jar
echo Assets copied to: bin\assets\
echo.
echo To run: java -jar EndlessDrive.jar
echo Or use run.bat
echo.
pause