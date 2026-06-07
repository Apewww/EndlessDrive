@echo off
:: Create compilation output folder if it doesn't exist
if not exist bin (
    mkdir bin
)

echo Compiling Java source files to 'bin' folder...
javac -d bin src/*.java

:: If compilation is successful, run the game
if %errorlevel% equ 0 (
    echo Running Endless Drive...
    java -cp bin EndlessDriveGame
) else (
    echo Compilation failed.
    pause
)
