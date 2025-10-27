 = 'COM2'
 = 'COM3'
 = 9600
 = 1000

Continue = 'Stop'

 = Split-Path -Parent System.Management.Automation.InvocationInfo.MyCommand.Path
Push-Location 

Write-Host 'Baue Projekt mit Gradle...'
& "\gradlew.bat" :sensor:jar :display:jar

 = Join-Path  'sensor\build\libs\temperature-sensor.jar'
 = Join-Path  'display\build\libs\temperature-display.jar'

Write-Host "Starte Sensor (Port  @  Baud, Intervall ms)..."
 = Start-Process -FilePath 'cmd.exe' -ArgumentList '/k', ("title Sensor & java -jar "{0}"" -f ) -WorkingDirectory 

Start-Sleep -Seconds 1

Write-Host "Starte Display (Port  @  Baud)..."
 = Start-Process -FilePath 'cmd.exe' -ArgumentList '/k', ("title Display & java -jar "{0}"" -f ) -WorkingDirectory 

Write-Host 'Beide Terminals wurden gestartet.'
