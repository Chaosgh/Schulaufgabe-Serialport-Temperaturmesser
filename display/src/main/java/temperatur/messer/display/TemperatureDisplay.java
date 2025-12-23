package temperatur.messer.display;

import temperatur.messer.common.SerialConfig;
import temperatur.messer.common.SerialPortLink;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Scanner;

public final class TemperatureDisplay {

    public static void main(String[] args) {
        String portName = (args.length > 0) ? args[0] : SerialConfig.DEFAULT_PORT_DISPLAY;

        // JDBC-URL abfragen macht save niemand weil wer benutzt nicht localhost lol
        Scanner scanner = new Scanner(System.in);
        System.out.println("Geben Sie die JDBC-URL ein (Leerlassen für Default):");
        System.out.println("Default: jdbc:mysql://localhost:3306/temperaturdb?useSSL=false&serverTimezone=UTC");
        String inputUrl = scanner.nextLine().trim();

        // Datenbank initialisieren
        AnomalyDatabase db;
        if (inputUrl.isEmpty()) {
            System.out.println("Nutze Default-Datenbank.");
            db = new AnomalyDatabase();
        } else {
            System.out.println("Nutze Custom-Datenbank: " + inputUrl);
            db = new AnomalyDatabase(inputUrl);
        }

        while (true) {
            try (SerialPortLink serial = SerialPortLink.open(portName, SerialConfig.BAUD_RATE)) {
                System.out.println("Display hört auf " + serial.getPortName());
                String line;

                while ((line = serial.readLine()) != null) {
                    line = line.trim();

                    String[] parts = line.split(";");
                    if (parts.length != 3 || !"TEMP".equals(parts[0])) {
                        System.out.println("Ungültige Nachricht: " + line);
                        continue;
                    }

                    try {
                        long millis = Long.parseLong(parts[1]);
                        double value = Double.parseDouble(parts[2]);
                        LocalDateTime time = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        String formattedTime = time.format(SerialConfig.DATE_FORMATTER);
                        String formattedValue = String.format("%.2f", value);

                        System.out.print("[" + formattedTime + "] Temperatur: " + formattedValue + " °C");

                        // Anomalie-Check
                        if (value > 30.0) {
                            System.out.println(" HITZEALARM!");
                            db.saveAnomaly("HIGH", value);
                        } else if (value < 10.0) {
                            System.out.println(" FROSTGEFAHR!");
                            db.saveAnomaly("LOW", value);
                        } else {
                            System.out.println();
                        }

                    } catch (NumberFormatException ex) {
                        System.out.println("Fehlerhafte Nachricht: " + line);
                    }
                }

            } catch (Exception e) {
                System.err.println("Verbindungsfehler (Display) auf " + portName + ": " + e.getMessage());
                System.out.println("Warte auf Verbindung (Reconnect in 2s)...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }
}
