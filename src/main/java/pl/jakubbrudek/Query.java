package pl.jakubbrudek;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import pl.jakubbrudek.mysql.MySQL;
import pl.jakubbrudek.sqlite.SQLite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Log
public class Query {

    /**
     * Database type
     */
    private MySQL mysql;
    private SQLite sqlite;
    private Object databaseType;

    /**
     * Query details
     */
    private String query;
    private String columnName;

    /**
     * Command details
     */
    private String command;

    /**
     * Constructors
     *
     * @param databaseType - type of database e.g. mysql, sqlite
     */
    @NonNull
    public Query(final Object databaseType, final String query, final String columnName) {
        this.query = query;
        this.columnName = columnName;
        this.databaseType = databaseType;

        if (databaseType instanceof MySQL) {
            mysql = (MySQL) databaseType;
        } else if (databaseType instanceof SQLite) {
            sqlite = (SQLite) databaseType;
        } else {
            throw new NullPointerException("Invalid database type");
        }
    }

    @NonNull
    public Query(final Object databaseType, final String command) {
        this.command = command;
        this.databaseType = databaseType;

        if (databaseType instanceof MySQL) {
            mysql = (MySQL) databaseType;
        } else if (databaseType instanceof SQLite) {
            sqlite = (SQLite) databaseType;
        } else {
            throw new NullPointerException("Invalid database type");
        }
    }

    /**
     * Getters
     */

    private MySQL getMysql() {
        return mysql;
    }

    private SQLite getSqlite() {
        return sqlite;
    }

    private String getQuery() {
        return query;
    }

    private String getColumnName() {
        return columnName;
    }

    private Object getDatabaseType() {
        return databaseType;
    }


    private void checkConnection() {
        if (getDatabaseType() instanceof MySQL) {
            if (getMysql().getConnection() == null || !getMysql().isConnected()) {
                getMysql().connectToDatabase();
            }
        } else if (getDatabaseType() instanceof SQLite) {
            if (getSqlite().getConnection() == null || !getSqlite().isConnected()) {
                getSqlite().connectToDatabase();
            }
        } else {
            throw new NullPointerException("No database found!");
        }
    }

    private Boolean checkVisibleQueries() {
        if (getDatabaseType() instanceof MySQL) {
            return getMysql().getVisibleQuery();
        } else if (getDatabaseType() instanceof SQLite) {
            return getSqlite().getVisibleQuery();
        } else {
            return false;
        }
    }

    private Connection getConnection() {
        if (getDatabaseType() instanceof MySQL) {
            return getMysql().getConnection();
        } else if (getDatabaseType() instanceof SQLite) {
            return getSqlite().getConnection();
        } else {
            throw new NullPointerException("No database found!");
        }
    }

    private void visibleQuery(final Long prev) {
        if (checkVisibleQueries()) {
            log.info(query + " [" + (System.currentTimeMillis() - prev) + "ms]");
        }
    }

    private void visibleCommand(final Long prev) {
        if (checkVisibleQueries()) {
            log.info(command + " [" + (System.currentTimeMillis() - prev) + "ms]");
        }
    }

    @SneakyThrows
    private ResultSet executeQuery() {
        final long prev = System.currentTimeMillis();

        checkConnection();

        final Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);

        visibleQuery(prev);

        return rs;
    }

    @SneakyThrows
    public Object queryObject() {
        ResultSet rs = executeQuery();

        while (rs.next()) {
            return rs.getObject(columnName);
        }
        return null;
    }

    @SneakyThrows
    public List<Object> queryObjects() {
        List<Object> objectList = new ArrayList<>();

        ResultSet rs = executeQuery();

        while (rs.next()) {
            objectList.add(rs.getObject(columnName));
        }
        return objectList;
    }

    @SneakyThrows
    @NonNull
    public void update() {
        final long prev = System.currentTimeMillis();

        checkConnection();

        final Statement statement = getConnection().createStatement();
        statement.executeUpdate(command);

        visibleCommand(prev);
    }
}
