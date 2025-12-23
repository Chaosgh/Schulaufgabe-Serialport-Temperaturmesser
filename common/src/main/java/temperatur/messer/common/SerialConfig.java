package temperatur.messer.common;

public final class SerialConfig {
    private SerialConfig() {
    }

    public static final String DEFAULT_PORT_SENSOR = "COM4";
    public static final String DEFAULT_PORT_DISPLAY = "COM3";
    public static final int BAUD_RATE = 9600;

    public static final java.time.format.DateTimeFormatter DATE_FORMATTER = java.time.format.DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");
}
