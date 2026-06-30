@echo off
REM Run script for EndlessDrive executable JAR

echo Starting EndlessDrive...

if not exist EndlessDrive.jar (
    echo Error: EndlessDrive.jar not found!
    echo Please run build.bat first to build the project.
    pause
    exit /b 1
)

if not exist assets (
    echo Warning: assets folder not found next to JAR.
    echo Game may not display images correctly.
    echo.
)

java -jar EndlessDrive.jar

if %errorlevel% neq 0 (
    echo.
    echo Game exited with error code %errorlevel%
    pause
)