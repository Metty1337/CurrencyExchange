package metty1337.currencyexchange.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseManager {
    private static final String URL_OF_DB = "jdbc:sqlite:src/main/data/db/sqlite/database.db";
    private static final String URL_OF_SQL_SCHEMA = "/sqlscripts/init_schema.sql";


    public static void init() {
        try (Connection connection = DriverManager.getConnection(URL_OF_DB)) {
            String SQLScript = getSQLScript(URL_OF_SQL_SCHEMA);
            Statement statement = connection.createStatement();
            turnForeignKeysOn(statement);
            executeSQLScript(statement, SQLScript);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void turnForeignKeysOn(Statement statement) {
        String TURN_FOREIGN_KEYS_SQL = "PRAGMA foreign_keys = ON";
        try {
            statement.execute(TURN_FOREIGN_KEYS_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream readResourceAsStream(String URL) {
        return DatabaseManager.class.getResourceAsStream(URL);
    }

    private static String getSQLScript(String URL) {
        try (InputStream inputStream = readResourceAsStream(URL)) {
            return new String(Objects.requireNonNull(inputStream).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeSQLScript(Statement statement, String SQLScript) {
        String[] statements = SQLScript.split(";");
        for (String s : statements) {
            if (!s.trim().isEmpty()) {
                try {
                    statement.execute(s.trim());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
