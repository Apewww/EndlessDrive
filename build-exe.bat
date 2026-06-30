@echo off
setlocal enabledelayedexpansion

echo ============================================
echo Building EndlessDrive Executable (.exe)
echo ============================================

set APP_NAME=EndlessDrive
set MAIN_CLASS=EndlessDriveGame
set JAR_NAME=%APP_NAME%.jar
set VERSION=1.0.0
set VENDOR=EndlessDrive Team
set PROJECT_DIR=%~dp0
if "%PROJECT_DIR:~-1%"=="\" set PROJECT_DIR=%PROJECT_DIR:~0,-1%
set STAGING_DIR=%TEMP%\endlessdrive-staging
set OUTPUT_DIR=%PROJECT_DIR%\dist

if exist "%STAGING_DIR%" rmdir /s /q "%STAGING_DIR%"
if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"

mkdir "%STAGING_DIR%"
mkdir "%STAGING_DIR%\app"

echo.
echo [1/4] Compiling Java source files...
dir /s /b "%PROJECT_DIR%\src\*.java" > "%STAGING_DIR%\sources.txt"
javac -d "%STAGING_DIR%\app" -sourcepath "%PROJECT_DIR%\src" "@%STAGING_DIR%\sources.txt"
if %errorlevel% neq 0 ( echo Compilation FAILED! & pause & exit /b 1 )
echo       Done.

echo.
echo [2/4] Creating JAR...
jar cfe "%STAGING_DIR%\app\%JAR_NAME%" %MAIN_CLASS% -C "%STAGING_DIR%\app" .
if %errorlevel% neq 0 ( echo JAR creation FAILED! & pause & exit /b 1 )
echo       Done.

echo.
echo [3/4] Copying assets to staging...
xcopy "%PROJECT_DIR%\assets" "%STAGING_DIR%\assets" /E /I /Q /Y >nul
if %errorlevel% neq 0 ( echo Asset copy FAILED! & pause & exit /b 1 )
echo       Done.

echo.
echo [4/4] Generating Windows executable with jpackage...
echo.

jpackage ^
    --type app-image ^
    --input "%STAGING_DIR%\app" ^
    --main-jar "%JAR_NAME%" ^
    --main-class %MAIN_CLASS% ^
    --name "%APP_NAME%" ^
    --app-version %VERSION% ^
    --vendor "%VENDOR%" ^
    --description "Endless Drive - 2D Neon Endless Runner Game" ^
    --copyright "Copyright 2026 EndlessDrive Team" ^
    --dest "%OUTPUT_DIR%" ^
    --app-content "%STAGING_DIR%\assets" ^
    --win-console ^
    --verbose

if %errorlevel% neq 0 (
    echo.
    echo jpackage FAILED! See error details above.
    pause
    exit /b 1
)

echo.
echo ============================================
echo Build EXE SUCCESSFUL!
echo ============================================
echo.
echo Output: %OUTPUT_DIR%\%APP_NAME%\
echo.
echo To run the game:
echo   %OUTPUT_DIR%\%APP_NAME%\%APP_NAME%.exe
echo.
pause