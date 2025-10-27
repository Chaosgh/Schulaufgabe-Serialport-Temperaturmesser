@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%runAll.ps1"
if errorlevel 1 (
    echo.
    echo PowerShell-Skript ist fehlgeschlagen. Details siehe oben.
    pause
)

endlocal
