package metty1337.currencyexchange.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;
import metty1337.currencyexchange.models.ExchangeRate;
import metty1337.currencyexchange.util.DatabaseConnection;
import metty1337.currencyexchange.util.ExchangeRatesColumns;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ExchangeRateDAO {
    private static final String sqlSelectAllExchangeRates = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates";

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlSelectAllExchangeRates)) {

            while (resultSet.next()) {
                ExchangeRate exchangeRate = mapRow(resultSet);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException(ExceptionMessages.DatabaseException.getMessage(), e);
        }
    }

    private ExchangeRate mapRow(ResultSet resultSet) {
        try {
            return new ExchangeRate(
                    resultSet.getInt(ExchangeRatesColumns.ID.getColumnName()),
                    resultSet.getInt(ExchangeRatesColumns.BASE_CURRENCY_ID.getColumnName()),
                    resultSet.getInt(ExchangeRatesColumns.TARGET_CURRENCY_ID.getColumnName()),
                    resultSet.getDouble(ExchangeRatesColumns.RATE.getColumnName())
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
