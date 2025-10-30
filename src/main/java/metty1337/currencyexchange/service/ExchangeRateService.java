package metty1337.currencyexchange.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.dao.ExchangeRateDAO;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.dto.ExchangeRateDTO;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
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
            exchangeRateDTOs.add(getExchangeRateDTO(exchangeRate));
        }
        return exchangeRateDTOs;
    }

    public ExchangeRateDTO getExchangeRateByCodes(String baseCurrencyCode, String targetCurrencyCode) {
            CurrencyDTO baseCurrencyDTO = currencyService.getCurrencyByCode(baseCurrencyCode);
            CurrencyDTO targetCurrencyDTO = currencyService.getCurrencyByCode(targetCurrencyCode);

            Integer baseCurrencyID = baseCurrencyDTO.getId();
            Integer targetCurrencyID = targetCurrencyDTO.getId();

            ExchangeRate exchangeRate = exchangeRateDAO.findByCurrencyIds(baseCurrencyID, targetCurrencyID);

            return getExchangeRateDTO(exchangeRate);
    }

    public ExchangeRateDTO createExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
            CurrencyDTO baseCurrencyDTO = currencyService.getCurrencyByCode(baseCurrencyCode);
            CurrencyDTO targetCurrencyDTO = currencyService.getCurrencyByCode(targetCurrencyCode);
            ExchangeRate exchangeRate = new ExchangeRate(null, baseCurrencyDTO.getId(), targetCurrencyDTO.getId(), rate);
            exchangeRateDAO.save(exchangeRate);
            return getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
    }

    private ExchangeRateDTO getExchangeRateDTO(ExchangeRate exchangeRate) {
        return new ExchangeRateDTO(
                exchangeRate.getID(),
                currencyService.getCurrencyById(exchangeRate.getBaseCurrencyID()),
                currencyService.getCurrencyById(exchangeRate.getTargetCurrencyID()),
                exchangeRate.getRate()
        );
    }
}
