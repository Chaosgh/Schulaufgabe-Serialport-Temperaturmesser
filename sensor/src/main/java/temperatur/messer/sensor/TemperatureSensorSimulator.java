package temperatur.messer.sensor;

import temperatur.messer.common.SerialConfig;
import temperatur.messer.common.SerialPortLink;

import java.util.concurrent.ThreadLocalRandom;

public final class TemperatureSensorSimulator {

    private static final long INTERVAL_MS = 1000;

    public static void main(String[] args) {
        String portName = (args.length > 0) ? args[0] : SerialConfig.DEFAULT_PORT_SENSOR;

        while (true) {
            try (SerialPortLink serial = SerialPortLink.open(portName, SerialConfig.BAUD_RATE)) {
                System.out.println("Sensor aktiv auf " + serial.getPortName());
                double currentTemp = 22.0;

                while (true) {
                    // sowas wie ein wetterevent simuliern
                    if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                        double change = ThreadLocalRandom.current().nextDouble(5.0, 15.0);
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            currentTemp += change;
                        } else {
                            currentTemp -= change;
                        }
                    } else {
                        currentTemp += ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                    }
                    // Begrenze auf realistische Werte (-30 bis +50)
                    currentTemp = Math.max(-30, Math.min(50, currentTemp));

                    String tempStr = String.format(java.util.Locale.US, "%.2f", currentTemp);
                    String line = "TEMP;" + System.currentTimeMillis() + ";" + tempStr;

                    serial.writeLine(line);
                    System.out.println("-> " + line);

                    Thread.sleep(INTERVAL_MS);
                }
            } catch (Exception e) {
                System.err.println("Verbindungsfehler (Sensor): " + e.getMessage());
                System.out.println("Versuche erneute Verbindung in 2 Sekunden...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    break; // Programm beenden bei Interrupt
                }
            }
        }
    }
}
