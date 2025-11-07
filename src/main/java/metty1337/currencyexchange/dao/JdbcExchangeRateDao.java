package metty1337.currencyexchange.dao;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;
import metty1337.currencyexchange.exceptions.ExchangeRateAlreadyExistsException;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
import metty1337.currencyexchange.models.ExchangeRate;
import metty1337.currencyexchange.util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@NoArgsConstructor
public class JdbcExchangeRateDao implements ExchangeRateDao {
    private static final String SELECT_ALL = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates";
    private static final String SELECT_BY_CURRENCY_IDS = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
    private static final String INSERT_INTO = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
    private static final String UPDATE_BY_ID = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";
    private static final String ID = "ID";
    private static final String BASE_CURRENCY_ID = "BaseCurrencyId";
    private static final String TARGET_CURRENCY_ID = "TargetCurrencyId";
    private static final String RATE = "Rate";
    private static final int ERROR_FOR_CONSTRAINT_VIOLATION = 19;

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL)) {

            while (resultSet.next()) {
                ExchangeRate exchangeRate = mapRow(resultSet);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    @Override
    public ExchangeRate findByCurrencyIds(Integer baseCurrencyId, Integer targetCurrencyId) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_CURRENCY_IDS)) {
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

    @Override
    public void save(ExchangeRate exchangeRate) {
        Integer baseCurrencyId = exchangeRate.getBaseCurrencyID();
        Integer targetCurrencyId = exchangeRate.getTargetCurrencyID();
        BigDecimal rate = exchangeRate.getRate();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO)) {
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

    @Override
    public void update(ExchangeRate exchangeRate) {
        Integer Id = exchangeRate.getID();
        BigDecimal rate = exchangeRate.getRate();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BY_ID)) {
            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setInt(2, Id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }

    private ExchangeRate mapRow(ResultSet resultSet) {
        try {
            return new ExchangeRate(
                    resultSet.getInt(ID),
                    resultSet.getInt(BASE_CURRENCY_ID),
                    resultSet.getInt(TARGET_CURRENCY_ID),
                    resultSet.getBigDecimal(RATE)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
