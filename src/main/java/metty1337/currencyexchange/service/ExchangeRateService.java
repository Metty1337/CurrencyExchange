package metty1337.currencyexchange.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.dao.CurrencyDAO;
import metty1337.currencyexchange.dao.ExchangeRateDAO;
import metty1337.currencyexchange.dto.ExchangeRateDTO;
import metty1337.currencyexchange.models.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ExchangeRateService {
    private ExchangeRateDAO exchangeRateDAO;
    private CurrencyService currencyService;

    public List<ExchangeRateDTO> getExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDAO.findAll();
        List<ExchangeRateDTO> exchangeRateDTOs = new ArrayList<>();

        for (ExchangeRate exchangeRate : exchangeRates) {
            exchangeRateDTOs.add(new ExchangeRateDTO(
                    exchangeRate.getID(),
                    currencyService.getCurrencyById(exchangeRate.getBaseCurrencyID()),
                    currencyService.getCurrencyById(exchangeRate.getTargetCurrencyID()),
                    exchangeRate.getRate()
            ));
        }
        return exchangeRateDTOs;
    }
}
