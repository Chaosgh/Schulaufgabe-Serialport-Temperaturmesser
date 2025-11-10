package temperatur.messer.sensor;



import temperatur.messer.common.SerialPortLink;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public final class TemperatureSensorSimulator {
    private static final String PORT_NAME = "COM4";
    private static final int BAUD_RATE = 9600;
    private static final long INTERVAL_MS = 1000;

    public static void main(String[] args) {
        try (SerialPortLink serial = SerialPortLink.open(PORT_NAME, BAUD_RATE)) {
            System.out.println("Sensor aktiv auf " + serial.getPortName());
            while (true) {
                double temp = 22 + ThreadLocalRandom.current().nextDouble(-1.5, 1.5);
                String line = "TEMP;" + System.currentTimeMillis() + ";" + String.format("%.2f", temp);
                serial.writeLine(line);
                System.out.println("→ " + line);
                Thread.sleep(INTERVAL_MS);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
