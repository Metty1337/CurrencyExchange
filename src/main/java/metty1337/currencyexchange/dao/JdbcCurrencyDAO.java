package metty1337.currencyexchange.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.exceptions.CurrencyAlreadyExistsException;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;
import metty1337.currencyexchange.models.Currency;
import metty1337.currencyexchange.util.CurrenciesColumns;
import metty1337.currencyexchange.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class JdbcCurrencyDAO implements CurrencyDAO {
    private static final String SELECT_ALL_CURRENCIES = "SELECT ID, Code, FullName, Sign FROM Currencies";
    private static final String SELECT_CURRENCY_BY_CODE = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE Code = ?";
    private static final String SELECT_CURRENCY_BY_ID = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE ID = ?";
    private static final String INSERT_INTO_CURRENCIES = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
    private static final int ERROR_FOR_CONSTRAINT_VIOLATION = 19;

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_CURRENCIES)) {

            while (resultSet.next()) {
                Currency currency = mapRow(resultSet);
                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    @Override
    public Currency findByCode(String code) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CURRENCY_BY_CODE)) {

            preparedStatement.setString(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    throw new CurrencyDoesntExistException(ExceptionMessages.CURRENCY_DOESNT_EXIST_EXCEPTION.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    @Override
    public Currency findById(int id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CURRENCY_BY_ID)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    throw new CurrencyDoesntExistException(ExceptionMessages.CURRENCY_DOESNT_EXIST_EXCEPTION.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    @Override
    public void save(Currency currency) {
        String code = currency.getCode();
        String fullName = currency.getFullName();
        String sign = currency.getSign();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_CURRENCIES)) {

            preparedStatement.setString(1, code);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, sign);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_FOR_CONSTRAINT_VIOLATION) {
                throw new CurrencyAlreadyExistsException(ExceptionMessages.CURRENCY_ALREADY_EXISTS.getMessage(), e);
            } else {
                throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
            }
        }

    }

    private Currency mapRow(ResultSet resultSet) {
        try {
            return new Currency(
                    resultSet.getInt(CurrenciesColumns.ID.getColumnName()),
                    resultSet.getString(CurrenciesColumns.CODE.getColumnName()),
                    resultSet.getString(CurrenciesColumns.NAME.getColumnName()),
                    resultSet.getString(CurrenciesColumns.SIGN.getColumnName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
