package temperatur.messer.common;

import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
public final class SerialPortLink implements Closeable {
    private final SerialPort port;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final String portName;

    private SerialPortLink(SerialPort port) {
        this.port = port;
        this.portName = port.getSystemPortName();
        InputStream in = port.getInputStream();
        OutputStream out = port.getOutputStream();
        this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }


    public static SerialPortLink open(String requestedPort, int baudRate) throws IOException {
        SerialPort port = SerialPort.getCommPort(requestedPort);
        port.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        if (!port.openPort()) {
            throw new IOException("Serieller Port " + port.getSystemPortName() + " konnte nicht geoeffnet werden.");
        }
        return new SerialPortLink(port);
    }

    public String getPortName() {
        return portName;
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        try {
            writer.close();
            reader.close();
        } finally {
            if (port.isOpen()) {
                port.closePort();
            }
        }
    }
}
