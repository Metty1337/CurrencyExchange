package metty1337.currencyexchange.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionFactory {
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\novik\\IdeaProjects\\CurrencyExchange\\data\\db\\sqlite\\database.db";
    private static final String TURN_FOREIGN_KEYS_ON = "PRAGMA foreign_keys = ON;";

    public static Connection getConnection() {
        try {
            DatabaseMigrator.migrate(DB_URL);
            Connection connection = DriverManager.getConnection(DB_URL);

            try (Statement statement = connection.createStatement()) {
                statement.execute(TURN_FOREIGN_KEYS_ON);
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }
}

