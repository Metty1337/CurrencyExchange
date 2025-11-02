package metty1337.currencyexchange.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;
import metty1337.currencyexchange.exceptions.ExchangeRateAlreadyExistsException;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
import metty1337.currencyexchange.models.ExchangeRate;
import metty1337.currencyexchange.util.DatabaseConnection;
import metty1337.currencyexchange.util.ExchangeRatesColumns;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ExchangeRateDAO {
    private static final String SELECT_ALL_EXCHANGE_RATES = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates";
    private static final String SELECT_EXCHANGE_RATE_BY_CURRENCY_IDS = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
    private static final String INSERT_INTO_EXCHANGE_RATE = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
    private static final String SELECT_EXCHANGE_RATE_BY_ID = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE Id = ?";
    private static final String UPDATE_EXCHANGE_RATE_BY_ID = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";
    private static final String SELECT_COUNT_EXCHANGE_RATES_BY_IDS = "SELECT COUNT(ID) FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
    private static final int ERROR_FOR_CONSTRAINT_VIOLATION = 19;

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_EXCHANGE_RATES)) {

            while (resultSet.next()) {
                ExchangeRate exchangeRate = mapRow(resultSet);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    public ExchangeRate findByCurrencyIds(Integer baseCurrencyId, Integer targetCurrencyId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EXCHANGE_RATE_BY_CURRENCY_IDS);) {
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    throw new ExchangeRateDoesntExistException(ExceptionMessages.EXCHANGE_RATE_DOESNT_EXISTS.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    public void save(ExchangeRate exchangeRate) {
        Integer baseCurrencyId = exchangeRate.getBaseCurrencyID();
        Integer targetCurrencyId = exchangeRate.getTargetCurrencyID();
        BigDecimal rate = exchangeRate.getRate();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_EXCHANGE_RATE);) {
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setBigDecimal(3, rate);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_FOR_CONSTRAINT_VIOLATION) {
                throw new ExchangeRateAlreadyExistsException(ExceptionMessages.EXCHANGE_RATE_ALREADY_EXISTS.getMessage(), e);
            } else {
                throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
            }
        }
    }

    public ExchangeRate findById(Integer id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EXCHANGE_RATE_BY_ID)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    throw new ExchangeRateDoesntExistException(ExceptionMessages.EXCHANGE_RATE_DOESNT_EXISTS.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    public void update(ExchangeRate exchangeRate) {
        Integer Id = exchangeRate.getID();
        BigDecimal rate = exchangeRate.getRate();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE_BY_ID)) {
            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setInt(2, Id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    public boolean existsByIDs(int baseCurrencyId, int targetCurrencyId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COUNT_EXCHANGE_RATES_BY_IDS)) {
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    private ExchangeRate mapRow(ResultSet resultSet) {
        try {
            return new ExchangeRate(
                    resultSet.getInt(ExchangeRatesColumns.ID.getColumnName()),
                    resultSet.getInt(ExchangeRatesColumns.BASE_CURRENCY_ID.getColumnName()),
                    resultSet.getInt(ExchangeRatesColumns.TARGET_CURRENCY_ID.getColumnName()),
                    resultSet.getBigDecimal(ExchangeRatesColumns.RATE.getColumnName())
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
