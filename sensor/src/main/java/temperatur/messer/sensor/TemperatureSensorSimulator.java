package temperatur.messer.sensor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import temperatur.messer.common.SerialPortLink;

public final class TemperatureSensorSimulator {
    private static final String PORT_NAME = "COM2";
    private static final int BAUD_RATE = 9_600;
    private static final long INTERVAL_MS = 1_000L;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TemperatureSensorSimulator() {
        // no instances
    }

    public static void main(String[] args) {
        try (SerialPortLink serial = SerialPortLink.open(PORT_NAME, BAUD_RATE)) {
            System.out.println("Sensor sendet ueber Port " + serial.getPortName() + " @ " + BAUD_RATE + " Baud.");
            while (true) {
                double temperature = createSyntheticTemperature();
                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                String payload = "TEMP;" + timestamp + ";" + formatTemperature(temperature);
                serial.writeLine(payload);
                System.out.println("Gesendet: " + payload);
                Thread.sleep(INTERVAL_MS);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (IOException ex) {
            System.err.println("Sensorfehler: " + ex.getMessage());
        }
    }

    private static double createSyntheticTemperature() {
        double baseTemperature = 22.0;
        double drift = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        double noise = ThreadLocalRandom.current().nextGaussian() * 0.3;
        return baseTemperature + drift + noise;
    }

    private static String formatTemperature(double temperature) {
        return BigDecimal.valueOf(temperature)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}
