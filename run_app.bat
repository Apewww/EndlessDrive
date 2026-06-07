@echo off
if exist bin (
    java -cp bin EndlessDriveGame
) else (
    echo bin folder not found
    pause
)