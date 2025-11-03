package metty1337.currencyexchange.dao;

import metty1337.currencyexchange.models.Currency;

import java.util.List;

public interface CurrencyDAO {
    List<Currency> findAll();
    Currency findByCode(String code);
    Currency findById(int id);
    void save(Currency currency);
}
