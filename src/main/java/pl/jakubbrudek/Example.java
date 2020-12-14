package pl.jakubbrudek;

import lombok.extern.java.Log;
import pl.jakubbrudek.mysql.MySQL;
import pl.jakubbrudek.sqlite.SQLite;

@Log
public class Example {
    public static void main(String[] args) {
        MySQL mysql = new MySQL("host", "database", "username", "password", true);
        SQLite sqlite = new SQLite(true);

        Query queryMySQL = new Query(mysql, "SELECT * FROM TEST;", "TEST");

        for(Object o : queryMySQL.queryObjects())
        {
            log.info(String.valueOf(o));
        }

        Query querySQLite = new Query(sqlite, "INSERT INTO TEST VALUES(1912)");
        querySQLite.update();
    }
}
