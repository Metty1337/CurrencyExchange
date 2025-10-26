package metty1337.currencyexchange.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.models.Currency;
import metty1337.currencyexchange.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CurrencyDAO {

    public List<Currency> findAll() {
        String sqlSelectAllCurrencies = "SELECT ID, Code, FullName, Sign FROM Currencies";
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
            throw new RuntimeException(e);
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
