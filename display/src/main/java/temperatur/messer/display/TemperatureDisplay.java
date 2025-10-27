package temperatur.messer.display;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import temperatur.messer.common.SerialPortLink;

public final class TemperatureDisplay {
    private static final String PORT_NAME = "COM3";
    private static final int BAUD_RATE = 9_600;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TemperatureDisplay() {
        // no instances
    }

    public static void main(String[] args) {
        try (SerialPortLink serial = SerialPortLink.open(PORT_NAME, BAUD_RATE)) {
            String actualPortName = serial.getPortName();
            System.out.println("Display lauscht auf Port " + actualPortName + " @ " + BAUD_RATE + " Baud.");
            String line;
            while ((line = serial.readLine()) != null) {
                handleLine(line);
            }
            System.out.println("Verbindung beendet.");
        } catch (IOException ex) {
            System.err.println("Displayfehler: " + ex.getMessage());
        }
    }

    private static void handleLine(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3 || !"TEMP".equals(parts[0])) {
            System.out.println("Ungueltige Nachricht: " + line);
            return;
        }

        try {
            LocalDateTime timestamp = LocalDateTime.parse(parts[1], TIMESTAMP_FORMAT);
            double value = Double.parseDouble(parts[2].replace(',', '.'));
            String formattedValue = formatTemperature(value);
            System.out.println("[" + timestamp + "] Temperatur: " + formattedValue + " C");
            return;
        } catch (DateTimeParseException | NumberFormatException ex) {
            System.out.println("Fehler beim Auswerten (" + ex.getMessage() + "): " + line);
            return;
        }
    }

    private static String formatTemperature(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}
