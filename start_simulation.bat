@echo off
title Temperature System Launcher
color 0f

echo ==========================================
echo   Temperatur-Messsystem Simulator
echo ==========================================
echo.
echo 1. Baue Projekt...
call gradlew.bat shadowJar
if %ERRORLEVEL% NEQ 0 (
    echo Build fehlgeschlagen!
    pause
    exit /b
)
echo Build erfolgreich!
echo.

echo 2. Starte Sensor (COM4)...
start "SENSOR (COM4)" cmd /c "color 4f & mode con: cols=60 lines=20 & echo SENSOR LÄUFT... & java -jar sensor/build/libs/temperature-sensor.jar COM4 & pause"

echo Warte 2 Sekunden...
timeout /t 2 /nobreak >nul

echo 3. Starte Display (COM3)...
start "DISPLAY (COM3)" cmd /c "color 1f & mode con: cols=80 lines=30 & echo DISPLAY LÄUFT... & java -jar display/build/libs/temperature-display.jar COM3 & pause"

echo.
echo ==========================================
echo   Simulation läuft!
echo   (Fenster schließen zum Beenden)
echo ==========================================
pause
