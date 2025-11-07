package metty1337.currencyexchange.dao;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.exceptions.CurrencyAlreadyExistsException;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;
import metty1337.currencyexchange.models.Currency;
import metty1337.currencyexchange.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@NoArgsConstructor
public class JdbcCurrencyDao implements CurrencyDao {
    private static final String SELECT_ALL = "SELECT ID, Code, FullName, Sign FROM Currencies";
    private static final String SELECT_BY_CODE = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE Code = ?";
    private static final String SELECT_BY_ID = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE ID = ?";
    private static final String INSERT_INTO = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
    private static final String ID = "ID";
    private static final String NAME = "FullName";
    private static final String SIGN = "Sign";
    private static final String CODE = "Code";
    private static final int ERROR_FOR_CONSTRAINT_VIOLATION = 19;

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
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
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_CODE)) {

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
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID)) {

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

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO)) {

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
                    resultSet.getInt(ID),
                    resultSet.getString(CODE),
                    resultSet.getString(NAME),
                    resultSet.getString(SIGN)
            );
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }
}
