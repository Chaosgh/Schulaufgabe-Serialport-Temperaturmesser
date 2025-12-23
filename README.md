# Schulaufgabe: Serialport Temperaturmesser

Ein verteiltes System zur Simulation, Anzeige und Protokollierung von Temperaturdaten via serieller Schnittstelle (RS232).

## Übersicht

Das Projekt besteht aus drei Modulen:
1.  **Sensor**: Simuliert einen Temperatursensor. Erzeugt Messwerte, formatiert diese und sendet sie über einen seriellen Port (Default: COM4).
2.  **Display**: Empfängt die Daten (Default: COM3), zeigt sie an, prüft auf Anomalien (Hitze > 30 / Frost) und speichert diese in MySQL.
3.  **Common**: Gemeinsam genutzter Code.

## Voraussetzungen

*   **Java JDK 21**
*   **MySQL Server** (z.B. via XAMPP)
*   **Virtuelle Serielle Ports** (z.B. com0com, Paarung COM3 <-> COM4 benötigt)

## Build Anleitung

Das Projekt nutzt Gradle. Zum Erstellen der ausführbaren JAR-Dateien:

**Windows:**
```powershell
./gradlew shadowJar
```

Dateien liegen anschließend in:
*   `sensor/build/libs/temperature-sensor.jar`
*   `display/build/libs/temperature-display.jar`

## Starten

Es gibt zwei Wege das System zu starten:

### 1. Via Batch-Skript (Automatisch)
Für eine schnelle Simulation beider Komponenten:
Starten Sie die Datei `start_simulation.bat`.

### 2. Manuell via Konsole

**Schritt 1: Display starten**
```powershell
java -jar display/build/libs/temperature-display.jar COM3
```
*   Das Programm fragt nach einer JDBC-URL.
*   ENTER drücken für Localhost-Default.
*   Datenbank `temperaturdb` und Tabelle `anomalies` werden bei Bedarf automatisch erstellt.

**Schritt 2: Sensor starten**
```powershell
java -jar sensor/build/libs/temperature-sensor.jar COM4
```

## COM Ports öffnen

Da hier nur die schnittstelle simuliert wird und keine echten Geräte genutzt werden müssen die ports über Programme wie com0com mit COM pairs (standard COM 3 + COM 4) geöffnet werden damit eine Verbindung klappt




## Datenbank

Anomalien werden in der Tabelle `anomalies` gespeichert.
**Hinweis:** Die Datenbank `temperaturdb` und die Tabelle werden beim ersten Start **automatisch erstellt**, falls sie noch nicht existieren. Manuelle SQL-Befehle sind nicht notwendig.

Struktur: `id`, `timestamp`, `type` (HIGH/LOW), `temperature`.
Lesbar über phpMyAdmin oder anderes SQL-Tool.

## Nachfragen

bei nachfragen innerhalb der ferien an julienfuhr|AT|outlook|DOT|de
außerhalb einfach standard schulmail
