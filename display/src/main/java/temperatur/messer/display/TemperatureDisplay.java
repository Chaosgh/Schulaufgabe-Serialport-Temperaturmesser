package temperatur.messer.display;

import temperatur.messer.common.SerialConfig;
import temperatur.messer.common.SerialPortLink;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class TemperatureDisplay {

    public static void main(String[] args) {
        String portName = (args.length > 0) ? args[0] : SerialConfig.DEFAULT_PORT_DISPLAY;

        try (SerialPortLink serial = SerialPortLink.open(portName, SerialConfig.BAUD_RATE)) {
            System.out.println("Display hört auf " + serial.getPortName());
            String line;

            while ((line = serial.readLine()) != null) {
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

                    System.out.println("[" + formattedTime + "] Temperatur: " + formattedValue + " °C");

                } catch (NumberFormatException ex) {
                    System.out.println("Fehlerhafte Nachricht: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Öffnen des Ports " + portName + ": " + e.getMessage());
        }
    }
}
