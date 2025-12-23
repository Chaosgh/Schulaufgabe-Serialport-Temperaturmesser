package temperatur.messer.display;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnomalyDatabase {
    private static final String DEFAULT_SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/temperaturdb?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASS = "";

    private final String currentDbUrl;

    public AnomalyDatabase() {
        this(DEFAULT_DB_URL);
        createDatabaseIfNotExists();
        initTable();
    }

    public AnomalyDatabase(String customUrl) {
        this.currentDbUrl = customUrl;
        initTable();
    }

    private void initTable() {
        try (Connection conn = DriverManager.getConnection(currentDbUrl, USER, PASS);
                Statement stmt = conn.createStatement()) {
            // rentnerfreundlich
            String sql = """
                        CREATE TABLE IF NOT EXISTS anomalies (
                            id INTEGER PRIMARY KEY AUTO_INCREMENT,
                            timestamp DATETIME NOT NULL,
                            type VARCHAR(10) NOT NULL,
                            temperature DOUBLE NOT NULL
                        )
                    """;
            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Fehler beim Initialisieren der Tabellen (" + currentDbUrl + "): " + e.getMessage());
        }
    }

    private void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DEFAULT_SERVER_URL, USER, PASS);
                Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS temperaturdb");
        } catch (SQLException e) {
            System.err.println("Kritischer Fehler: Konnte Datenbank nicht erstellen! " + e.getMessage());
        }
    }

    public void saveAnomaly(String type, double temperature) {
        String insertSql = "INSERT INTO anomalies(timestamp, type, temperature) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(currentDbUrl, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            // MySQL DATETIME erwartet yyyy-MM-dd HH:mm:ss
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pstmt.setString(1, timestamp);
            pstmt.setString(2, type);
            pstmt.setDouble(3, temperature);

            pstmt.executeUpdate();
            System.out.println("[DB] Anomalie gespeichert: " + type + " (" + temperature + " Grad Celsius)");

        } catch (SQLException e) {
            System.err.println("Fehler beim Speichern der Anomalie: " + e.getMessage());
        }
    }
}
