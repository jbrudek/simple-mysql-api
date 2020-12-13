package pl.jakubbrudek.sqlite;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite {

    private Boolean visibleQuery;
    private Connection connection;

    public SQLite(final Boolean visibleQuery) {
        this.visibleQuery = visibleQuery;
        this.connectToDatabase();
    }

    public Boolean getVisibleQuery() {
        return visibleQuery;
    }

    public Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    @SneakyThrows
    public Boolean isConnected() {
        return !getConnection().isClosed();
    }

    @SneakyThrows
    public void connectToDatabase() {
        setConnection(DriverManager.getConnection("jdbc:sqlite:database.db"));
    }

    @SneakyThrows
    public void disconnect() {
        this.getConnection().close();
    }
}
