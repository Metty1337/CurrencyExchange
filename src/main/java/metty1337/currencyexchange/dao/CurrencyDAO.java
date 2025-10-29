package metty1337.currencyexchange.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.exceptions.CurrencyAlreadyExists;
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
public class CurrencyDAO {
    private static final String sqlSelectAllCurrencies = "SELECT ID, Code, FullName, Sign FROM Currencies";
    private static final String sqlSelectCurrencyByCode = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE Code = ?";
    private static final String sqlInsertIntoCurrencies = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
    private static final int ErrorForConstraintViolation = 19;

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlSelectAllCurrencies)) {

            while (resultSet.next()) {
                Currency currency = mapRow(resultSet);
                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DatabaseException.getMessage(), e);
        }
    }

    public Currency findByCode(String code) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectCurrencyByCode)) {

            preparedStatement.setString(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    throw new CurrencyDoesntExistException(ExceptionMessages.CurrencyDoesntExistException.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DatabaseException.getMessage(), e);
        }
    }

    public void save(Currency currency) {
        String code = currency.getCode();
        String fullName = currency.getFullName();
        String sign = currency.getSign();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertIntoCurrencies)) {

            preparedStatement.setString(1, code);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, sign);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == ErrorForConstraintViolation) {
                throw new CurrencyAlreadyExists(ExceptionMessages.CurrencyAlreadyExists.getMessage(), e);
            } else {
                throw new DatabaseException(ExceptionMessages.DatabaseException.getMessage(), e);
            }
        }

    }

    private Currency mapRow(ResultSet resultSet) {
        try {
            return new Currency(
                    resultSet.getInt(CurrenciesColumns.ID),
                    resultSet.getString(CurrenciesColumns.CODE),
                    resultSet.getString(CurrenciesColumns.NAME),
                    resultSet.getString(CurrenciesColumns.SIGN)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
