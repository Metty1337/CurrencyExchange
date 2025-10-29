package metty1337.currencyexchange.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.exceptions.DatabaseException;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\novik\\IdeaProjects\\CurrencyExchange\\data\\db\\sqlite\\database.db";
    private static final String DATABASE_ERROR_MESSAGE = "Database connection failed";
    public static Connection getConnection() {
        DatabaseMigrator.migrate(DB_URL);
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new DatabaseException(DATABASE_ERROR_MESSAGE, e);
        }
    }
}

