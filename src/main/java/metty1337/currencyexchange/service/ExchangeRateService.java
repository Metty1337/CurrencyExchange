package metty1337.currencyexchange.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import metty1337.currencyexchange.dao.ExchangeRateDAO;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.dto.ExchangeDTO;
import metty1337.currencyexchange.dto.ExchangeRateDTO;
import metty1337.currencyexchange.models.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ExchangeRateService {
    private static final String USD_CODE = "USD";
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

    public ExchangeRateDTO changeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        ExchangeRateDTO exchangeRateDTO = getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
        ExchangeRate exchangeRate = new ExchangeRate(exchangeRateDTO.getId(), exchangeRateDTO.getBaseCurrency().getId(), exchangeRateDTO.getTargetCurrency().getId(), rate);
        exchangeRateDAO.update(exchangeRate);
        return getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public ExchangeDTO exchange(String baseCurrencyCode, String targetCurrencyCode, double amount) {
        CurrencyDTO baseCurrencyDTO = currencyService.getCurrencyByCode(baseCurrencyCode);
        CurrencyDTO targetCurrencyDTO = currencyService.getCurrencyByCode(targetCurrencyCode);
        CurrencyDTO usdCurrencyDTO = currencyService.getCurrencyByCode(USD_CODE);
        int baseCurrencyID = baseCurrencyDTO.getId();
        int targetCurrencyID = targetCurrencyDTO.getId();
        int usdCurrencyID = usdCurrencyDTO.getId();

        double rate = 0;
        double convertedAmount = 0;
        if (isExchangeRateExist(baseCurrencyID, targetCurrencyID)) {
            ExchangeRateDTO exchangeRateDTO = getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
            rate = exchangeRateDTO.getRate();
            convertedAmount = amount * rate;
        } else if (isExchangeRateExist(targetCurrencyID, baseCurrencyID)) {
            ExchangeRateDTO exchangeRateDTO = getExchangeRateByCodes(targetCurrencyCode, baseCurrencyCode);
            rate = 1 / exchangeRateDTO.getRate();
            convertedAmount = amount * rate;
        } else if (isExchangeRateExist(usdCurrencyID, baseCurrencyID) && isExchangeRateExist(usdCurrencyID, targetCurrencyID)) {
            ExchangeRateDTO usdToBaseCurrencyDTO = getExchangeRateByCodes(USD_CODE, baseCurrencyCode);
            ExchangeRateDTO usdToTargetCurrencyDTO = getExchangeRateByCodes(USD_CODE, targetCurrencyCode);
            rate = usdToTargetCurrencyDTO.getRate() / usdToBaseCurrencyDTO.getRate();
            convertedAmount = amount * rate;
        }

        return new ExchangeDTO(baseCurrencyDTO, targetCurrencyDTO, rate, amount, convertedAmount);
    }

    private ExchangeRateDTO getExchangeRateDTO(ExchangeRate exchangeRate) {
        return new ExchangeRateDTO(
                exchangeRate.getID(),
                currencyService.getCurrencyById(exchangeRate.getBaseCurrencyID()),
                currencyService.getCurrencyById(exchangeRate.getTargetCurrencyID()),
                exchangeRate.getRate()
        );
    }

    private boolean isExchangeRateExist(int baseCurrencyID, int targetCurrencyID) {
        return exchangeRateDAO.existsByIDs(baseCurrencyID, targetCurrencyID);
    }
}
