package temperatur.messer.common;

import com.fazecast.jSerialComm.SerialPort;
import java.io.*;
import java.nio.charset.StandardCharsets;

public final class SerialPortLink implements Closeable {
    private final SerialPort port;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    private SerialPortLink(SerialPort port) {
        this.port = port;
        this.reader = new BufferedReader(new InputStreamReader(port.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(port.getOutputStream(), StandardCharsets.UTF_8));
    }

    public static SerialPortLink open(String name, int baud) throws IOException {
        SerialPort port = SerialPort.getCommPort(name);
        port.setComPortParameters(baud, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        if (!port.openPort()) throw new IOException("Port konnte nicht geöffnet werden: " + name);
        return new SerialPortLink(port);
    }

    public String getPortName() {
        return port.getSystemPortName();
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public void writeLine(String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            reader.close();
            writer.close();
        } finally {
            if (port.isOpen()) port.closePort();
        }
    }
}
