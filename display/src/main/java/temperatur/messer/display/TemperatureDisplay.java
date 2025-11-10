package temperatur.messer.display;

import temperatur.messer.common.SerialPortLink;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TemperatureDisplay {

    private static final String PORT_NAME = "COM3";
    private static final int BAUD_RATE = 9600;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        try (SerialPortLink serial = SerialPortLink.open(PORT_NAME, BAUD_RATE)) {
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
                    double value = Double.parseDouble(parts[2].replace(',', '.'));
                    LocalDateTime time = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    String formattedTime = time.format(FORMATTER);
                    String formattedValue = String.format("%.2f", value);

                    System.out.println("[" + formattedTime + "] Temperatur: " + formattedValue + " °C");

                } catch (NumberFormatException ex) {
                    System.out.println("Fehlerhafte Nachricht: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Öffnen des Ports " + PORT_NAME + ": " + e.getMessage());
        }
    }
}
