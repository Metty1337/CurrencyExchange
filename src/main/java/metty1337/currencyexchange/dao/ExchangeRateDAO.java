package metty1337.currencyexchange.dao;

import metty1337.currencyexchange.models.ExchangeRate;

import java.util.List;

public interface ExchangeRateDAO {
    List<ExchangeRate> findAll();
    ExchangeRate findByCurrencyIds(Integer baseCurrencyId, Integer targetCurrencyId);
    void save(ExchangeRate exchangeRate);
    void update(ExchangeRate exchangeRate);
}
