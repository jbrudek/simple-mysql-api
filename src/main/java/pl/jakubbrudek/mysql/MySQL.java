package pl.jakubbrudek.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.sql.*;

@Log
public class MySQL {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private String host;
    private String database;
    private String username;
    private String password;
    private Boolean visibleQuery;

    @NonNull
    public MySQL(final String host, final String database, final String username, final String password, final Boolean visibleQuery) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.visibleQuery = visibleQuery;

        connectToDatabase();
    }

    public static Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getVisibleQuery() {
        return visibleQuery;
    }

    private void setHikariDataSource(final HikariDataSource hikariDataSource) {
        ds = hikariDataSource;
    }

    public void connectToDatabase() {
        config.setJdbcUrl("jdbc:mysql://" + this.host + ":3306/" + this.database + "?autoReconnect=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        setHikariDataSource(new HikariDataSource(config));
    }

    public Boolean isConnected() {
        try {
            if (getConnection() == null || getConnection().isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void disconnect() {
        try {
            if (isConnected() && getConnection() != null) {
                getConnection().close();
                log.severe("Connection closed");
            } else {
                log.severe("Connection is already terminated");
            }
        } catch (SQLException e) {
            log.severe("An error occurred while disconnecting");
            e.printStackTrace();
        }
    }

    private void checkConnection() {
        if (getConnection() == null || !this.isConnected()) {
            this.connectToDatabase();
        }
    }

    private void visibleQuery(final String query, final Long prev) {
        if (visibleQuery) {
            MySQL.log.info(query + " [" + (System.currentTimeMillis() - prev) + "ms]");
        }
    }
}
