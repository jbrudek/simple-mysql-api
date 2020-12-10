package pl.jakubbrudek.mysql;

import lombok.NonNull;
import lombok.extern.java.Log;

import java.sql.*;

@Log
public class MySQL {

    /**
     * Connection details
     */

    private String host;
    private String database;
    private String username;
    private String password;
    private Boolean visibleQuery;
    private Connection connection;

    public MySQL(final String host, final String database, final String username, final String password, final Boolean visibleQuery) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.visibleQuery = visibleQuery;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.severe("Driver not found!");
            e.printStackTrace();
        }
        String url = "jdbc:mysql://" + this.host + ":3306/" + this.database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            setConnection(DriverManager.getConnection(url, "srv117323", "pZCCzNc9"));
            log.info("Connected!");
        } catch (SQLException e) {
            log.severe("Error with connection!");
            e.printStackTrace();
        }
    }

    private Boolean isConnected() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void disconnect() {
        try {
            if (isConnected() && this.connection != null) {
                this.connection.close();
                log.severe("Connection closed");
            } else {
                log.severe("Connection is already terminated");
            }
        } catch (SQLException e) {
            log.severe("An error occurred while disconnecting");
            e.printStackTrace();
        }
    }

    public ResultSet query(final String query) {
        final long prev = System.currentTimeMillis();

        try {
            if (this.connection == null || !isConnected()) {
                this.connectToDatabase();
            }
            if (visibleQuery) {
                log.info(query + " [" + (System.currentTimeMillis() - prev) + "ms]");
            }
            final Statement statement = this.connection.createStatement();
            statement.executeQuery(query);
            return statement.getResultSet();
        } catch (SQLException e) {
            log.severe("An error occurred while executing query");
            e.printStackTrace();
            return null;
        }
    }

    public void update(final String command) {
        final long prev = System.currentTimeMillis();

        try {
            if (!isConnected()) {
                this.connectToDatabase();
            }
            if (visibleQuery) {
                log.info(command + " [" + (System.currentTimeMillis() - prev) + "ms]");
            }
            final Statement statement = this.connection.createStatement();
            statement.executeUpdate(command);
        } catch (SQLException e) {
            log.severe("An error occurred while executing the command");
            e.printStackTrace();
        }
    }
}
