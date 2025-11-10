# ================================
# Schulaufgabe Temperatur-Messer
# Startet Sensor + Display in CMD-Fenstern
# ================================

# Konfiguration
$PortSensor  = 'COM2'
$PortDisplay = 'COM3'
$Baud        = 9600
$Interval    = 1000

# Arbeitsverzeichnis (Root-Projekt)
$Root = Split-Path -Parent $MyInvocation.MyCommand.Definition
Push-Location $Root

Write-Host '🔧 Baue Projekt mit Gradle...'
& "$Root\gradlew.bat" :sensor:jar :display:jar

# Pfade zu den JARs
$JarSensor  = Join-Path $Root 'sensor\build\libs\temperature-sensor.jar'
$JarDisplay = Join-Path $Root 'display\build\libs\temperature-display.jar'

# CMD-Befehle vorbereiten
$CmdSensor  = "/k title Sensor && java -jar `"$JarSensor`""
$CmdDisplay = "/k title Display && java -jar `"$JarDisplay`""

# Start Sensor
Write-Host "🚀 Starte Sensor ($PortSensor @ $Baud Baud, Intervall $Interval ms)..."
$procSensor = Start-Process -FilePath "cmd.exe" -ArgumentList $CmdSensor -WorkingDirectory $Root -PassThru

Start-Sleep -Seconds 1

# Start Display
Write-Host "🚀 Starte Display ($PortDisplay @ $Baud Baud)..."
$procDisplay = Start-Process -FilePath "cmd.exe" -ArgumentList $CmdDisplay -WorkingDirectory $Root -PassThru

Write-Host "✅ Beide Terminals wurden gestartet."

# Optional: Warten, bis beide beendet sind
# Wait-Process -Id $procSensor.Id, $procDisplay.Id
