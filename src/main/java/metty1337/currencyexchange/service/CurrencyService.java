package metty1337.currencyexchange.service;

import lombok.AllArgsConstructor;
import metty1337.currencyexchange.dao.CurrencyDAO;
import metty1337.currencyexchange.dao.JdbcCurrencyDAO;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.mapper.CurrencyMapper;
import metty1337.currencyexchange.models.Currency;

import java.util.List;

@AllArgsConstructor
public class CurrencyService {
    private final CurrencyDAO currencyDAO;
    private final CurrencyMapper currencyMapper;

    public List<CurrencyDTO> getCurrencies() {
        List<Currency> currencies = currencyDAO.findAll();
        return currencyMapper.toDTO(currencies);
    }

    public CurrencyDTO getCurrencyByCode(String code) {
        Currency currency = currencyDAO.findByCode(code);
        return currencyMapper.toDTO(currency);
    }

    public CurrencyDTO getCurrencyById(int id) {
        Currency currency = currencyDAO.findById(id);
        return currencyMapper.toDTO(currency);
    }

    public void createCurrency(CurrencyDTO currencyDTO) {
        Currency currency = currencyMapper.toModel(currencyDTO);
        currencyDAO.save(currency);
    }
}
