package metty1337.currencyexchange.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.dao.CurrencyDao;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.mapper.CurrencyMapper;
import metty1337.currencyexchange.models.Currency;

import java.util.List;

@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@ApplicationScoped
public class CurrencyService {
    private final CurrencyDao currencyDAO;
    private final CurrencyMapper currencyMapper;

    @Inject
    public CurrencyService(CurrencyDao currencyDAO, CurrencyMapper currencyMapper) {
        this.currencyDAO = currencyDAO;
        this.currencyMapper = currencyMapper;
    }

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
